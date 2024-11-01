package com.example.project2.controller;

import com.example.project2.model.OrderModel;
import com.example.project2.service.OrderService;
import com.example.project2.service.UserService;
import com.example.project2.service.ProductService;
import com.example.project2.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderControllerAPI {

    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;
    private final StatusService statusService;

    @Autowired
    public OrderControllerAPI(OrderService orderService, UserService userService, ProductService productService, StatusService statusService) {
        this.orderService = orderService;
        this.userService = userService;
        this.productService = productService;
        this.statusService = statusService;
    }

    @GetMapping
    public List<OrderModel> getAllOrders() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderModel> getOrderById(@PathVariable Long id) {
        Optional<OrderModel> order = Optional.ofNullable(orderService.findById(id));
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public OrderModel createOrder(@RequestBody OrderModel order) {
        return orderService.save(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderModel> updateOrder(@PathVariable Long id, @RequestBody OrderModel orderDetails) {
        Optional<OrderModel> orderOptional = Optional.ofNullable(orderService.findById(id));
        if (orderOptional.isPresent()) {
            OrderModel order = orderOptional.get();
            order.setUser(orderDetails.getUser());
            order.setProducts(orderDetails.getProducts());
            order.setStatus(orderDetails.getStatus());
            // Добавьте другие поля для обновления, если они есть
            return ResponseEntity.ok(orderService.save(order));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}