package com.example.pos_backend.controller;

import com.example.pos_backend.entity.Product;
import com.example.pos_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // 获取所有商品
    @GetMapping
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    // 新增商品
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productRepository.save(product);
    }

    // 根据 ID 获取商品
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productRepository.findById(id).orElseThrow();
    }

    // 更新商品
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product updated) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setName(updated.getName());
        product.setDescription(updated.getDescription());
        product.setPrice(updated.getPrice());
        product.setStock(updated.getStock());
        product.setStatus(updated.getStatus());
        return productRepository.save(product);
    }

    // 删除商品（软删除）
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setIsDeleted(true);
        productRepository.save(product);
    }
}
