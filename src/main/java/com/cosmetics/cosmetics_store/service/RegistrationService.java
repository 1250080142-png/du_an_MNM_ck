package com.cosmetics.cosmetics_store.service;

import com.cosmetics.cosmetics_store.model.Cart;
import com.cosmetics.cosmetics_store.model.User;
import com.cosmetics.cosmetics_store.repository.CartRepository;
import com.cosmetics.cosmetics_store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class RegistrationService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private CartRepository cartRepository;

    @Transactional
    public User registerNewUser(User user) {
        // 1. Kiểm tra tồn tại
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }
        
        // 2. Mã hóa mật khẩu
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        
        // 3. Lưu User
        User savedUser = userRepository.save(user);

        // 4. Tạo Giỏ hàng mặc định cho User mới
        Cart newCart = new Cart();
        newCart.setUser(savedUser);
        cartRepository.save(newCart);

        return savedUser;
    }
}