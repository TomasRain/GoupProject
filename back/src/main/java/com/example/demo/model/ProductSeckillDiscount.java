package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class ProductSeckillDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore  // 防止序列化时进入循环引用
    private Product product;  // 商品

    @ManyToOne
    @JoinColumn(name = "seckill_event_id")
    @JsonIgnore  // 防止序列化时进入循环引用
    private SeckillEvent seckillEvent;  // 关联的秒杀活动

    private BigDecimal discount;  // 秒杀折扣

    // Getter 和 Setter 方法

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public SeckillEvent getSeckillEvent() {
        return seckillEvent;
    }

    public void setSeckillEvent(SeckillEvent seckillEvent) {
        this.seckillEvent = seckillEvent;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}
