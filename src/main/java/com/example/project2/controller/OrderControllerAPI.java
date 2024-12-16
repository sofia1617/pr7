package com.example.project2.controller;

import com.example.project2.model.ServicesModel;
import com.example.project2.service.ServicesService;
import com.example.project2.service.UserService;
import com.example.project2.service.RecordsService;
import com.example.project2.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderControllerAPI {

    private final ServicesService servicesService;
    private final UserService userService;
    private final RecordsService recordsService;
    private final ChartService chartService;

    @Autowired
    public OrderControllerAPI(ServicesService servicesService, UserService userService, RecordsService recordsService, ChartService chartService) {
        this.servicesService = servicesService;
        this.userService = userService;
        this.recordsService = recordsService;
        this.chartService = chartService;
    }

    @GetMapping
    public List<ServicesModel> getAllOrders() {
        return servicesService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicesModel> getOrderById(@PathVariable Long id) {
        Optional<ServicesModel> order = Optional.ofNullable(servicesService.findById(id));
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ServicesModel createOrder(@RequestBody ServicesModel service) {
        return servicesService.save(service);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicesModel> updateOrder(@PathVariable Long id, @RequestBody ServicesModel orderDetails) {
        Optional<ServicesModel> orderOptional = Optional.ofNullable(servicesService.findById(id));
        if (orderOptional.isPresent()) {
            ServicesModel order = orderOptional.get();
            order.setUser(orderDetails.getUser());
            order.setNumberRecords(orderDetails.getNumberRecords());
            order.setChart(orderDetails.getChart());
            // Добавьте другие поля для обновления, если они есть
            return ResponseEntity.ok(servicesService.save(order));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        servicesService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}