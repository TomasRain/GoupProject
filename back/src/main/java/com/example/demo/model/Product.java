package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")  // 对应数据库的 products 表
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "商品名称不能为空")
    @Size(max = 255, message = "商品名称长度不能超过255个字符")
    @Column(nullable = false, length = 255)
    private String name;  // 商品名称

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    @Column(nullable = false)
    private Integer stock;  // 库存数量

    @NotNull(message = "原价不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "原价必须大于0")
    @Digits(integer = 10, fraction = 2, message = "原价格式不正确")
    @Column(name = "original_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalPrice;  // 原价格

    @NotNull(message = "秒杀价不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "秒杀价必须大于0")
    @Digits(integer = 10, fraction = 2, message = "秒杀价格式不正确")
    @Column(name = "sale_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;  // 秒杀价格

    @NotNull(message = "产品描述不能为空")
    @Size(max = 5000, message = "产品描述长度不能超过5000个字符")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;  // 产品描述

    @NotNull(message = "产品图片不能为空")
    @Size(max = 255, message = "产品图片URL长度不能超过255个字符")
    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;  // 产品图片URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;  // 产品所属分类

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;  // 创建时间

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // 更新时间

    @Version
    private Integer version;  // 乐观锁版本号

    // Getter 和 Setter 方法

    public Long getId() {
        return id;
    }

    // 通常不提供 setId 方法，因为 ID 是由数据库自动生成的

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 不提供 setCreatedAt 方法，创建时间由数据库自动设置

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // 不提供 setUpdatedAt 方法，更新时间由数据库自动设置

    public Integer getVersion() {
        return version;
    }

    // 不需要提供 setVersion 方法，版本号由 JPA 自动管理

    // 可选：添加库存扣减方法，确保库存操作的原子性
    public boolean reduceStock(int quantity) {
        if (this.stock >= quantity) {
            this.stock -= quantity;
            return true;
        } else {
            return false;  // 库存不足
        }
    }
}
