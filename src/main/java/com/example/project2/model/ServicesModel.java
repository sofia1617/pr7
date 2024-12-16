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
@Table(name = "services")
public class ServicesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @NotBlank(message = "Поле обязательно к заполнению")
    @Size(min = 6, max = 25, message = "Поле должно содержать 6 - 25 символов")
    private String numberRecords;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserModel user;

    @ManyToMany
    @JoinTable(
            name = "services_records",
            joinColumns = @JoinColumn(name = "services_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "records_id", referencedColumnName = "id")
    )
    private Collection<RecordsModel> servicesRecords;

    @NotBlank(message = "Поле обязательно к заполнению")
    @Size(min = 6, max = 25, message = "Поле должно содержать 6 - 25 символов")
    private String numberServices;  // Используйте правильное имя

    @Column(name = "deleted")
    private boolean isDeleted;

    // геттеры и сеттеры
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }


    @ManyToOne
    @JoinColumn(name = "chart_id", referencedColumnName = "id")
    private ChartModel chart;


}



