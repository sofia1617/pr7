package com.example.project2.controller;

import com.example.project2.model.RoleModel;
import com.example.project2.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // Список всех ролей с возможностью поиска
    @GetMapping
    public String listRoles(@RequestParam(value = "search", required = false) String search, Model model) {
        List<RoleModel> roles;
        if (search != null && !search.isEmpty()) {
            roles = List.of(roleService.findByName(search)); // Поиск ролей по имени
        } else {
            roles = roleService.findAll(); // Получить все роли
        }
        model.addAttribute("roles", roles);
        model.addAttribute("search", search); // Передать поисковый запрос в модель
        return "listRoles"; // Имя HTML-шаблона
    }

    // Форма создания новой роли
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("role", new RoleModel());
        return "createRoles"; // Имя HTML-шаблона для создания роли
    }

    // Создать новую роль
    @PostMapping
    public String createRole(@ModelAttribute RoleModel role) {
        roleService.save(role);
        return "redirect:/roles"; // Перенаправление после создания
    }

    // Форма редактирования роли
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        RoleModel role = roleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + id)); // Обработка Optional
        model.addAttribute("role", role);
        return "editRoles"; // Имя HTML-шаблона для редактирования роли
    }

    // Обновление роли
    @PostMapping("/{id}")
    public String updateRole(@PathVariable Long id, @ModelAttribute RoleModel role) {
        role.setId(id);
        roleService.save(role);
        return "redirect:/roles"; // Перенаправление после редактирования
    }

    // Удаление роли
    @GetMapping("/delete/{id}")
    public String deleteRole(@PathVariable Long id) {
        roleService.deleteById(id);
        return "redirect:/roles"; // Перенаправление после удаления
    }
}
