package com.example.project2.service;

import com.example.project2.model.ClientsModel;
import com.example.project2.repository.ClientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientsService {

    private final ClientsRepository clientsRepository;

    @Autowired
    public ClientsService(ClientsRepository clientsRepository) {
        this.clientsRepository = clientsRepository;
    }

    // Получить все категории, отсортированные по имени
    public List<ClientsModel> findAllSorted(String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "name");
        return clientsRepository.findAllByDeletedFalse(sort);
    }

    // Поиск по названию с сортировкой
    public List<ClientsModel> searchByName(String name, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "name");
        return clientsRepository.findByNameContainingAndDeletedFalse(name, sort);
    }


    // Получить все категории
    public List<ClientsModel> findAll() {
        return clientsRepository.findAll();
    }

    // Получить только активные категории
    public List<ClientsModel> findActiveCategories() {
        return clientsRepository.findByDeletedFalse();
    }

    // Найти категорию по ID
    public Optional<ClientsModel> findById(Long id) {
        return clientsRepository.findById(id);
    }

    // Сохранить категорию
    public ClientsModel save(ClientsModel client) {
        return clientsRepository.save(client);
    }

    // Логическое удаление
    public void softDeleteById(Long id) {
        Optional<ClientsModel> category = findById(id);
        category.ifPresent(cat -> {
            cat.setDeleted(true);
            clientsRepository.save(cat);
        });
    }

    // Получить все удаленные категории
    public List<ClientsModel> findAllDeleted() {
        return clientsRepository.findByDeletedTrue();
    }


    // Восстановление логически удаленной категории
    public void restoreById(Long id) {
        Optional<ClientsModel> category = findById(id);
        category.ifPresent(cat -> {
            cat.setDeleted(false);
            clientsRepository.save(cat);
        });
    }

    // Физическое удаление
    public void hardDeleteById(Long id) {
        clientsRepository.deleteById(id);
    }

    // Обновление категории
    public ClientsModel updateCategory(Long id, ClientsModel categoryDetails) {
        return findById(id).map(client -> {
            client.setName(categoryDetails.getName());
            client.setDeleted(categoryDetails.isDeleted());
            return clientsRepository.save(client);
        }).orElse(null);
    }

    // Новый метод для получения активных клиентов
    public List<ClientsModel> findActiveClients() {
        return clientsRepository.findByDeletedFalse();
    }



}