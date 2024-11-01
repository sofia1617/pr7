package com.example.project2.service;

import com.example.project2.model.OrderModel;
import com.example.project2.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderModel> findAll() {
        return orderRepository.findAll();
    }

    public OrderModel findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public OrderModel save(OrderModel order) {
        return orderRepository.save(order);
    }

    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    // Метод для поиска заказов по номеру заказа или имени пользователя, игнорируя регистр
    public List<OrderModel> search(String keyword) {
        return orderRepository.findByNumberOrderContainingIgnoreCaseOrUserNameContainingIgnoreCase(keyword, keyword);
    }

}
