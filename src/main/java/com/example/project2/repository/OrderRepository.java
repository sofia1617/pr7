package com.example.project2.repository;

import com.example.project2.model.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {
    // Поиск заказов по номеру заказа или имени пользователя, игнорируя регистр
    List<OrderModel> findByNumberOrderContainingIgnoreCaseOrUserNameContainingIgnoreCase(String numberOrder, String userName);
}
