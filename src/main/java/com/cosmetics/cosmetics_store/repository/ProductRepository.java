package com.cosmetics.cosmetics_store.repository;
import com.cosmetics.cosmetics_store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product, Long> {}