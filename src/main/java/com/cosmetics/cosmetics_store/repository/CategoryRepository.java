package com.cosmetics.cosmetics_store.repository;

import com.cosmetics.cosmetics_store.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Cần import Optional

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Thêm phương thức để tìm Category theo tên, phục vụ DataInitializer
    Optional<Category> findByName(String name);
}