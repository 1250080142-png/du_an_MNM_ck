package com.cosmetics.cosmetics_store.service;

import com.cosmetics.cosmetics_store.model.Product;
import com.cosmetics.cosmetics_store.model.Category;
import com.cosmetics.cosmetics_store.repository.ProductRepository;
import com.cosmetics.cosmetics_store.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }
    
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}