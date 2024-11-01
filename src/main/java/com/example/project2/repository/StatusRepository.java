package com.example.project2.repository;

import com.example.project2.model.StatusModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRepository extends JpaRepository<StatusModel, Long> {
    List<StatusModel> findByNameContainingIgnoreCase(String name);
}
