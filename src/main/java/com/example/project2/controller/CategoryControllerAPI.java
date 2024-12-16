package com.example.project2.controller;

import com.example.project2.model.ClientsModel;
import com.example.project2.service.ClientsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryControllerAPI {

    @Autowired
    private ClientsService clientsService;

    @Operation(summary = "Get all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the categories"),
            @ApiResponse(responseCode = "404", description = "Categories not found")
    })
    @GetMapping
    public List<ClientsModel> getAllCategories() {
        return clientsService.findAll(); // Исправлено на findAll()
    }

    @Operation(summary = "Get a category by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the category"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientsModel> getCategoryById(@Parameter(description = "id of category to be searched")
                                                         @PathVariable Long id) {
        Optional<ClientsModel> category = clientsService.findById(id); // Исправлено на findById
        return category.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created"), // Изменено на 201
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<ClientsModel> createCategory(@RequestBody ClientsModel category) {
        ClientsModel createdCategory = clientsService.save(category);
        return ResponseEntity.status(201).body(createdCategory); // Возвращаем статус 201
    }

    @Operation(summary = "Update a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClientsModel> updateCategory(@PathVariable Long id, @RequestBody ClientsModel categoryDetails) {
        ClientsModel updatedCategory = clientsService.updateCategory(id, categoryDetails);
        return updatedCategory != null ? ResponseEntity.ok(updatedCategory) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        clientsService.hardDeleteById(id); // Используем метод для физического удаления
        return ResponseEntity.noContent().build();
    }
}
