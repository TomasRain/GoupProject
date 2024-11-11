package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")  // 对应数据库的 products 表
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称长度不能超过100个字符")
    @Column(nullable = false)
    private String name;  // 商品名称

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    @Column(nullable = false)
    private Integer stock;  // 库存数量

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "价格必须大于0")
    @Digits(integer = 10, fraction = 2, message = "价格格式不正确")
    @Column(nullable = false)
    private BigDecimal price;  // 商品价格

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

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
