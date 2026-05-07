package com.cosmetics.cosmetics_store.service;

import com.cosmetics.cosmetics_store.model.User;
import com.cosmetics.cosmetics_store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	System.out.println("===> Đang đăng nhập user: " + username);
    	
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));
        System.out.println("===> Mật khẩu lấy từ DB là: " + user.getPasswordHash());
        System.out.println("===> Quyền (Role) lấy từ DB là: " + user.getRole());
        
        // Kiểm tra xem role trong DB có sẵn chữ "ROLE_" chưa
        String roleName = user.getRole();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), 
                user.getPasswordHash(), 
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }
}