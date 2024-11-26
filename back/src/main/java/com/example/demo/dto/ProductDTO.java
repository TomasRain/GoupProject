package com.example.demo.dto;

public class ProductDTO {
    private Long id;
    private String name;
    private Integer stock;
    private Double originalPrice;
    private Double salePrice;
    private String description;
    private String imageUrl;
    private String categoryName; // 或者使用 Long categoryId

    // 构造器
    public ProductDTO() {}

    public ProductDTO(Long id, String name, Integer stock, Double originalPrice, Double salePrice, String description, String imageUrl, String categoryName) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.originalPrice = originalPrice;
        this.salePrice = salePrice;
        this.description = description;
        this.imageUrl = imageUrl;
        this.categoryName = categoryName;
    }

    // Getter 和 Setter 方法

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getStock() {
        return stock;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
