package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.model.Product;
import com.example.demo.model.Category;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.InsufficientStockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // 获取所有秒杀商品
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();
            List<ProductDTO> productDTOs = products.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            // 记录错误日志（可选）
            // logger.error("获取商品列表时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 获取单个产品详情
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            ProductDTO productDTO = convertToDTO(productOpt.get());
            return ResponseEntity.ok(productDTO);
        } else {
            throw new ProductNotFoundException("产品未找到，ID: " + id);
        }
    }

    // 创建新产品
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

    // 更新产品信息
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody Product updatedProduct) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (!productOpt.isPresent()) {
            throw new ProductNotFoundException("产品未找到，ID: " + id);
        }

        Product existingProduct = productOpt.get();
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

    // 删除产品
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (!productOpt.isPresent()) {
            throw new ProductNotFoundException("产品未找到，ID: " + id);
        }
        productRepository.delete(productOpt.get());
        return ResponseEntity.noContent().build();
    }

    // 购买产品
    @PostMapping("/buy/{id}")
    public ResponseEntity<String> buyProduct(@PathVariable Long id, @RequestParam(defaultValue = "1") int quantity) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (!productOpt.isPresent()) {
            throw new ProductNotFoundException("产品未找到，ID: " + id);
        }

        Product product = productOpt.get();

        if (product.getStock() < quantity) {
            throw new InsufficientStockException("库存不足，无法购买此数量的产品。");
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        return ResponseEntity.ok("购买成功！");
    }

    // 辅助方法：将 Product 转换为 ProductDTO
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
