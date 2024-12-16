package com.example.project2.repository;
import com.example.project2.model.Product_and_materialsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Product_and_materialsRepository extends JpaRepository<Product_and_materialsModel, Long> {
    List<Product_and_materialsModel> findByNameContainingIgnoreCase(String name);

    // Новый метод для получения элементов, где поле deleted == false
    List<Product_and_materialsModel> findAllByDeletedFalse();
}
