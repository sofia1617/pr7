package com.example.project2.service;

import com.example.project2.model.RoleModel;
import com.example.project2.model.UserModel;
import com.example.project2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;


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

    @Autowired
    private DataSource dataSource;

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
    public UserModel findByUsername(String username) {
        return userRepository.findByUsername(username); // Найдите одного пользователя по имени
    }

    public long countByRoleName(String roleName) {
        return userRepository.countByRoleName(roleName);
    }


    public String exportToCSV() {
        List<UserModel> users = findAll();
        StringBuilder csvBuilder = new StringBuilder("ID,Name,Surname,Login,Role\n");
        for (UserModel user : users) {
            csvBuilder.append(user.getId()).append(",")
                    .append(user.getName()).append(",")
                    .append(user.getSurname()).append(",")
                    .append(user.getLogin()).append(",")
                    .append(user.getRole().getName()).append("\n");
        }
        return csvBuilder.toString();
    }

    public String exportToSQL() {
        List<UserModel> users = findAll();
        StringBuilder sqlBuilder = new StringBuilder();
        for (UserModel user : users) {
            sqlBuilder.append("INSERT INTO users (id, name, surname, login, password, role_id) VALUES (")
                    .append(user.getId()).append(", '")
                    .append(user.getName()).append("', '")
                    .append(user.getSurname()).append("', '")
                    .append(user.getLogin()).append("', '")
                    .append(user.getPassword()).append("', ")
                    .append(user.getRole().getId()).append(");\n");
        }
        return sqlBuilder.toString();
    }

    public void importFromCSV(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            List<String> lines = reader.lines().skip(1).collect(Collectors.toList());
            for (String line : lines) {
                String[] fields = line.split(",");
                UserModel user = new UserModel();
                user.setId(Long.parseLong(fields[0]));
                user.setName(fields[1]);
                user.setSurname(fields[2]);
                user.setLogin(fields[3]);
                RoleModel role = roleService.findByName(fields[4]);
                user.setRole(role);
                save(user);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении CSV: " + e.getMessage());
        }
    }

    public void importFromSQL(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("--")) {
                        statement.execute(line);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка выполнения SQL: " + e.getMessage(), e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения SQL-файла: " + e.getMessage(), e);
        }
    }
}
