package com.example.project2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
@Entity
@Table(name = "products")
public class ProductModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 25, message = "Максимальная длинна = 25 символов")
    private String name;

    @Size(max = 200, message = "Максимальная длинна = 200 символов")
    private String description;

    @Positive(message = "Поле может содержать только числа > 0")
    private Double price;

    @PositiveOrZero(message = "Поле не может содержать отрицательные числа")
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private CategoryModel category;

    @ManyToOne
    @JoinColumn(name = "manufacturer_id", referencedColumnName = "id")  // Fixed typo from "manufacture_id"
    private ManufacturerModel manufacturer;

    private boolean deleted;  // Removed @NotBlank
}
