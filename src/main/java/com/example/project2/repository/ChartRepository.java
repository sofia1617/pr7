package com.example.project2.repository;

import com.example.project2.model.ChartModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChartRepository extends JpaRepository<ChartModel, Long> {
    List<ChartModel> findByNameContainingIgnoreCase(String name);
}
