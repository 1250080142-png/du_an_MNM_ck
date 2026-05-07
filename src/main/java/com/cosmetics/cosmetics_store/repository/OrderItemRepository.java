package com.cosmetics.cosmetics_store.repository;

import com.cosmetics.cosmetics_store.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Không cần thêm phương thức đặc biệt nào ở đây, JpaRepository đủ dùng
}