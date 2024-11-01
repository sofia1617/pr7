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
@RequestMapping("/superuser")
public class SuperUserController {

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
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String listUsers(@RequestParam(value = "search", required = false) String search, Model model) {
        List<UserModel> users = userService.findAll();
        // Используем метод поиска с учетом регистра
        if (search != null && !search.isEmpty()) {
            users = userService.findByNameContainingIgnoreCase(search);
        } else {
            users = userService.findAll();
        }
        model.addAttribute("users", users);
        return "superuser/listUsers";
    }

    @GetMapping("/createUser")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new UserModel());
        model.addAttribute("roles", roleService.findAll());
        return "superuser/createUser";
    }

    @PostMapping("/createUser")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
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
        return "redirect:/superuser/listUsers";
    }

    @GetMapping("/listUsers/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        UserModel user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAll());
        return "superuser/editUser";
    }

    @PostMapping("/listUsers/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
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
        return "redirect:/superuser/listUsers";
    }

    @GetMapping("/deleteUser/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/superuser/listUsers";
    }

    @GetMapping("/listOrders")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
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
        return "superuser/listOrders";
    }


    // Форма создания нового заказа
    @GetMapping("/orders/create")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String showCreateOrderForm(Model model) {
            model.addAttribute("order", new OrderModel());
            model.addAttribute("users", userService.findAll()); // Добавляем список пользователей
            model.addAttribute("products", productService.findAll()); // Добавляем список продуктов
            model.addAttribute("statuses", statusService.findAll()); // Добавляем список статусов

            return "superuser/createOrder";
    }

    // Обработка создания нового заказа
    @PostMapping("/orders")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String createOrder(@ModelAttribute OrderModel order) {
        orderService.save(order);
        return "redirect:/superuser/listOrders";
    }

    // Форма редактирования существующего заказа
    @GetMapping("/orders/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String showEditOrderForm(@PathVariable Long id, Model model) {
        OrderModel order = orderService.findById(id);
        if (order == null) {
            return "redirect:/admin/listOrders";  // или страница ошибки
        }

        model.addAttribute("order", order);
        model.addAttribute("users", userService.findAll()); // Добавляем список пользователей
        model.addAttribute("products", productService.findAll()); // Добавляем список продуктов
        model.addAttribute("statuses", statusService.findAll()); // Добавляем список статусов
        return "superuser/editOrder";
    }


    // Обработка редактирования существующего заказа
    @PostMapping("/orders/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String updateOrder(@PathVariable Long id, @ModelAttribute OrderModel order) {
        order.setId(id);
        orderService.save(order);
        return "redirect:/superuser/listOrders";
    }

    // Удаление заказа
    @GetMapping("/orders/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteById(id);
        return "redirect:/superuser/listOrders";
    }

    // Методы для управления производителями
    @GetMapping("/listManufacturers")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
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
        return "superuser/listManufacturers"; // Убедитесь, что этот путь соответствует пути к шаблону
    }



    @GetMapping("/manufacturers/create")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String showCreateFormManufacturer(Model model) {
        model.addAttribute("manufacturer", new ManufacturerModel());
        return "superuser/createManufacturer";
    }

    @PostMapping("/manufacturers")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String createManufacturer(@ModelAttribute ManufacturerModel manufacturer) {
        manufacturerService.save(manufacturer);
        return "redirect:/superuser/listManufacturers"; // Убедитесь, что перенаправление правильное
    }

    @GetMapping("/manufacturers/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String showEditFormManufacturer(@PathVariable Long id, Model model) {
        ManufacturerModel manufacturer = manufacturerService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid manufacturer ID: " + id));
        model.addAttribute("manufacturer", manufacturer);
        return "superuser/editManufacturer";
    }

    @PostMapping("/manufacturers/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String updateManufacturer(@PathVariable Long id, @ModelAttribute ManufacturerModel manufacturer) {
        manufacturer.setId(id);
        manufacturerService.save(manufacturer);
        return "redirect:/superuser/listManufacturers"; // Убедитесь, что перенаправление правильное
    }

    @GetMapping("/manufacturers/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String deleteManufacturer(@PathVariable Long id) {
        manufacturerService.deleteById(id);
        return "redirect:/superuser/listManufacturers"; // Убедитесь, что перенаправление правильное
    }

    // Список всех статусов с возможностью поиска
    @GetMapping("/listStatuses")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String listStatuses(@RequestParam(value = "search", required = false) String search, Model model) {
        List<StatusModel> statuses;
        if (search != null && !search.isEmpty()) {
            statuses = statusService.findByNameContainingIgnoreCase(search);
        } else {
            statuses = statusService.findAll();
        }
        model.addAttribute("statuses", statuses);
        model.addAttribute("search", search);
        return "superuser/listStatuses";
    }

    // Форма создания нового статуса
    @GetMapping("/statuses/create")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String showCreateStatusForm(Model model) {
        model.addAttribute("status", new StatusModel());
        return "superuser/createStatus";
    }

    // Обработка создания нового статуса
    @PostMapping("/statuses")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String createStatus(@ModelAttribute StatusModel status) {
        statusService.save(status);
        return "redirect:/superuser/listStatuses";
    }

    // Форма редактирования существующего статуса
    @GetMapping("/statuses/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String showEditStatusForm(@PathVariable Long id, Model model) {
        StatusModel status = statusService.findById(id);
        if (status == null) {
            throw new IllegalArgumentException("Invalid status ID: " + id);
        }
        model.addAttribute("status", status);
        return "superuser/editStatus";
    }


    // Обработка редактирования существующего статуса
    @PostMapping("/statuses/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String updateStatus(@PathVariable Long id, @ModelAttribute StatusModel status) {
        status.setId(id);
        statusService.save(status);
        return "redirect:/superuser/listStatuses";
    }

    // Удаление статуса
    @GetMapping("/statuses/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String deleteStatus(@PathVariable Long id) {
        statusService.deleteById(id);
        return "redirect:/superuser/listStatuses";
    }


    // Список товаров
    @GetMapping("/listProducts")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String listProducts(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "name") String sortField,
                               @RequestParam(defaultValue = "asc") String sortDir,
                               @RequestParam(value = "search", required = false) String search,
                               Model model) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Page<ProductModel> productsPage;

        // Пагинация и поиск по имени
        if (search != null && !search.isEmpty()) {
            productsPage = productService.findByNameContainingIgnoreCase(search, PageRequest.of(page, size, sort));
        } else {
            productsPage = productService.findPaginated(PageRequest.of(page, size, sort));
        }


        model.addAttribute("productsPage", productsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("search", search);

        return "superuser/listProducts"; // Убедитесь, что этот шаблон существует
    }

    // Форма создания нового товара
    @GetMapping("/products/create")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new ProductModel());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("manufacturers", manufacturerService.findAll());
        return "superuser/createProduct"; // Шаблон для создания товара
    }

    // Обработка создания нового товара
    @PostMapping("/products")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String createProduct(@ModelAttribute ProductModel product) {
        productService.save(product);
        return "redirect:/superuser/listProducts"; // Перенаправление на список товаров
    }

    @GetMapping("/products/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String showEditFormProduct(@PathVariable Long id, Model model) {
        ProductModel product = productService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("manufacturers", manufacturerService.findAll());
        return "superuser/editProduct"; // Имя HTML-шаблона
    }

    @PostMapping("/products/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String updateProduct(@PathVariable Long id, @ModelAttribute ProductModel product) {
        product.setId(id); // Убедитесь, что ID корректно установлен
        productService.save(product);
        return "redirect:/superuser/listProducts"; // Перенаправление после редактирования
    }

    // Удаление продукта
    @GetMapping("/products/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/superuser/listProducts"; // Перенаправление на список продуктов после удаления
    }




    @GetMapping("/listCategories")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
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
        return "superuser/listCategories"; // Убедитесь, что этот шаблон существует
    }



    // Форма создания новой категории
    @GetMapping("/categories/create")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String showCreateCategoryForm(Model model) {
        model.addAttribute("category", new CategoryModel());
        return "superuser/createCategory"; // Убедитесь, что этот шаблон существует
    }

    // Обработка создания новой категории
    @PostMapping("/categories")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String createCategory(@ModelAttribute CategoryModel category) {
        categoryService.save(category);
        return "redirect:/superuser/listCategories"; // Перенаправление на список категорий после создания
    }

    // Форма редактирования существующей категории
    @GetMapping("/categories/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String showEditCategoryForm(@PathVariable Long id, Model model) {
        CategoryModel category = categoryService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + id));
        model.addAttribute("category", category);
        return "superuser/editCategory"; // Убедитесь, что этот шаблон существует
    }

    // Обработка редактирования существующей категории
    @PostMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String updateCategory(@PathVariable Long id, @ModelAttribute CategoryModel category) {
        category.setId(id);
        categoryService.save(category);
        return "redirect:/superuser/listCategories"; // Перенаправление на список категорий после редактирования
    }

    // Удаление категории
    @GetMapping("/categories/soft-delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String softDeleteCategory(@PathVariable Long id) {
        categoryService.softDeleteById(id);
        return "redirect:/superuser/listCategories"; // Перенаправление на список категорий после удаления
    }

    @GetMapping("/categories/hard-delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String hardDeleteCategory(@PathVariable Long id) {
        categoryService.hardDeleteById(id);
        return "redirect:/superuser/listCategories"; // Перенаправление на список категорий после удаления
    }

    @GetMapping("/categories/restore/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String restoreCategory(@PathVariable Long id) {
        categoryService.restoreById(id);
        return "redirect:/superuser/listCategories"; // Перенаправление на список категорий после восстановления
    }

    // Список всех ролей с возможностью поиска
    @GetMapping("/listRoles")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String listRoles(@RequestParam(value = "search", required = false) String search, Model model) {
        List<RoleModel> roles;
        // Используем метод поиска с учетом регистра
        if (search != null && !search.isEmpty()) {
            roles = roleService.findByNameContainingIgnoreCase(search);
        } else {
            roles = roleService.findAll();
        }
        model.addAttribute("roles", roles);
        model.addAttribute("search", search);
        return "superuser/listRoles";
    }

    // Форма создания новой роли
    @GetMapping("/roles/create")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String showCreateRoleForm(Model model) {
        model.addAttribute("role", new RoleModel());
        return "superuser/createRoles";
    }

    // Обработка создания новой роли
    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String createRole(@ModelAttribute RoleModel role) {
        roleService.save(role);
        return "redirect:/superuser/listRoles";
    }

    // Форма редактирования существующей роли
    @GetMapping("/roles/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String showEditRoleForm(@PathVariable Long id, Model model) {
        RoleModel role = roleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + id));
        model.addAttribute("role", role);
        return "superuser/editRoles";
    }

    // Обработка редактирования существующей роли
    @PostMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String updateRole(@PathVariable Long id, @ModelAttribute RoleModel role) {
        role.setId(id);
        roleService.save(role);
        return "redirect:/superuser/listRoles";
    }

    // Удаление роли
    @GetMapping("/roles/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String deleteRole(@PathVariable Long id) {
        roleService.deleteById(id);
        return "redirect:/superuser/listRoles";
    }
}
