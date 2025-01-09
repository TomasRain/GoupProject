package com.example.demo.service;

import com.example.demo.model.SeckillEvent;
import com.example.demo.dto.SeckillEventDTO;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.SeckillEventRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Service
public class SeckillEventService {

    @Autowired
    private SeckillEventRepository seckillEventRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // 创建秒杀活动
    @Transactional
    public SeckillEvent createSeckillEvent(SeckillEventDTO seckillEventDTO) {
        // 默认管理员为4（root）
        User admin = userRepository.findById(4L)
            .orElseThrow(() -> new RuntimeException("管理员未找到"));

        // 创建秒杀活动
        SeckillEvent seckillEvent = new SeckillEvent();
        seckillEvent.setName(seckillEventDTO.getName());
        seckillEvent.setStartTime(seckillEventDTO.getStartTime());
        seckillEvent.setEndTime(seckillEventDTO.getEndTime());
        seckillEvent.setAdmin(admin);

        // 将商品 ID 列表保存到 SeckillEvent
        Set<Long> productIds = new HashSet<>(seckillEventDTO.getProductIds());
        seckillEvent.setProductIds(productIds);

        // 保存秒杀活动
        return seckillEventRepository.save(seckillEvent);
    }

    // 获取所有秒杀活动及其关联商品
    public List<SeckillEvent> getAllSeckillEvents() {
        return seckillEventRepository.findAll();
    }
}
