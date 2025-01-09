package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class SeckillEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // 秒杀活动名称

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "GMT+8")
    private Date startTime;  // 活动开始时间

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "GMT+8")
    private Date endTime;    // 活动结束时间

    @ElementCollection  // 使用 ElementCollection 来存储商品 ID
    @CollectionTable(
            name = "seckill_event_product",
            joinColumns = @JoinColumn(name = "seckill_event_id")
    )
    @Column(name = "product_id")
    private Set<Long> productIds;  // 仅保存商品的 ID 集合

    @OneToMany(mappedBy = "seckillEvent", cascade = CascadeType.ALL)
    private Set<ProductSeckillDiscount> productSeckillDiscounts;  // 商品折扣

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;  // 创建活动的管理员

    // Getter 和 Setter 方法

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Set<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(Set<Long> productIds) {
        this.productIds = productIds;
    }

    public Set<ProductSeckillDiscount> getProductSeckillDiscounts() {
        return productSeckillDiscounts;
    }

    public void setProductSeckillDiscounts(Set<ProductSeckillDiscount> productSeckillDiscounts) {
        this.productSeckillDiscounts = productSeckillDiscounts;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }
}
