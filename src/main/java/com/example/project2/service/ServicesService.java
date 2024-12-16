package com.example.project2.service;

import com.example.project2.model.ServicesModel;
import com.example.project2.repository.ServicesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicesService {

    private final ServicesRepository servicesRepository;

    @Autowired
    public ServicesService(ServicesRepository servicesRepository) {
        this.servicesRepository = servicesRepository;
    }

    public List<ServicesModel> findAll() {
        return servicesRepository.findAll();
    }

    public ServicesModel findById(Long id) {
        return servicesRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        servicesRepository.deleteById(id);
    }

    public List<ServicesModel> findAllNotDeleted() {
        return servicesRepository.findByIsDeletedFalse();
    }


    public List<ServicesModel> search(String keyword) {
        return servicesRepository.findByNumberServicesContainingIgnoreCaseOrUserNameContainingIgnoreCase(keyword, keyword);
    }

    public ServicesModel save(ServicesModel services) {
        servicesRepository.save(services);
        return services;
    }
}

