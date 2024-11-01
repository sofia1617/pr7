package com.example.project2.controller;

import com.example.project2.model.RoleModel;
import com.example.project2.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
public class RoleControllerAPI {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<RoleModel> getAllRoles() {
        return roleService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleModel> getRoleById(@PathVariable Long id) {
        Optional<RoleModel> role = roleService.findById(id);
        return role.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public RoleModel createRole(@RequestBody RoleModel role) {
        return roleService.save(role);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleModel> updateRole(@PathVariable Long id, @RequestBody RoleModel roleDetails) {
        Optional<RoleModel> roleOptional = roleService.findById(id);
        if (roleOptional.isPresent()) {
            RoleModel role = roleOptional.get();
            role.setName(roleDetails.getName());
            // Добавьте другие поля для обновления, если они есть
            return ResponseEntity.ok(roleService.save(role));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}