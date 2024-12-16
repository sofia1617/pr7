package com.example.project2.repository;

import com.example.project2.model.ClientsModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientsRepository extends JpaRepository<ClientsModel, Long> {
    List<ClientsModel> findAllByDeletedFalse(Sort sort);

    List<ClientsModel> findByNameContainingAndDeletedFalse(String name, Sort sort);

    List<ClientsModel> findByDeletedFalse();

    List<ClientsModel> findByDeletedTrue();
}


