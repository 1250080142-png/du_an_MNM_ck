package com.cosmetics.cosmetics_store.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;  
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    
    // Sử dụng TEXT để thay thế cho NVARCHAR(MAX)
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Double price;
    private Integer stockQuantity;
    private String imageUrl;

    @ManyToOne 
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

 // 1. Tự thêm Constructor không tham số (Bắt buộc cho JPA)
    public Product() {
    }

    // 2. Tự thêm Constructor có tham số (Để DataInitializer dùng)
    public Product(String name, String description, Double price, Integer stockQuantity, Category category, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.imageUrl = imageUrl;
    }
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(Integer stockQuantity) {
		this.stockQuantity = stockQuantity;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
    
	
    
}