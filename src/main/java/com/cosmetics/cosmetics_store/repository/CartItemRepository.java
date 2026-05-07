package com.cosmetics.cosmetics_store.repository;

import com.cosmetics.cosmetics_store.model.Cart;
import com.cosmetics.cosmetics_store.model.CartItem;
import com.cosmetics.cosmetics_store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Tìm một món hàng trong giỏ theo Cart và Product
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}