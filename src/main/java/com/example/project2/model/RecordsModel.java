package com.example.project2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
@Entity
@Table(name = "records")
public class RecordsModel {

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
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private ClientsModel client;

    @ManyToOne
    @JoinColumn(name = "product_and_material_id", referencedColumnName = "id")  // Fixed typo from "manufacture_id"
    private Product_and_materialsModel product_and_materials;

    private boolean deleted;  // Removed @NotBlank
}
