package com.example.project2.controller;

import com.example.project2.model.Product_and_materialsModel;
import com.example.project2.service.Product_and_materialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/manufacturers")
public class ManufacturerControllerAPI {

    @Autowired
    private Product_and_materialsService productandmaterialsService;

    @GetMapping
    public List<Product_and_materialsModel> getAllManufacturers() {
        return productandmaterialsService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product_and_materialsModel> getManufacturerById(@PathVariable Long id) {
        Optional<Product_and_materialsModel> manufacturer = productandmaterialsService.findById(id);
        return manufacturer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Product_and_materialsModel createManufacturer(@RequestBody Product_and_materialsModel manufacturer) {
        return productandmaterialsService.save(manufacturer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product_and_materialsModel> updateManufacturer(@PathVariable Long id, @RequestBody Product_and_materialsModel manufacturerDetails) {
        Optional<Product_and_materialsModel> manufacturerOptional = productandmaterialsService.findById(id);
        if (manufacturerOptional.isPresent()) {
            Product_and_materialsModel manufacturer = manufacturerOptional.get();
            manufacturer.setName(manufacturerDetails.getName());
            // Добавьте другие поля для обновления, если они есть
            return ResponseEntity.ok(productandmaterialsService.save(manufacturer));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManufacturer(@PathVariable Long id) {
        productandmaterialsService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}