package com.example.demo.controller;

import com.example.demo.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/seckill")
public class SeckillController {

    private final SeckillService seckillService;

    // 构造器注入
    @Autowired
    public SeckillController(SeckillService seckillService) {
        this.seckillService = seckillService;
    }

    /**
     * 初始化商品库存到 Redis
     *
     * @param productId 商品ID
     * @param stock     库存数量
     * @return 初始化结果消息
     */
    @PostMapping("/initStock/{productId}/{stock}")
    public ResponseEntity<Map<String, String>> initStock(@PathVariable Long productId, @PathVariable Integer stock) {
        seckillService.initStock(productId, stock);
        Map<String, String> response = new HashMap<>();
        response.put("message", "商品库存初始化成功，商品ID：" + productId + "，库存：" + stock);
        return ResponseEntity.ok(response);
    }

    /**
     * 秒杀接口，用户发起秒杀请求
     *
     * @param productId 产品ID
     * @param userId    用户ID
     * @return 秒杀结果消息
     */
    @PostMapping("/execute/{productId}/{userId}")
    public ResponseEntity<Map<String, String>> executeSeckill(@PathVariable Long productId, @PathVariable Long userId) {
        String result = seckillService.seckill(productId, userId);
        Map<String, String> response = new HashMap<>();
        if ("秒杀成功，正在生成订单".equals(result)) {
            response.put("message", result);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", result);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 查询商品库存接口
     *
     * @param productId 商品ID
     * @return 库存信息
     */
    @GetMapping("/stock")
    public ResponseEntity<Map<String, Object>> getStock(@RequestParam Long productId) {
        Integer stock = seckillService.getStock(productId);
        Map<String, Object> response = new HashMap<>();
        if (stock == null) {
            response.put("message", "库存信息不可用，商品ID：" + productId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("productId", productId);
        response.put("stock", stock);
        return ResponseEntity.ok(response);
    }

    /**
     * 查询用户秒杀状态接口
     *
     * @param productId 商品ID
     * @param userId    用户ID
     * @return 用户秒杀状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> hasUserPurchased(@RequestParam Long productId, @RequestParam Long userId) {
        Boolean hasPurchased = seckillService.hasUserPurchased(productId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("productId", productId);
        response.put("userId", userId);
        response.put("hasPurchased", hasPurchased != null && hasPurchased);
        return ResponseEntity.ok(response);
    }
}
