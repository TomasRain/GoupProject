package com.example.demo.controller;

import com.example.demo.dto.SeckillEventDTO;
import com.example.demo.model.SeckillEvent;
import com.example.demo.service.SeckillEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seckill")
public class SeckillEventController {

    @Autowired
    private SeckillEventService seckillEventService;

    // 创建秒杀活动
    @PostMapping("/create")
    public SeckillEvent createSeckillEvent(@RequestBody SeckillEventDTO seckillEventDTO) {
        return seckillEventService.createSeckillEvent(seckillEventDTO);
    }

    // 获取所有秒杀活动
    @GetMapping("/events")
    public List<SeckillEvent> getAllSeckillEvents() {
        return seckillEventService.getAllSeckillEvents();
    }
}
