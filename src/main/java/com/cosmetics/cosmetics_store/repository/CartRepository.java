package com.cosmetics.cosmetics_store.repository;

import com.cosmetics.cosmetics_store.model.Cart;
import com.cosmetics.cosmetics_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    // Tìm giỏ hàng theo User
    Optional<Cart> findByUser(User user);
}