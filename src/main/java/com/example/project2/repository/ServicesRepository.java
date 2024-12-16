package com.example.project2.repository;

import com.example.project2.model.ServicesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicesRepository extends JpaRepository<ServicesModel, Long> {
    List<ServicesModel> findByNumberServicesContainingIgnoreCaseOrUserNameContainingIgnoreCase(String numberServices, String name);


    List<ServicesModel> findByIsDeletedFalse();
}

