package com.example.project2.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Collection;
@Data
@Entity
@Table(name = "orders")  // Renamed table to "orders" for consistency
public class OrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Поле обязательно к заполнению")
    @Size(min = 6, max = 25, message = "Поле должно содержать 6 - 25 символов")
    private String numberOrder;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserModel user;

    @ManyToMany
    @JoinTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id")
    )
    private Collection<ProductModel> products;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")  // Связь с моделью статуса
    private StatusModel status;

    private boolean deleted;  // Removed @NotBlank
}

