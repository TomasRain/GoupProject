package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for product responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private Integer stock;
    private Double originalPrice;
    private Double salePrice;
    private String description;
    private String imageUrl;
    private String categoryName; // 或者使用 Long categoryId，根据需求
}
