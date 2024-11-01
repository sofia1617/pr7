package com.example.project2.controller;

import com.example.project2.model.ManufacturerModel;
import com.example.project2.service.ManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/manufacturers")
public class ManufacturerControllerAPI {

    @Autowired
    private ManufacturerService manufacturerService;

    @GetMapping
    public List<ManufacturerModel> getAllManufacturers() {
        return manufacturerService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManufacturerModel> getManufacturerById(@PathVariable Long id) {
        Optional<ManufacturerModel> manufacturer = manufacturerService.findById(id);
        return manufacturer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ManufacturerModel createManufacturer(@RequestBody ManufacturerModel manufacturer) {
        return manufacturerService.save(manufacturer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ManufacturerModel> updateManufacturer(@PathVariable Long id, @RequestBody ManufacturerModel manufacturerDetails) {
        Optional<ManufacturerModel> manufacturerOptional = manufacturerService.findById(id);
        if (manufacturerOptional.isPresent()) {
            ManufacturerModel manufacturer = manufacturerOptional.get();
            manufacturer.setName(manufacturerDetails.getName());
            // Добавьте другие поля для обновления, если они есть
            return ResponseEntity.ok(manufacturerService.save(manufacturer));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManufacturer(@PathVariable Long id) {
        manufacturerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}