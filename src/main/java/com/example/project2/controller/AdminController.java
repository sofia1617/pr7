package com.example.project2.controller;

import com.example.project2.model.*;
import com.example.project2.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ManufacturerService manufacturerService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/listUsers")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String listUsers(@RequestParam(value = "search", required = false) String search, Model model) {
        List<UserModel> users = userService.findAll();
        // Используем метод поиска с учетом регистра
        if (search != null && !search.isEmpty()) {
            users = userService.findByNameContainingIgnoreCase(search);
        } else {
            users = userService.findAll();
        }
        model.addAttribute("users", users);
        return "admin/listUsers";
    }

    @GetMapping("/createUser")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new UserModel());
        model.addAttribute("roles", roleService.findAll());
        return "admin/createUser";
    }

    @PostMapping("/createUser")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String createUser(@ModelAttribute UserModel user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Пароль не должен быть пустым");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() != null && user.getRole().getId() != null) {
            RoleModel role = roleService.findById(user.getRole().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + user.getRole().getId()));
            user.setRole(role);
        }
        userService.save(user);
        return "redirect:/admin/listUsers";
    }

    @GetMapping("/listUsers/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        UserModel user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAll());
        return "admin/editUser";
    }

    @PostMapping("/listUsers/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String updateUser(@PathVariable Long id, @ModelAttribute UserModel user) {
        UserModel existingUser = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));

        existingUser.setName(user.getName());
        existingUser.setSurname(user.getSurname());
        existingUser.setPathronymic(user.getPathronymic());
        existingUser.setLogin(user.getLogin());

        if (user.getRole() != null && user.getRole().getId() != null) {
            RoleModel newRole = roleService.findById(user.getRole().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + user.getRole().getId()));
            existingUser.setRole(newRole);
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userService.save(existingUser);
        return "redirect:/admin/listUsers";
    }

    @GetMapping("/deleteUser/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin/listUsers";
    }

    @GetMapping("/listOrders")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String listOrders(@RequestParam(value = "search", required = false) String search, Model model) {
        List<OrderModel> orders;
        if (search != null && !search.isEmpty()) {
            // Используем параметр `search` для поиска и по номеру заказа, и по имени пользователя
            orders = orderService.search(search);
        } else {
            orders = orderService.findAll();
        }
        model.addAttribute("orders", orders);
        model.addAttribute("search", search);
        return "admin/listOrders";
    }


    // Форма создания нового заказа
    @GetMapping("/orders/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showCreateOrderForm(Model model) {
        model.addAttribute("order", new OrderModel());
        model.addAttribute("users", userService.findAll()); // Добавляем список пользователей
        model.addAttribute("products", productService.findAll()); // Добавляем список продуктов
        model.addAttribute("statuses", statusService.findAll()); // Добавляем список статусов

        return "admin/createOrder";
    }


    // Обработка создания нового заказа
    @PostMapping("/orders")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String createOrder(@ModelAttribute OrderModel order) {
        orderService.save(order);
        return "redirect:/admin/listOrders";
    }

    // Форма редактирования существующего заказа
    @GetMapping("/orders/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showEditOrderForm(@PathVariable Long id, Model model) {
        OrderModel order = orderService.findById(id);
        if (order == null) {
            return "redirect:/admin/listOrders";  // или страница ошибки
        }

        model.addAttribute("order", order);
        model.addAttribute("users", userService.findAll()); // Добавляем список пользователей
        model.addAttribute("products", productService.findAll()); // Добавляем список продуктов
        model.addAttribute("statuses", statusService.findAll()); // Добавляем список статусов

        return "admin/editOrder";
    }



    // Обработка редактирования существующего заказа
    @PostMapping("/orders/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String updateOrder(@PathVariable Long id, @ModelAttribute OrderModel order) {
        order.setId(id);
        orderService.save(order);
        return "redirect:/admin/listOrders";
    }

    // Удаление заказа
    @GetMapping("/orders/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteById(id);
        return "redirect:/admin/listOrders";
    }

    // Методы для управления производителями
    @GetMapping("/listManufacturers")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String listManufacturers(@RequestParam(value = "search", required = false) String search, Model model) {
        List<ManufacturerModel> manufacturers;

        // Используем метод поиска с учетом регистра
        if (search != null && !search.isEmpty()) {
            manufacturers = manufacturerService.findByNameContainingIgnoreCase(search);
        } else {
            manufacturers = manufacturerService.findAll();
        }

        model.addAttribute("manufacturers", manufacturers);
        model.addAttribute("search", search);
        return "admin/listManufacturers"; // Убедитесь, что этот путь соответствует пути к шаблону
    }



    @GetMapping("/manufacturers/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showCreateFormManufacturer(Model model) {
        model.addAttribute("manufacturer", new ManufacturerModel());
        return "admin/createManufacturer";
    }

    @PostMapping("/manufacturers")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String createManufacturer(@ModelAttribute ManufacturerModel manufacturer) {
        manufacturerService.save(manufacturer);
        return "redirect:/admin/listManufacturers"; // Убедитесь, что перенаправление правильное
    }

    @GetMapping("/manufacturers/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showEditFormManufacturer(@PathVariable Long id, Model model) {
        ManufacturerModel manufacturer = manufacturerService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid manufacturer ID: " + id));
        model.addAttribute("manufacturer", manufacturer);
        return "admin/editManufacturer";
    }

    @PostMapping("/manufacturers/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String updateManufacturer(@PathVariable Long id, @ModelAttribute ManufacturerModel manufacturer) {
        manufacturer.setId(id);
        manufacturerService.save(manufacturer);
        return "redirect:/admin/listManufacturers"; // Убедитесь, что перенаправление правильное
    }

    @GetMapping("/manufacturers/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteManufacturer(@PathVariable Long id) {
        manufacturerService.deleteById(id);
        return "redirect:/admin/listManufacturers"; // Убедитесь, что перенаправление правильное
    }

    // Список всех статусов с возможностью поиска
    @GetMapping("/listStatuses")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String listStatuses(@RequestParam(value = "search", required = false) String search, Model model) {
        List<StatusModel> statuses;
        if (search != null && !search.isEmpty()) {
            statuses = statusService.findByNameContainingIgnoreCase(search);
        } else {
            statuses = statusService.findAll();
        }
        model.addAttribute("statuses", statuses);
        model.addAttribute("search", search);
        return "admin/listStatuses";
    }

    // Форма создания нового статуса
    @GetMapping("/statuses/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showCreateStatusForm(Model model) {
        model.addAttribute("status", new StatusModel());
        return "admin/createStatus";
    }

    // Обработка создания нового статуса
    @PostMapping("/statuses")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String createStatus(@ModelAttribute StatusModel status) {
        statusService.save(status);
        return "redirect:/admin/listStatuses";
    }

    // Форма редактирования существующего статуса
    @GetMapping("/statuses/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showEditStatusForm(@PathVariable Long id, Model model) {
        StatusModel status = statusService.findById(id);
        if (status == null) {
            throw new IllegalArgumentException("Invalid status ID: " + id);
        }
        model.addAttribute("status", status);
        return "admin/editStatus";
    }


    // Обработка редактирования существующего статуса
    @PostMapping("/statuses/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String updateStatus(@PathVariable Long id, @ModelAttribute StatusModel status) {
        status.setId(id);
        statusService.save(status);
        return "redirect:/admin/listStatuses";
    }

    // Удаление статуса
    @GetMapping("/statuses/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteStatus(@PathVariable Long id) {
        statusService.deleteById(id);
        return "redirect:/admin/listStatuses";
    }


    @GetMapping("/listProducts")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String accessDeniedProduct(Model model) {
        return accessDenied(model);
    }



    @GetMapping("/listCategories")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String listCategories(@RequestParam(value = "search", required = false) String search,
                                 @RequestParam(value = "sort", required = false, defaultValue = "asc") String sortDirection,
                                 @RequestParam(value = "deleted", required = false, defaultValue = "false") boolean showDeleted, Model model) {


        List<CategoryModel> categories = categoryService.findAll();


        if (showDeleted) {
            categories = categoryService.findAll(); // Заменено на findAll() для получения всех категорий
        } else {
            categories = categoryService.findActiveCategories();
        }

        // Применить поиск по имени
        if (search != null && !search.isEmpty()) {
            categories = categories.stream()
                    .filter(category -> category.getName().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Применить сортировку
        if ("asc".equals(sortDirection)) {
            categories = categories.stream()
                    .sorted(Comparator.comparing(CategoryModel::getName)) // Сортировка по возрастанию (А-Я)
                    .collect(Collectors.toList());
        } else {
            categories = categories.stream()
                    .sorted(Comparator.comparing(CategoryModel::getName).reversed()) // Сортировка по убыванию (Я-А)
                    .collect(Collectors.toList());
        }



        model.addAttribute("categories", categories);
        model.addAttribute("currentSort", sortDirection);
        model.addAttribute("search", search);
        model.addAttribute("deleted", !showDeleted);
        return "admin/listCategories"; // Убедитесь, что этот шаблон существует
    }



    // Форма создания новой категории
    @GetMapping("/categories/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showCreateCategoryForm(Model model) {
        model.addAttribute("category", new CategoryModel());
        return "admin/createCategory"; // Убедитесь, что этот шаблон существует
    }

    // Обработка создания новой категории
    @PostMapping("/categories")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String createCategory(@ModelAttribute CategoryModel category) {
        categoryService.save(category);
        return "redirect:/admin/listCategories"; // Перенаправление на список категорий после создания
    }

    // Форма редактирования существующей категории
    @GetMapping("/categories/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showEditCategoryForm(@PathVariable Long id, Model model) {
        CategoryModel category = categoryService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + id));
        model.addAttribute("category", category);
        return "admin/editCategory"; // Убедитесь, что этот шаблон существует
    }

    // Обработка редактирования существующей категории
    @PostMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String updateCategory(@PathVariable Long id, @ModelAttribute CategoryModel category) {
        category.setId(id);
        categoryService.save(category);
        return "redirect:/admin/listCategories"; // Перенаправление на список категорий после редактирования
    }

    // Удаление категории
    @GetMapping("/categories/soft-delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String softDeleteCategory(@PathVariable Long id) {
        categoryService.softDeleteById(id);
        return "redirect:/admin/listCategories"; // Перенаправление на список категорий после удаления
    }

    @GetMapping("/categories/hard-delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String hardDeleteCategory(@PathVariable Long id) {
        categoryService.hardDeleteById(id);
        return "redirect:/admin/listCategories"; // Перенаправление на список категорий после удаления
    }

    @GetMapping("/categories/restore/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String restoreCategory(@PathVariable Long id) {
        categoryService.restoreById(id);
        return "redirect:/admin/listCategories"; // Перенаправление на список категорий после восстановления
    }

    // Запрет доступа к таблице Roles
    @GetMapping("/listRoles")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String accessDeniedRoles(Model model) {
        return accessDenied(model);
    }

    // Метод для отображения сообщения о запрете доступа
    private String accessDenied(Model model) {
        model.addAttribute("message", "Доступ запрещен к этой странице.");
        return "admin/access-denied"; // Убедитесь, что у вас есть этот шаблон
    }


}
