package com.example.project2.controller;

import com.example.project2.model.StatusModel;
import com.example.project2.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/statuses")
public class StatusControllerAPI {

    private final StatusService statusService;

    public StatusControllerAPI(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping
    public List<StatusModel> getAllStatuses() {
        return statusService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatusModel> getStatusById(@PathVariable Long id) {
        Optional<StatusModel> status = Optional.ofNullable(statusService.findById(id));
        return status.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public StatusModel createStatus(@RequestBody StatusModel status) {
        return statusService.save(status);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatusModel> updateStatus(@PathVariable Long id, @RequestBody StatusModel statusDetails) {
        Optional<StatusModel> statusOptional = Optional.ofNullable(statusService.findById(id));
        if (statusOptional.isPresent()) {
            StatusModel status = statusOptional.get();
            status.setName(statusDetails.getName());
            // Добавьте другие поля для обновления, если они есть
            return ResponseEntity.ok(statusService.save(status));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        statusService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}