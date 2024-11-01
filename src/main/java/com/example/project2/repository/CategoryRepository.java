package com.example.project2.repository;

import com.example.project2.model.CategoryModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryModel, Long> {
    // Поиск по частичному совпадению названия (игнорируя логически удаленные категории)
    List<CategoryModel> findByNameContainingAndDeletedFalse(String name);

    // Поиск по частичному совпадению с сортировкой
    List<CategoryModel> findByNameContainingAndDeletedFalse(String name, Sort sort);

    // Поиск всех не удалённых с сортировкой
    List<CategoryModel> findAllByDeletedFalse(Sort sort);

    // Найти только не удаленные категории
    List<CategoryModel> findByDeletedFalse();

    List<CategoryModel> findByDeletedTrue();


}
