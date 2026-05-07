package com.cosmetics.cosmetics_store.controller;

import com.cosmetics.cosmetics_store.model.Cart;
import com.cosmetics.cosmetics_store.model.Order;
import com.cosmetics.cosmetics_store.service.CartService;
import com.cosmetics.cosmetics_store.service.OrderService;
import com.cosmetics.cosmetics_store.repository.OrderRepository; // Cần import OrderRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List; // Cần import List

@Controller
public class OrderController {

    @Autowired private CartService cartService;
    @Autowired private OrderService orderService;
    @Autowired private OrderRepository orderRepository; // Thêm OrderRepository để Admin quản lý

    // --- CHỨC NĂNG DÀNH CHO KHÁCH HÀNG (Client Functions) ---
    
    // 1. Hiển thị Trang Thanh toán (Checkout)
    @GetMapping("/checkout")
    public String checkout(Model model, RedirectAttributes redirectAttributes) {
        Cart cart = cartService.getCartByUser();
        if (cart == null || cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng của bạn đang trống.");
            return "redirect:/cart";
        }
        
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("cart", cart);
        model.addAttribute("totalAmount", total);
        model.addAttribute("order", new Order()); 
        
        return "client/checkout";
    }

    // 2. Xác nhận Đặt hàng (Place Order)
    @PostMapping("/placeOrder")
    public String placeOrder(@ModelAttribute("order") Order orderDetails, RedirectAttributes redirectAttributes) {
        try {
            Order placedOrder = orderService.placeOrder(
                orderDetails.getReceiverName(), 
                orderDetails.getShippingAddress(), 
                orderDetails.getReceiverPhone()
            );
            
            redirectAttributes.addFlashAttribute("success", "Đặt hàng thành công! Mã đơn hàng: #" + placedOrder.getId());
            return "redirect:/orders"; 
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi đặt hàng: Vui lòng đăng nhập lại.");
            return "redirect:/login";
        }
    }
    
    // 3. Xem danh sách Đơn hàng đã đặt
    @GetMapping("/orders")
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getOrdersByUser());
        return "client/order-list"; 
    }
}