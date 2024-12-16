package com.example.project2.repository;

import com.example.project2.model.RecordsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordsRepository extends JpaRepository<RecordsModel, Long> {
    Page<RecordsModel> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

