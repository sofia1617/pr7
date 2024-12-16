package com.example.project2.controller;

import com.example.project2.model.LoginDto;
import com.example.project2.model.RoleModel;
import com.example.project2.model.UserModel;
import com.example.project2.service.CustomUserDetailsService;
import com.example.project2.service.RoleService;
import com.example.project2.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Показать страницу входа
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "authentication/login";
    }

    // Показать страницу регистрации
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserModel());
        return "authentication/register";
    }

    // Регистрация пользователя
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("employee") UserModel employee, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleService.findAll());
            return "authentication/register";
        }

        // Проверка на существование пользователя с таким логином
        if (userService.existsByLogin(employee.getLogin())) {
            model.addAttribute("errorMessage", "Пользователь с таким логином уже существует");
            model.addAttribute("roles", roleService.findAll());
            return "authentication/register";
        }

        RoleModel role = roleService.findByName("Employee");
        employee.setRole(role);

        if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
            model.addAttribute("errorMessage", "Пароль не должен быть пустым");
            model.addAttribute("roles", roleService.findAll());
            return "authentication/register";
        }

        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        userService.save(employee);
        return "redirect:/auth/login";
    }

    // Логин пользователя
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginDto") LoginDto loginDto, BindingResult result,
                        RedirectAttributes redirectAttributes, HttpSession session) {
        if (result.hasErrors()) {
            return "authentication/login";
        }
        try {
            System.out.println("Попытка аутентификации пользователя: " + loginDto.getLogin());
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginDto.getLogin(), loginDto.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);

            // Проверка успешной аутентификации
            if (authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                session.setAttribute("Employee", authentication.getPrincipal());

                // Перенаправление в зависимости от роли
                String redirectUrl = determineRedirectUrl(authentication);
                return "redirect:" + redirectUrl;
            }
        } catch (AuthenticationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Неверный логин или пароль");
            return "redirect:/auth/login";
        }

        redirectAttributes.addFlashAttribute("errorMessage", "Неверный логин или пароль");
        return "redirect:/auth/login";
    }

    // Метод для определения URL перенаправления в зависимости от роли
    private String determineRedirectUrl(Authentication authentication) {
        // Проверяем роли пользователя
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_Employee"))) {
            return "/employee/listChart";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_AdminBD"))) {
            return "/adminbd/listUsers";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_AdminSalona"))) {
            return "/adminsalona/listUsers";
        } else {
            throw new IllegalArgumentException("Не существует");
        }
    }

}
