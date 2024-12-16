package com.example.project2.repository;

import com.example.project2.model.UserModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    // Метод для подсчета пользователей по роли
    long countByRoleName(String roleName); // Подсчет пользователей по имени роли

    Optional<UserModel> findByLogin(String login); // Поиск по логину

    boolean existsByLogin(@NotBlank(message = "Поле не может быть пустым")
                          @Size(min = 3, max = 25, message = "Логин должен состоять из 3 - 25 символов")
                          String login);

    // Метод для поиска пользователей по имени
    List<UserModel> findByNameContainingIgnoreCase(String name); // Поиск пользователей по имени

    public UserModel findByUsername(String username); // Не возвращаем список

}
