package com.example.project2.service;

import com.example.project2.model.ProductModel;
import com.example.project2.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Метод для получения всех товаров
    public List<ProductModel> findAll() {
        return productRepository.findAll();
    }

    // Метод для получения всех товаров с пагинацией
    public Page<ProductModel> findPaginated(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    // Метод для поиска товаров по имени с пагинацией
    public Page<ProductModel> findByNameContainingIgnoreCase(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    public Optional<ProductModel> findById(Long id) {
        return productRepository.findById(id);
    }

    public ProductModel save(ProductModel productModel) {
        return productRepository.save(productModel);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public Page<ProductModel> findAll(int page, String search) {
        Pageable pageable = PageRequest.of(page, 10);
        if (search != null && !search.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(search, pageable);
        }
        return productRepository.findAll(pageable);
    }

}
