package com.example.demo.service;

import com.example.demo.dto.OrderMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@Service
public class SeckillService {

    private static final Logger logger = LoggerFactory.getLogger(SeckillService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public static final String STOCK_PREFIX = "seckill:stock:";
    public static final String USER_PURCHASED_SET_PREFIX = "seckill:userpurchased:";

    // Lua 脚本，将库存检查和扣减原子化
    private static final DefaultRedisScript<Long> STOCK_LUA_SCRIPT;

    static {
        STOCK_LUA_SCRIPT = new DefaultRedisScript<>();
        STOCK_LUA_SCRIPT.setScriptText(
                "local stock = tonumber(redis.call('GET', KEYS[1]));" +
                "if stock <= 0 then return -1;" +
                "else redis.call('DECR', KEYS[1]); return stock - 1; end"
        );
        STOCK_LUA_SCRIPT.setResultType(Long.class);
    }

    /**
     * 秒杀逻辑：扣减库存，发送订单创建消息
     *
     * @param productId 产品ID
     * @param userId    用户ID
     * @return 秒杀结果消息
     */
    public String seckill(Long productId, Long userId) {
        if (productId == null || userId == null) {
            return "参数错误";
        }

        String stockKey = STOCK_PREFIX + productId;
        String userKey = USER_PURCHASED_SET_PREFIX + productId;

        try {
            // 先判断用户是否已经购买过，使用 Redis 的 Set
            Boolean isMember = redisTemplate.opsForSet().isMember(userKey, userId);
            if (Boolean.TRUE.equals(isMember)) {
                return "您已经秒杀成功，无法重复购买";
            }

            // 使用 DefaultRedisScript 原子性地检查和扣减库存
            Long stock = redisTemplate.execute(STOCK_LUA_SCRIPT, Collections.singletonList(stockKey));

            if (stock == null || stock < 0) {
                return "商品库存不足";
            }

            // 将用户加入已购买的集合
            redisTemplate.opsForSet().add(userKey, userId);

            // 发送订单创建消息到消息队列
            OrderMessage orderMessage = new OrderMessage(userId, productId, 1); // 假设每次秒杀购买1件
            rabbitTemplate.convertAndSend("orderExchange", "orderRoutingKey", orderMessage);

            return "秒杀成功，正在生成订单";
        } catch (Exception e) {
            logger.error("秒杀过程发生异常", e);
            return "秒杀失败，系统错误";
        }
    }

    // 初始化库存到 Redis
    public void initStock(Long productId, Integer stock) {
        redisTemplate.opsForValue().set(STOCK_PREFIX + productId, stock);
        // 初始化已购买用户的集合
        redisTemplate.delete(USER_PURCHASED_SET_PREFIX + productId);
    }

    // 新增方法：查询 Redis 中的库存
    public Integer getStock(Long productId) {
        Object stock = redisTemplate.opsForValue().get(STOCK_PREFIX + productId);
        return stock != null ? Integer.parseInt(stock.toString()) : null;
    }

    // 新增方法：从 Redis 中查询用户是否已经秒杀
    public Boolean hasUserPurchased(Long productId, Long userId) {
        String userKey = USER_PURCHASED_SET_PREFIX + productId;
        return redisTemplate.opsForSet().isMember(userKey, userId);
    }
}
