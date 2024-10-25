package com.example.demo.controller;

import com.example.demo.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seckill")
public class RedisController {

    @Autowired
    public SeckillService seckillService;//not safe

    // 秒杀接口：用户发起秒杀请求
    @PostMapping("/execute")
    public String seckill(@RequestParam Long productId, @RequestParam Long userId) {
        return seckillService.seckill(productId, userId);
    }

    // 初始化商品库存接口
    @PostMapping("/initStock")
    public String initStock(@RequestParam Long productId, @RequestParam Integer stock) {
        seckillService.initStock(productId, stock);
        return "商品库存已初始化，商品ID：" + productId + "，库存：" + stock;
    }

    // 查询商品库存接口
    @GetMapping("/stock")
    public String getStock(@RequestParam Long productId) {
        Integer stock = seckillService.getStock(productId);
        if (stock == null) {
            return "库存信息不可用，商品ID：" + productId;
        }
        return "商品ID：" + productId + "，剩余库存：" + stock;
    }

    // 查询用户秒杀状态接口
    @GetMapping("/status")
    public String hasUserPurchased(@RequestParam Long productId, @RequestParam Long userId) {
        Boolean hasPurchased = seckillService.hasUserPurchased(productId, userId);
        if (hasPurchased != null && hasPurchased) {
            return "用户ID：" + userId + " 已经秒杀了商品ID：" + productId;
        }
        return "用户ID：" + userId + " 尚未秒杀商品ID：" + productId;
    }
}
