package com.example.project2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "clients")
public class ClientsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 50, message = "Максимальная длина имени — 50 символов")
    private String name;

    @NotBlank(message = "Поле не может быть пустым")
    @Size(max = 50, message = "Максимальная длина фамилии — 50 символов")
    private String surname;

    @Size(max = 255, message = "Описание не может превышать 255 символов")
    private String description;  // Описание клиента

    @Email(message = "Некорректный email")
    private String email;

    @Size(max = 15, message = "Номер телефона не должен превышать 15 символов")
    private String phoneNumber;

    private LocalDate birthDate; // Дата рождения

    private boolean deleted;  // Поле для логического удаления
}
