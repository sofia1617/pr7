package com.example.project2.controller;

import com.example.project2.model.RoleModel;
import com.example.project2.model.UserModel;
import com.example.project2.service.RoleService;
import com.example.project2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String listUsers(Model model) {
        List<UserModel> users = userService.findAll();
        model.addAttribute("users", users);
        return "listUsers";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new UserModel());
        model.addAttribute("roles", roleService.findAll());
        return "createUser";
    }

    @PostMapping
    public String createUser(@ModelAttribute UserModel user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Пароль не должен быть пустым");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() != null && user.getRole().getId() != null) {
            RoleModel role = roleService.findById(user.getRole().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role ID:" + user.getRole().getId()));
            user.setRole(role);
        }

        userService.save(user);
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        UserModel user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID:" + id));
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAll());
        return "editUser";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute UserModel user) {
        UserModel existingUser = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID:" + id));

        existingUser.setName(user.getName());
        existingUser.setSurname(user.getSurname());
        existingUser.setPathronymic(user.getPathronymic());
        existingUser.setLogin(user.getLogin());

        if (user.getRole() != null && user.getRole().getId() != null) {
            RoleModel newRole = roleService.findById(user.getRole().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role ID:" + user.getRole().getId()));
            existingUser.setRole(newRole);
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userService.save(existingUser);
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/users";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserModel());
        model.addAttribute("roles", roleService.findAll());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserModel user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleService.findAll());
            return "register";
        }

        RoleModel role = roleService.findByName("USER");
        user.setRole(role);

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            model.addAttribute("errorMessage", "Пароль не должен быть пустым");
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return "redirect:/auth/login";
    }
}
