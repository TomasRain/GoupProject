package com.example.demo.service.impl;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderItemDTO;
import com.example.demo.exception.specific.InsufficientStockException;
import com.example.demo.exception.specific.OrderNotFoundException;
import com.example.demo.exception.specific.ProductNotFoundException;
import com.example.demo.exception.specific.UserNotFoundException;
import com.example.demo.model.Order;
import com.example.demo.model.Order.OrderStatus;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;


import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository; // 添加 UserRepository

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        // 获取用户
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("用户未找到，ID: " + orderDTO.getUserId()));
        
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        Set<OrderItem> orderItems = new HashSet<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("产品未找到，ID: " + itemDTO.getProductId()));
            
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new InsufficientStockException("库存不足，无法购买产品：" + product.getName());
            }

            // 扣减库存
            product.setStock(product.getStock() - itemDTO.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(product.getSalePrice());
            // totalPrice 由实体类自动计算

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getTotalPrice());
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("订单未找到，ID: " + id));
        return convertToDTO(order);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("订单未找到，ID: " + id));
        // 更新订单状态等字段
        order.setStatus(OrderStatus.valueOf(orderDTO.getStatus()));
        // 更新其他字段如需要

        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("订单未找到，ID: " + id));
        // 还原库存
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
        orderRepository.delete(order);
    }

    @Override
    @Transactional
    public void buyProduct(Long productId, int quantity) {
        // 从 SecurityContextHolder 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("当前未认证，无法下单");
        }

        // principal 此时是 userId（Long 类型）
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long)) {
            throw new RuntimeException("无法从认证信息中获取用户ID");
        }

        Long currentUserId = (Long) principal;

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("用户未找到，ID: " + currentUserId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("产品未找到，ID: " + productId));

        if (product.getStock() < quantity) {
            throw new InsufficientStockException("库存不足，无法购买此数量的产品。");
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getSalePrice());

        Set<OrderItem> items = new HashSet<>();
        items.add(orderItem);
        order.setOrderItems(items);

        BigDecimal totalAmount = product.getSalePrice().multiply(BigDecimal.valueOf(quantity));
        order.setTotalAmount(totalAmount);

        orderRepository.save(order);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        Set<OrderItemDTO> items = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    itemDTO.setProductId(item.getProduct().getId());
                    itemDTO.setProductName(item.getProduct().getName());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPrice(item.getPrice());
                    itemDTO.setTotalPrice(item.getTotalPrice());
                    itemDTO.setImageUrl(item.getProduct().getImageUrl()); // 从 product 中获取图片URL
                    return itemDTO;
                })
                .collect(Collectors.toSet());
        dto.setOrderItems(items);
        return dto;
    }
}
