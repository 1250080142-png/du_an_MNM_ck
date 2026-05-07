package com.cosmetics.cosmetics_store.service;

import com.cosmetics.cosmetics_store.model.Cart;
import com.cosmetics.cosmetics_store.model.CartItem;
import com.cosmetics.cosmetics_store.model.Product;
import com.cosmetics.cosmetics_store.model.User;
import com.cosmetics.cosmetics_store.repository.CartItemRepository;
import com.cosmetics.cosmetics_store.repository.CartRepository;
import com.cosmetics.cosmetics_store.repository.ProductRepository;
import com.cosmetics.cosmetics_store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
public class CartService {

    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;

    // --- Lấy User đang đăng nhập ---
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("User must be logged in to access cart.");
        }
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    // --- 1. Lấy hoặc Tạo Giỏ hàng hiện tại của người dùng ---
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    // --- 2. Thêm Sản phẩm vào Giỏ hàng ---
    @Transactional
    public void addProductToCart(Long productId, int quantity) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }
    }

    // --- 3. Lấy nội dung Giỏ hàng ---
    public Cart getCartByUser() {
        User user = getCurrentUser();
        return cartRepository.findByUser(user)
                .orElse(null);
    }
    
    // --- 4. Cập nhật số lượng sản phẩm trong giỏ ---
    @Transactional
    public void updateCartItem(Long itemId, int quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart Item not found"));
        
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }
    
    // --- 5. Xóa một món hàng khỏi giỏ ---
    public void removeCartItem(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }
}