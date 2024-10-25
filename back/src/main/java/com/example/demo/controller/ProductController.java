package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    public ProductRepository productRepository;//not safe

    // 获取所有秒杀商品
    @GetMapping
    public List<Product> getAllProducts() {
        // 返回数据库中所有商品，前端会使用这个数据来展示商品列表
        return productRepository.findAll();
    }
}
