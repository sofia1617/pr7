package com.example.project2.repository;
import com.example.project2.model.ManufacturerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManufacturerRepository extends JpaRepository<ManufacturerModel, Long> {
    List<ManufacturerModel> findByNameContainingIgnoreCase(String name);
}
