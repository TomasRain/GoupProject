package com.example.demo.service;

import com.example.demo.dto.OrderDTO;
import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public OrderDTO createOrder(Long userId, Long productId, Integer quantity) {
        // 获取 User 对象
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 获取 Product 对象
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 检查库存是否足够
        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        // 扣减库存
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        // 创建订单
        Order order = new Order();
        order.setUser(user);
        order.setProduct(product);
        order.setQuantity(quantity);
        // orderTime 由 @CreationTimestamp 自动生成，无需设置

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        // 创建并返回 OrderDTO
        return new OrderDTO(
                savedOrder.getId(),
                savedOrder.getProduct().getName(),
                savedOrder.getUser().getUsername(),
                savedOrder.getOrderTime(),
                savedOrder.getQuantity()
        );
    }
}
