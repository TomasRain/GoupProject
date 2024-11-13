package com.example.demo.dto;

import java.time.LocalDateTime;

public class OrderDTO {
    private Long id;
    private String productName;
    private String username;
    private LocalDateTime orderTime;
    private Integer quantity;

    // 构造器
    public OrderDTO(Long id, String productName, String username, LocalDateTime orderTime, Integer quantity) {
        this.id = id;
        this.productName = productName;
        this.username = username;
        this.orderTime = orderTime;
        this.quantity = quantity;
    }

    // Getter 方法

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
