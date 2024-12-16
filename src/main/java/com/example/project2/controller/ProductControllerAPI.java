package com.example.project2.controller;

import com.example.project2.model.RecordsModel;
import com.example.project2.service.RecordsService;
import com.example.project2.service.ClientsService;
import com.example.project2.service.Product_and_materialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/records")
public class ProductControllerAPI {

    @Autowired
    private RecordsService recordsService;

    @Autowired
    private ClientsService clientsService;

    @Autowired
    private Product_and_materialsService productandmaterialsService;

    @GetMapping
    public List<RecordsModel> getAllProducts() {
        return recordsService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecordsModel> getProductById(@PathVariable Long id) {
        Optional<RecordsModel> product = recordsService.findById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public RecordsModel createProduct(@RequestBody RecordsModel product) {
        return recordsService.save(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecordsModel> updateProduct(@PathVariable Long id, @RequestBody RecordsModel productDetails) {
        Optional<RecordsModel> productOptional = recordsService.findById(id);
        if (productOptional.isPresent()) {
            RecordsModel product = productOptional.get();
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setName(productDetails.getClient().getName());
            product.setProduct_and_materials(productDetails.getProduct_and_materials());
            // Добавьте другие поля для обновления, если они есть
            return ResponseEntity.ok(recordsService.save(product));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        recordsService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}