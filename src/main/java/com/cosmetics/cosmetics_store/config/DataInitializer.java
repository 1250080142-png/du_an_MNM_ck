package com.cosmetics.cosmetics_store.config;

import com.cosmetics.cosmetics_store.model.Category;
import com.cosmetics.cosmetics_store.model.Product;
import com.cosmetics.cosmetics_store.model.User;
import com.cosmetics.cosmetics_store.repository.CategoryRepository;
import com.cosmetics.cosmetics_store.repository.ProductRepository;
import com.cosmetics.cosmetics_store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // 1. Tạo Tài khoản Admin
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123")); 
            admin.setEmail("admin@cosmetics.com");
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println(">>> Đã tạo tài khoản admin/admin123");
        }

        // 2. Tạo Danh mục
        if (categoryRepository.count() == 0) {
            Category skincare = new Category();
            skincare.setName("SKINCARE");
            categoryRepository.save(skincare);

            Category makeup = new Category();
            makeup.setName("MAKEUP");
            categoryRepository.save(makeup);
        }

        // 3. Tạo Sản phẩm mẫu
        if (productRepository.count() == 0) {
            Category skincareCat = categoryRepository.findByName("SKINCARE").orElse(null);
            Category makeupCat = categoryRepository.findByName("MAKEUP").orElse(null);

            if (skincareCat != null && makeupCat != null) {
                saveSampleProduct("Vitamin C Serum", "Serum làm sáng da.", 450000.0, 50, skincareCat, "https://tse1.mm.bing.net/th/id/OIP.RFhBJofnPI4Au8nntg4PNAHaHa");
                saveSampleProduct("Sunscreen SPF 50+", "Kem chống nắng phổ rộng.", 300000.0, 80, skincareCat, "https://via.placeholder.com/150");
                saveSampleProduct("Matte Lipstick", "Son lì màu đỏ quyến rũ.", 250000.0, 90, makeupCat, "https://via.placeholder.com/150");
                
                System.out.println(">>> Đã khởi tạo dữ liệu mẫu thành công trên PostgreSQL.");
            }
        }
    }

    private void saveSampleProduct(String name, String desc, Double price, Integer stock, Category cat, String url) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(price);
        p.setStockQuantity(stock);
        p.setCategory(cat);
        p.setImageUrl(url);
        productRepository.save(p);
    }
}