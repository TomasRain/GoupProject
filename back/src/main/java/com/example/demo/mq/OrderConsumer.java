package com.example.demo.mq;

import com.example.demo.dto.OrderMessage;
import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // 修改导入路径
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class OrderConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    @RabbitListener(queues = "orderQueue")
    public void handleOrderMessage(OrderMessage orderMessage) {
        try {
            Long userId = orderMessage.getUserId();
            Long productId = orderMessage.getProductId();

            // 获取用户和商品
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("商品不存在"));

            // 再次检查库存，防止库存不足
            if (product.getStock() <= 0) {
                logger.warn("库存不足，无法创建订单。商品ID：" + productId);
                // 库存不足，处理失败情况（可选）
                return;
            }

            // 扣减数据库中的库存
            product.setStock(product.getStock() - 1);
            productRepository.save(product);

            // 创建订单
            Order order = new Order();
            order.setUser(user);
            order.setProduct(product);
            order.setQuantity(1);
            // orderTime 由 @CreationTimestamp 自动生成

            // 保存订单
            orderRepository.save(order);

            // 日志记录
            logger.info("订单创建成功，订单ID：" + order.getId());
        } catch (Exception e) {
            // 异常处理
            logger.error("处理订单消息时发生异常", e);
            // 根据需要，重新抛出异常，或者进行补偿处理
            throw e; // 或者根据业务需求决定是否抛出
        }
    }
}
