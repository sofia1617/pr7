package com.example.project2.service;

import com.example.project2.model.Product_and_materialsModel;
import com.example.project2.repository.Product_and_materialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Product_and_materialsService {

    private final Product_and_materialsRepository productandmaterialsRepository;

    @Autowired
    public Product_and_materialsService(Product_and_materialsRepository productandmaterialsRepository) {
        this.productandmaterialsRepository = productandmaterialsRepository;
    }

    public List<Product_and_materialsModel> findAll() {
        return productandmaterialsRepository.findAll();
    }

    public Optional<Product_and_materialsModel> findById(Long id) {
        return productandmaterialsRepository.findById(id);
    }

    public Product_and_materialsModel save(Product_and_materialsModel productandmaterialsModel) {
        return productandmaterialsRepository.save(productandmaterialsModel);
    }

    public void deleteById(Long id) {
        productandmaterialsRepository.deleteById(id);
    }

    // Новый метод для поиска по имени с учетом регистра
    public List<Product_and_materialsModel> findByNameContainingIgnoreCase(String name) {
        return productandmaterialsRepository.findByNameContainingIgnoreCase(name);
    }

    // Новый метод для получения элементов с deleted == false
    public List<Product_and_materialsModel> findAllByDeletedFalse() {
        return productandmaterialsRepository.findAllByDeletedFalse();
    }
}
