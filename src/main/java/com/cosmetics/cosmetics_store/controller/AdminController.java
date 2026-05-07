package com.cosmetics.cosmetics_store.controller;

import com.cosmetics.cosmetics_store.model.*;
import com.cosmetics.cosmetics_store.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;

    // --- 1. DASHBOARD CHUNG ---
    @GetMapping({"", "/", "/dashboard"})
    public String adminIndex(Model model) {
        model.addAttribute("productCount", productRepository.count());
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("orderCount", orderRepository.count());
        return "admin/index";
    }

    // --- 2. QUẢN LÝ SẢN PHẨM ---
    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("activePage", "products");
        model.addAttribute("products", productRepository.findAll());
        return "admin/product-list"; 
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("activePage", "products");
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute("product") Product product,
                              @RequestParam("imageProduct") MultipartFile multipartFile,
                              RedirectAttributes ra) throws IOException {
        
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            
            // SỬA TẠI ĐÂY: Lưu luôn đường dẫn hoàn chỉnh vào DB
            product.setImageUrl("/images/" + fileName); 
            
            String uploadDir = "src/main/resources/static/images/";
            Path uploadPath = Paths.get(uploadDir);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } else {
            if (product.getId() != null) {
                Product existingProduct = productRepository.findById(product.getId()).orElse(null);
                if (existingProduct != null) {
                    product.setImageUrl(existingProduct.getImageUrl());
                }
            }
        }

        productRepository.save(product);
        ra.addFlashAttribute("success", "Đã lưu sản phẩm thành công!"); // Dùng "success" để khớp với list.html
        return "redirect:/admin/products";
    }
    
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            productRepository.deleteById(id);
            ra.addFlashAttribute("success", "Đã xóa sản phẩm thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }
    
    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại ID: " + id));
            
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("pageTitle", "Chỉnh sửa sản phẩm (ID: " + id + ")");
            model.addAttribute("activePage", "products");
            
            return "admin/product-form";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/products";
        }
    }	

    // --- 3. QUẢN LÝ DANH MỤC ---
    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("activePage", "categories");
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/category-list"; 
    }
    
    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute("category") Category category, RedirectAttributes ra) {
        categoryRepository.save(category);
        ra.addFlashAttribute("message", "Đã lưu danh mục thành công!");
        return "redirect:/admin/categories";
    }
    
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            // Kiểm tra xem danh mục có đang chứa sản phẩm nào không để tránh lỗi khóa ngoại (Foreign Key)
            categoryRepository.deleteById(id);
            ra.addFlashAttribute("success", "Đã xóa danh mục thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa danh mục này (có thể do đang có sản phẩm thuộc danh mục này)!");
        }
        return "redirect:/admin/categories";
    }

 // --- 4. QUẢN LÝ ĐƠN HÀNG ---
    @GetMapping("/orders")
    public String listOrders(Model model) {
        model.addAttribute("activePage", "orders");
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/order-list"; 
    }

    // Sửa đường dẫn để khớp với file list và xử lý xem chi tiết
    @GetMapping("/orders/detail/{id}")
    public String viewOrderDetail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("activePage", "orders");
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại: " + id));
        
        model.addAttribute("order", order);
        return "admin/order-detail"; 
    }

    // Thêm hàm xử lý cập nhật trạng thái đơn hàng
    @PostMapping("/orders/update-status")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId, 
                                    @RequestParam("status") String status, 
                                    RedirectAttributes ra) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại: " + orderId));
            order.setStatus(status);
            orderRepository.save(order);
            ra.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/orders/detail/" + orderId;
    }

 // --- 5. QUẢN LÝ NGƯỜI DÙNG ---

    // 5.1. Danh sách người dùng
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("activePage", "users");
        model.addAttribute("users", userRepository.findAll());
        return "admin/user-list";
    }

    // 5.2. Cập nhật quyền hạn (Role)
    @PostMapping("/users/update-role")
    public String updateUserRole(@RequestParam("userId") Long userId, 
                                 @RequestParam("role") String role, 
                                 RedirectAttributes ra) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setRole(role);
            userRepository.save(user);
            ra.addFlashAttribute("successMessage", "Đã cập nhật quyền cho: " + user.getUsername());
        } else {
            ra.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
        }
        return "redirect:/admin/users";
    }

    // 5.3. Khóa/Mở khóa tài khoản
    @PostMapping("/users/toggle-status")
    public String toggleUserStatus(@RequestParam("userId") Long userId, RedirectAttributes ra) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            // Đảo trạng thái true <-> false
            user.setEnabled(!user.isEnabled()); 
            userRepository.save(user);
            
            String action = user.isEnabled() ? "Mở khóa" : "Khóa";
            ra.addFlashAttribute("successMessage", "Đã " + action + " tài khoản: " + user.getUsername());
        } else {
            ra.addFlashAttribute("errorMessage", "Lỗi: Không tìm thấy người dùng!");
        }
        return "redirect:/admin/users";
    }

    // 5.4. Xóa người dùng
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            userRepository.deleteById(id);
            ra.addFlashAttribute("successMessage", "Xóa người dùng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa người dùng này!");
        }
        return "redirect:/admin/users";
    }
    
    // --- 6. QUẢN LÝ NHẬP KHO ---
    @GetMapping("/inventory")
    public String viewInventory(Model model) {
        model.addAttribute("activePage", "inventory");
        model.addAttribute("products", productRepository.findAll());
        return "admin/inventory-index";
    }
    
    @PostMapping("/inventory/add-stock")
    public String addStock(@RequestParam("productId") Long productId, 
                           @RequestParam("quantity") int quantity, 
                           RedirectAttributes redirectAttributes) {
        try {
            // 1. Tìm sản phẩm trong DB
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("ID sản phẩm không tồn tại: " + productId));

            // 2. Tính toán số lượng mới (Số lượng hiện tại + số lượng nhập thêm)
            int currentStock = (product.getStockQuantity() != null) ? product.getStockQuantity() : 0;
            product.setStockQuantity(currentStock + quantity);

            // 3. Lưu lại vào database
            productRepository.save(product);

            // 4. Thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật kho cho sản phẩm: " + product.getName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật kho: " + e.getMessage());
        }
        
        return "redirect:/admin/inventory";
    }
    
    
}