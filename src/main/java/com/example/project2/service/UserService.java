package com.example.project2.service;

import com.example.project2.model.ManufacturerModel;
import com.example.project2.model.UserModel;
import com.example.project2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    // Метод для шифрования пароля
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public List<UserModel> findAll() {
        return userRepository.findAll();
    }

    public Optional<UserModel> findById(Long id) {
        return userRepository.findById(id);
    }

    public UserModel save(UserModel userModel) {
        return userRepository.save(userModel);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    // Метод для поиска пользователя по логину
    public Optional<UserModel> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    // Метод для проверки существования логина
    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    // Новый метод для поиска по имени с учетом регистра
    public List<UserModel> findByNameContainingIgnoreCase(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }
}
