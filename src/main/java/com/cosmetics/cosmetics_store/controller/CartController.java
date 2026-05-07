package com.cosmetics.cosmetics_store.controller;

import com.cosmetics.cosmetics_store.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // 1. Xem Giỏ hàng (URL: /cart)
    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("cart", cartService.getCartByUser());
        return "client/cart"; // Trả về cart.html
    }

    // 2. Thêm Sản phẩm vào Giỏ hàng
    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId, 
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity) {
        
        cartService.addProductToCart(productId, quantity);
        return "redirect:/cart"; // Chuyển hướng về trang giỏ hàng
    }

    // 3. Cập nhật số lượng
    @PostMapping("/update")
    public String updateCart(@RequestParam("itemId") Long itemId, 
                             @RequestParam("quantity") int quantity) {
        
        cartService.updateCartItem(itemId, quantity);
        return "redirect:/cart";
    }

    // 4. Xóa món hàng
    @GetMapping("/remove/{itemId}")
    public String removeCartItem(@PathVariable("itemId") Long itemId) {
        cartService.removeCartItem(itemId);
        return "redirect:/cart";
    }
}