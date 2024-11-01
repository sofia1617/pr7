package com.example.project2.service;

import com.example.project2.model.UserModel;
import com.example.project2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserModel user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден с логином: " + login));

        // Логируем информацию о пользователе
        System.out.println("Загружен пользователь: " + user.getLogin());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getLogin())
                .password(user.getPassword())  // Убедитесь, что это хэшированный пароль
                .roles(user.getRole().getName()) // предполагается, что RoleModel имеет метод getName()
                .build();
    }

}
