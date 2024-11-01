package com.example.project2.service;

import com.example.project2.model.RoleModel;
import com.example.project2.model.UserModel;
import com.example.project2.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Получение роли по имени с выбросом исключения, если роль не найдена
    public RoleModel findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Роль " + name + " не найдена"));
    }

    // Получить все роли
    public List<RoleModel> findAll() {
        return roleRepository.findAll();
    }

    // Найти роль по ID
    public Optional<RoleModel> findById(Long id) {
        return roleRepository.findById(id);
    }

    public List<RoleModel> findByNameContainingIgnoreCase(String name) {
        return roleRepository.findByNameContainingIgnoreCase(name);
    }

    // Сохранить роль
    public RoleModel save(RoleModel role) {
        return roleRepository.save(role);
    }

    // Удалить роль по ID
    public void deleteById(Long id) {
        roleRepository.deleteById(id);
    }
}
