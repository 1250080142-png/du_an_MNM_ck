package com.cosmetics.cosmetics_store.service;

import com.cosmetics.cosmetics_store.model.*;
import com.cosmetics.cosmetics_store.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private UserRepository userRepository;

    // --- Lấy User đang đăng nhập ---
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("User must be logged in to place an order.");
        }
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    // --- 1. Tạo Đơn hàng từ Giỏ hàng ---
    @Transactional
    public Order placeOrder(String receiverName, String shippingAddress, String receiverPhone) {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Cart not found for user."));
        
        List<CartItem> cartItems = cart.getItems();
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty cart.");
        }

        // 1. Tạo đối tượng Order
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setReceiverName(receiverName);
        newOrder.setShippingAddress(shippingAddress);
        newOrder.setReceiverPhone(receiverPhone);

        double total = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        // 2. Chuyển CartItem sang OrderItem
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(newOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtOrder(cartItem.getProduct().getPrice()); // Lấy giá hiện tại của sản phẩm

            total += orderItem.getPriceAtOrder() * orderItem.getQuantity();
            orderItems.add(orderItem);
        }

        newOrder.setTotalAmount(total);
        newOrder.setItems(orderItems);
        orderRepository.save(newOrder);
        orderItemRepository.saveAll(orderItems);

        // 3. Xóa các mục trong Giỏ hàng (Clear Cart)
        cartItemRepository.deleteAll(cartItems);
        cartItems.clear(); 
        cartRepository.save(cart); // Lưu để cập nhật danh sách items rỗng

        return newOrder;
    }

    // --- 4. Lấy danh sách các đơn hàng của người dùng ---
    public List<Order> getOrdersByUser() {
        User user = getCurrentUser();
        return orderRepository.findByUser(user);
    }
}