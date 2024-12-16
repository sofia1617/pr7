package com.example.project2.service;

import com.example.project2.model.Specialization;
import com.example.project2.model.UserModel;
import com.example.project2.repository.SpecializationRepository;
import com.example.project2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepository;
    private final UserRepository userRepository;

    @Autowired
    public SpecializationService(SpecializationRepository specializationRepository, UserRepository userRepository) {
        this.specializationRepository = specializationRepository;
        this.userRepository = userRepository;
    }

    public List<Specialization> findAll() {
        return specializationRepository.findAll();
    }

    public Optional<Specialization> findById(Long id) {
        return specializationRepository.findById(id);
    }

    public Specialization save(Specialization specialization) {
        return specializationRepository.save(specialization);
    }

    public void deleteById(Long id) {
        specializationRepository.deleteById(id);
    }

    public List<UserModel> getUsers() {
        return userRepository.findAll();
    }
}
