package com.cosmetics.cosmetics_store.controller;

import com.cosmetics.cosmetics_store.model.Cart;
import com.cosmetics.cosmetics_store.model.User;
import com.cosmetics.cosmetics_store.repository.CartRepository;
import com.cosmetics.cosmetics_store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * HIỂN THỊ TRANG ĐĂNG NHẬP
     * Xử lý GET request cho /login
     */
    @GetMapping("/login")
    public String loginPage(
        @RequestParam(value = "error", required = false) String error,
        Model model) {
        
        if (error != null) {
            model.addAttribute("loginError", "Tên đăng nhập hoặc mật khẩu không đúng.");
        }
        // Trả về file HTML: src/main/resources/templates/client/login.html
        return "client/login"; 
    }

    /**
     * HIỂN THỊ TRANG ĐĂNG KÝ
     * Xử lý GET request cho /register
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Tạo đối tượng User rỗng để liên kết với form HTML
        model.addAttribute("user", new User());
        // Trả về file HTML: src/main/resources/templates/client/register.html
        return "client/register"; 
    }

    /**
     * XỬ LÝ ĐĂNG KÝ (POST request)
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        // 1. Kiểm tra tồn tại
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("registerError", "Tên đăng nhập đã tồn tại.");
            return "client/register";
        }
        
        // 2. Mã hóa mật khẩu, gán Role và lưu User
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setRole("CUSTOMER"); // Mặc định là Khách hàng
        User savedUser = userRepository.save(user);

        // 3. Tạo Giỏ hàng mới cho người dùng
        Cart newCart = new Cart();
        newCart.setUser(savedUser);
        cartRepository.save(newCart);

        // 4. Chuyển hướng về trang Đăng nhập với thông báo thành công
        return "redirect:/login?registerSuccess"; 
    }
}