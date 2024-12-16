package com.example.project2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "chart")  // Указываем имя таблицы для этой сущности
public class ChartModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    @Column(name = "name")  // Указываем имя столбца для поля name
    private String name;

    @NotNull(message = "День недели обязателен")
    @Column(name = "dayofweek")  // Указываем имя столбца для поля dayOfWeek
    private String dayOfWeek;

    @NotNull(message = "Время начала обязательно")
    @Column(name = "starttime")  // Указываем только имя столбца
    private LocalTime startTime;

    @NotNull(message = "Время окончания обязательно")
    @Column(name = "endtime")  // Указываем только имя столбца
    private LocalTime endTime;


    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")  // Связь с таблицей UserModel
    private UserModel user;

    private Boolean deleted;  // Используйте Boolean вместо boolean

}
