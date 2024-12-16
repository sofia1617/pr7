package com.example.project2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "product_and_materials")  // Имя таблицы "Product_and_materials"
public class Product_and_materialsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 13, message = "Максимальная длина = 13")
    private String name;

    private boolean deleted;  // Поле для логического удаления
}
