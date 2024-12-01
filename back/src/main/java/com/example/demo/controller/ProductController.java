package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.exception.specific.*;
import com.example.demo.exception.base.*;
import com.example.demo.model.Product;
import com.example.demo.model.Category;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // 构造器注入
    @Autowired
    public ProductController(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * 获取所有秒杀商品
     *
     * @return 商品列表
     */
     @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "8") int size,
            @RequestParam(value = "query", defaultValue = "") String query
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> productPage;

        if (query.isEmpty()) {
            productPage = productRepository.findAll(pageable);
        } else {
            productPage = productRepository.findByNameContainingIgnoreCase(query, pageable);
        }

        List<ProductDTO> productDTOs = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("products", productDTOs);
        response.put("totalPages", productPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * 获取单个产品详情
     *
     * @param id 产品ID
     * @return 产品详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("产品未找到，ID: " + id));
        ProductDTO productDTO = convertToDTO(product);
        return ResponseEntity.ok(productDTO);
    }

    /**
     * 创建新产品
     *
     * @param product 产品数据
     * @return 创建的产品
     */
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody Product product) {
        // 验证分类是否存在
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new ProductNotFoundException("分类未找到，ID: " + product.getCategory().getId()));
            product.setCategory(category);
        } else {
            product.setCategory(null); // 分类为可选
        }

        Product savedProduct = productRepository.save(product);
        ProductDTO productDTO = convertToDTO(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
    }

    /**
     * 更新产品信息
     *
     * @param id            产品ID
     * @param updatedProduct 更新后的产品数据
     * @return 更新后的产品
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody Product updatedProduct) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("产品未找到，ID: " + id));

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setStock(updatedProduct.getStock());
        existingProduct.setOriginalPrice(updatedProduct.getOriginalPrice());
        existingProduct.setSalePrice(updatedProduct.getSalePrice());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());

        // 更新分类
        if (updatedProduct.getCategory() != null && updatedProduct.getCategory().getId() != null) {
            Category category = categoryRepository.findById(updatedProduct.getCategory().getId())
                    .orElseThrow(() -> new ProductNotFoundException("分类未找到，ID: " + updatedProduct.getCategory().getId()));
            existingProduct.setCategory(category);
        } else {
            existingProduct.setCategory(null); // 分类为可选
        }

        Product savedProduct = productRepository.save(existingProduct);
        ProductDTO productDTO = convertToDTO(savedProduct);
        return ResponseEntity.ok(productDTO);
    }

    /**
     * 删除产品
     *
     * @param id 产品ID
     * @return 无内容
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("产品未找到，ID: " + id));
        productRepository.delete(product);
        return ResponseEntity.noContent().build();
    }

    /**
     * 购买产品
     *
     * @param id       产品ID
     * @param quantity 购买数量
     * @return 购买结果消息
     */
    @PostMapping("/buy/{id}")
    public ResponseEntity<String> buyProduct(@PathVariable Long id, @RequestParam(defaultValue = "1") int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("产品未找到，ID: " + id));

        if (product.getStock() < quantity) {
            throw new InsufficientStockException("库存不足，无法购买此数量的产品。");
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        return ResponseEntity.ok("购买成功！");
    }

    /**
     * 辅助方法：将 Product 转换为 ProductDTO
     *
     * @param product 产品实体
     * @return 产品DTO
     */
    private ProductDTO convertToDTO(Product product) {
        String categoryName = (product.getCategory() != null) ? product.getCategory().getName() : null;
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getStock(),
                product.getOriginalPrice().doubleValue(),
                product.getSalePrice().doubleValue(),
                product.getDescription(),
                product.getImageUrl(),
                categoryName
        );
    }
}
