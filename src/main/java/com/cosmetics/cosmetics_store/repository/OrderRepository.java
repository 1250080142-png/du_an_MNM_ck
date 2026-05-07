package com.cosmetics.cosmetics_store.repository;

import com.cosmetics.cosmetics_store.model.Order;
import com.cosmetics.cosmetics_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Tìm danh sách đơn hàng theo User
    List<Order> findByUser(User user);
}