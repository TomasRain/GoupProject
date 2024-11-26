package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "categories")  // 对应数据库的 categories 表
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "类别名称不能为空")
    @Size(max = 255, message = "类别名称长度不能超过255个字符")
    @Column(nullable = false, unique = true, length = 255)
    private String name;  // 类别名称

    @Size(max = 1000, message = "类别描述长度不能超过1000个字符")
    @Column(columnDefinition = "TEXT")
    private String description;  // 类别描述

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Product> products;  // 类别下的产品

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;  // 创建时间

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // 更新时间

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 不提供 setCreatedAt 方法，创建时间由数据库自动设置

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // 不提供 setUpdatedAt 方法，更新时间由数据库自动设置
}
