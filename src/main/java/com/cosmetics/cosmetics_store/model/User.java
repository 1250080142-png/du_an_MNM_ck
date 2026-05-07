package com.cosmetics.cosmetics_store.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(name = "password_hash") // Đảm bảo tên này khớp 100% với tên cột trong pgAdmin
    private String passwordHash;
    private String email;
    private String phone;
    private String address;
    private String role; // ADMIN hoặc CUSTOMER
    private boolean enabled = true; // Mặc định tài khoản mới sẽ được kích hoạt

	 // Getter và Setter
	 public boolean isEnabled() {
	     return enabled;
	 }
	
	 public void setEnabled(boolean enabled) {
	     this.enabled = enabled;
	 }
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
    
    
}