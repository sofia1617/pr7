package com.example.project2.repository;

import com.example.project2.model.RoleModel;
import com.example.project2.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleModel, Long> {
    Optional<RoleModel> findByName(String name); // Изменить на Optional
    List<RoleModel> findByNameContainingIgnoreCase(String name);
    List<RoleModel> findByNameContaining(String name); // Убедитесь, что этот метод есть
}
