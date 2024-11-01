package com.example.project2.service;

import com.example.project2.model.ManufacturerModel;
import com.example.project2.repository.ManufacturerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;

    @Autowired
    public ManufacturerService(ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }

    public List<ManufacturerModel> findAll() {
        return manufacturerRepository.findAll();
    }

    public Optional<ManufacturerModel> findById(Long id) {
        return manufacturerRepository.findById(id);
    }

    public ManufacturerModel save(ManufacturerModel manufacturerModel) {
        return manufacturerRepository.save(manufacturerModel);
    }

    public void deleteById(Long id) {
        manufacturerRepository.deleteById(id);
    }

    // Новый метод для поиска по имени с учетом регистра
    public List<ManufacturerModel> findByNameContainingIgnoreCase(String name) {
        return manufacturerRepository.findByNameContainingIgnoreCase(name);
    }
}
