package com.example.project2.controller;

import com.example.project2.model.UserModel;
import com.example.project2.service.RoleService;
import com.example.project2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserControllerAPI {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<UserModel> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserModel> getUserById(@PathVariable Long id) {
        Optional<UserModel> user = userService.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserModel createUser(@RequestBody UserModel user) {
        return userService.save(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserModel> updateUser(@PathVariable Long id, @RequestBody UserModel userDetails) {
        Optional<UserModel> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            UserModel user = userOptional.get();
            user.setName(userDetails.getName());
            user.setSurname(userDetails.getSurname());
            user.setPathronymic(userDetails.getPathronymic());
            user.setLogin(userDetails.getLogin());
            user.setPassword(userDetails.getPassword()); // Пароль будет зашифрован в сервисе
            user.setDeleted(userDetails.getRole().isDeleted());
            user.setRole(userDetails.getRole());
            // Добавьте другие поля для обновления, если они есть
            return ResponseEntity.ok(userService.save(user));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}