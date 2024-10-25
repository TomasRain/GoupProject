package com.example.demo.controller;

import com.example.demo.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seckill")
public class SeckillController {

    @Autowired
    public SeckillService seckillService;//not safe

    // 初始化商品库存到 Redis
    @PostMapping("/initStock/{productId}/{stock}")
    public String initStock(@PathVariable Long productId, @PathVariable Integer stock) {
        seckillService.initStock(productId, stock);
        return "商品库存初始化成功";
    }

    // 秒杀接口，用户发起秒杀请求
    @PostMapping("/execute/{productId}/{userId}")
    public String executeSeckill(@PathVariable Long productId, @PathVariable Long userId) {
        return seckillService.seckill(productId, userId);
    }
}


