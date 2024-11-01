package com.example.project2.repository;

import com.example.project2.model.ProductModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, Long> {
    Page<ProductModel> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

