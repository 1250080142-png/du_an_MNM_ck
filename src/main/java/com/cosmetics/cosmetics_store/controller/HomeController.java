package com.cosmetics.cosmetics_store.controller;

import com.cosmetics.cosmetics_store.model.Product;
import com.cosmetics.cosmetics_store.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    @Autowired private ProductRepository productRepository;
    
    // 1. Trang chủ (Hiển thị danh sách sản phẩm)
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "client/index";
    }
    
    // 2. Trang Chi tiết Sản phẩm
    @GetMapping("/products/{id}")
    public String viewProductDetail(@PathVariable Long id, Model model) {
        // Tìm sản phẩm theo ID
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

        model.addAttribute("product", product);
        
        return "client/product-detail";
    }
}