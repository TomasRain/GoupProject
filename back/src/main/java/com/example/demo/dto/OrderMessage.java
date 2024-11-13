package com.example.demo.dto;

import java.io.Serializable;

public class OrderMessage implements Serializable {
    private Long userId;
    private Long productId;

    public OrderMessage(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    // Getter 和 Setter 方法

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
