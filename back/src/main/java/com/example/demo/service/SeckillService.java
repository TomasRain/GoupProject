package com.example.demo.service;

import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class SeckillService {

    @Autowired
    public ProductRepository productRepository;

    @Autowired
    public OrderRepository orderRepository;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    public static final String STOCK_PREFIX = "seckill:stock:";
    public static final String USER_PURCHASED_KEY_PREFIX = "seckill:user:";//not safe

    // 秒杀逻辑：扣减库存，生成订单
    public synchronized String seckill(Long productId, Long userId) {
        // 检查用户是否已经参与过秒杀，防止重复购买
        String userKey = USER_PURCHASED_KEY_PREFIX + userId + ":product:" + productId;
        Boolean hasPurchased = redisTemplate.hasKey(userKey);
        if (hasPurchased != null && hasPurchased) {
            return "您已经秒杀成功，无法重复购买";
        }

        // 检查 Redis 缓存中的库存
        Integer stock = (Integer) redisTemplate.opsForValue().get(STOCK_PREFIX + productId);
        if (stock == null || stock <= 0) {
            return "商品库存不足";
        }

        // 预减库存，避免超卖
        redisTemplate.opsForValue().decrement(STOCK_PREFIX + productId);

        // 检查数据库中该商品是否存在
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("商品不存在"));

        // 创建订单
        Order order = new Order();
        order.setProductId(productId);
        order.setUserId(userId);
        order.setQuantity(1);
        order.setOrderTime(new Date());

        // 保存订单
        orderRepository.save(order);

        // 记录用户的秒杀状态，防止重复秒杀（记录10分钟有效）
        redisTemplate.opsForValue().set(userKey, true, 10, TimeUnit.MINUTES);

        // 返回秒杀成功信息
        return "秒杀成功，订单ID：" + order.getId();
    }

    // 初始化库存到 Redis
    public void initStock(Long productId, Integer stock) {
        redisTemplate.opsForValue().set(STOCK_PREFIX + productId, stock);
    }

    // 新增方法：查询 Redis 中的库存
    public Integer getStock(Long productId) {
        return (Integer) redisTemplate.opsForValue().get(STOCK_PREFIX + productId);
    }

    // 新增方法：从 Redis 中查询用户是否已经秒杀
    public Boolean hasUserPurchased(Long productId, Long userId) {
        String userKey = USER_PURCHASED_KEY_PREFIX + userId + ":product:" + productId;
        return redisTemplate.hasKey(userKey);
    }
}
