package com.example.project2.controller;

import com.example.project2.model.ChartModel;
import com.example.project2.service.ChartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/statuses")
public class StatusControllerAPI {

    private final ChartService chartService;

    public StatusControllerAPI(ChartService chartService) {
        this.chartService = chartService;
    }

    @GetMapping
    public List<ChartModel> getAllStatuses() {
        return chartService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChartModel> getStatusById(@PathVariable Long id) {
        Optional<ChartModel> status = Optional.ofNullable(chartService.findById(id));
        return status.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ChartModel createStatus(@RequestBody ChartModel chart) {
        return chartService.save(chart);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChartModel> updateStatus(@PathVariable Long id, @RequestBody ChartModel statusDetails) {
        Optional<ChartModel> statusOptional = Optional.ofNullable(chartService.findById(id));
        if (statusOptional.isPresent()) {
            ChartModel chart = statusOptional.get();
            chart.setName(statusDetails.getName());
            // Добавьте другие поля для обновления, если они есть

            return ResponseEntity.ok(chartService.save(chart));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        chartService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}