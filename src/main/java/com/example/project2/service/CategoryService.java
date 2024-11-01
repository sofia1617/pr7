package com.example.project2.service;

import com.example.project2.model.CategoryModel;
import com.example.project2.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Получить все категории, отсортированные по имени
    public List<CategoryModel> findAllSorted(String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "name");
        return categoryRepository.findAllByDeletedFalse(sort);
    }

    // Поиск по названию с сортировкой
    public List<CategoryModel> searchByName(String name, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "name");
        return categoryRepository.findByNameContainingAndDeletedFalse(name, sort);
    }


    // Получить все категории
    public List<CategoryModel> findAll() {
        return categoryRepository.findAll();
    }

    // Получить только активные категории
    public List<CategoryModel> findActiveCategories() {
        return categoryRepository.findByDeletedFalse();
    }

    // Найти категорию по ID
    public Optional<CategoryModel> findById(Long id) {
        return categoryRepository.findById(id);
    }

    // Сохранить категорию
    public CategoryModel save(CategoryModel category) {
        return categoryRepository.save(category);
    }

    // Логическое удаление
    public void softDeleteById(Long id) {
        Optional<CategoryModel> category = findById(id);
        category.ifPresent(cat -> {
            cat.setDeleted(true);
            categoryRepository.save(cat);
        });
    }

    // Получить все удаленные категории
    public List<CategoryModel> findAllDeleted() {
        return categoryRepository.findByDeletedTrue();
    }


    // Восстановление логически удаленной категории
    public void restoreById(Long id) {
        Optional<CategoryModel> category = findById(id);
        category.ifPresent(cat -> {
            cat.setDeleted(false);
            categoryRepository.save(cat);
        });
    }

    // Физическое удаление
    public void hardDeleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    // Обновление категории
    public CategoryModel updateCategory(Long id, CategoryModel categoryDetails) {
        return findById(id).map(category -> {
            category.setName(categoryDetails.getName());
            category.setDeleted(categoryDetails.isDeleted());
            return categoryRepository.save(category);
        }).orElse(null);
    }


}