package com.example.project2.service;

import com.example.project2.model.StatusModel;
import com.example.project2.model.UserModel;
import com.example.project2.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusService {

    private final StatusRepository statusRepository;

    @Autowired
    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public List<StatusModel> findAll() {
        return statusRepository.findAll();
    }

    public StatusModel findById(Long id) {
        return statusRepository.findById(id).orElse(null);
    }

    public StatusModel save(StatusModel status) {
        return statusRepository.save(status);
    }

    public void deleteById(Long id) {
        statusRepository.deleteById(id);
    }



    public List<StatusModel> findByNameContainingIgnoreCase(String name) {
        return statusRepository.findByNameContainingIgnoreCase(name);
    }

}
