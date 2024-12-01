package com.example.demo.mq;

import com.example.demo.dto.OrderMessage;
import com.example.demo.exception.specific.InsufficientStockException;
import com.example.demo.exception.specific.ProductNotFoundException;
import com.example.demo.exception.specific.UserNotFoundException;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * 订单消费者，用于处理来自 RabbitMQ 的订单消息
 */
@Component
public class OrderConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * 处理来自 "orderQueue" 队列的订单消息
     *
     * @param orderMessage 接收到的订单消息
     */
    @Transactional
    @RabbitListener(queues = "orderQueue")
    public void handleOrderMessage(OrderMessage orderMessage) {
        try {
            Long userId = orderMessage.getUserId();
            Long productId = orderMessage.getProductId();
            Integer quantity = orderMessage.getQuantity();

            logger.info("接收到订单消息 - 用户ID: {}, 产品ID: {}, 数量: {}", userId, productId, quantity);

            // 获取用户
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("用户不存在，ID: " + userId));

            // 获取产品
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("产品不存在，ID: " + productId));

            // 检查库存
            if (product.getStock() < quantity) {
                throw new InsufficientStockException("产品库存不足，产品ID: " + productId + ", 可用库存: " + product.getStock() + ", 需求数量: " + quantity);
            }

            // 扣减库存
            product.setStock(product.getStock() - quantity);
            productRepository.save(product); // 更新库存

            // 创建订单
            Order order = new Order();
            order.setUser(user);
            // 状态和时间由实体类的生命周期回调自动设置

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getSalePrice());
            // totalPrice 由生命周期回调自动计算

            Set<OrderItem> orderItems = new HashSet<>();
            orderItems.add(orderItem);
            order.setOrderItems(orderItems);

            // 计算订单总金额
            BigDecimal totalAmount = product.getSalePrice().multiply(new BigDecimal(quantity));
            order.setTotalAmount(totalAmount);

            // 保存订单（同时保存订单项）
            Order savedOrder = orderRepository.save(order);

            // 日志记录
            logger.info("订单创建成功，订单ID：{}", savedOrder.getId());
        } catch (InsufficientStockException e) {
            logger.error("处理订单消息时库存不足：{}", e.getMessage());
            // 根据业务需求决定是否重新抛出异常以触发消息重试
            throw e;
        } catch (UserNotFoundException | ProductNotFoundException e) {
            logger.error("处理订单消息时发生异常：{}", e.getMessage());
            // 根据业务需求决定是否重新抛出异常
            throw e;
        } catch (Exception e) {
            logger.error("处理订单消息时发生未预期的异常：{}", e.getMessage(), e);
            // 根据业务需求决定是否重新抛出异常
            throw new RuntimeException("处理订单消息时发生未预期的异常", e);
        }
    }
}
