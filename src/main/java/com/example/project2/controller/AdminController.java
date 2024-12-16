package com.example.project2.controller;

import com.example.project2.model.*;
import com.example.project2.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/adminbd")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ServicesService servicesService;

    @Autowired
    private Product_and_materialsService productandmaterialsService;

    @Autowired
    private ChartService chartService;

    @Autowired
    private RecordsService recordsService;

    @Autowired
    private ClientsService clientsService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/listUsers")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
    public String listUsers(@RequestParam(value = "search", required = false) String search, Model model) {
        List<UserModel> users;
        // Используем метод поиска с учетом регистра
        if (search != null && !search.isEmpty()) {
            users = userService.findByNameContainingIgnoreCase(search);
        } else {
            users = userService.findAll();
        }
        model.addAttribute("users", users);
        return "adminbd/listUsers";
    }

    @GetMapping("/createUser")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new UserModel());
        model.addAttribute("roles", roleService.findAll());
        return "adminbd/createUser";
    }

    @PostMapping("/createUser")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
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
        return "redirect:/adminbd/listUsers";
    }

    @GetMapping("/listUsers/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
    public String showEditForm(@PathVariable Long id, Model model) {
        UserModel user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAll());
        return "adminbd/editUser";
    }

    @PostMapping("/listUsers/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
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
        return "redirect:/adminbd/listUsers";
    }

    @GetMapping("/deleteUser/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/adminbd/listUsers";
    }


    @GetMapping("/listOrders")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
    public String accessDeniedOrders(Model model) {
        return accessDenied(model);
    }


    @GetMapping("/listManufacturers")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
    public String accessDeniedManufacturers(Model model) {
        return accessDenied(model);
    }

    @GetMapping("/listStatuses")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
    public String accessDeniedStatuses(Model model) {
        return accessDenied(model);
    }


    @GetMapping("/listProducts")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
    public String accessDeniedProduct(Model model) {
        return accessDenied(model);
    }

    @GetMapping("/listCategories")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String accessDeniedCategories(Model model) {
        return accessDenied(model);
    }


    // Запрет доступа к таблице Roles
    @GetMapping("/listRoles")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
    public String accessDeniedRoles(Model model) {
        return accessDenied(model);
    }

    // Метод для отображения сообщения о запрете доступа
    private String accessDenied(Model model) {
        model.addAttribute("message", "Доступ запрещен к этой странице.");
        return "adminbd/access-denied"; // Убедитесь, что у вас есть этот шаблон
    }

    /**
     * Страница управления экспортом и импортом данных.
     */
    @GetMapping("/data-management")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
    public String showExportImportPage() {
        return "adminbd/export-import"; // Имя вашего HTML-шаблона
    }


    /**
     * Экспорт данных из всех таблиц в формате CSV.
     */
    @GetMapping("/export/csv")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
    public ResponseEntity<byte[]> exportAllToCSV() {
        String csvData = adminService.exportAllToCSV(); // Сервис для всех таблиц
        byte[] csvBytes = csvData.getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "all_data.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }

    /**
     * Экспорт данных из всех таблиц в формате SQL.
     */
    @GetMapping("/export/sql")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
    public ResponseEntity<byte[]> exportAllToSQL() {
        String sqlData = adminService.exportAllToSQL(); // Вызов через экземпляр
        byte[] sqlBytes = sqlData.getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "all_data.sql");

        return ResponseEntity.ok()
                .headers(headers)
                .body(sqlBytes);
    }

    /**
     * Импорт данных в базу из файла CSV для всех таблиц.
     */
    @PostMapping("/import/csv")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
    public String importAllFromCSV(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            adminService.importAllFromCSV(file.getInputStream()); // Сервис для всех таблиц
            // Успешное сообщение о завершении импорта
            redirectAttributes.addFlashAttribute("message", "Импорт CSV выполнен успешно!");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при импорте CSV: " + e.getMessage(), e);
        }
        return "redirect:/adminbd/data-management";  // Редирект на страницу управления экспортом и импортом
    }

    /**
     * Импорт данных в базу из файла SQL для всех таблиц.
     */
    @PostMapping("/import/sql")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
    public String importAllFromSQL(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл не выбран для импорта.");
        }
        try {
            adminService.importAllFromSQL(file.getInputStream()); // Сервис для всех таблиц
            // Успешное сообщение о завершении импорта
            redirectAttributes.addFlashAttribute("message", "Импорт SQL выполнен успешно!");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла: " + e.getMessage(), e);
        }
        return "redirect:/adminbd/data-management";  // Редирект на страницу управления экспортом и импортом
    }

    @GetMapping("/userStats")
    @PreAuthorize("hasAuthority('ROLE_ADMINBD')")
    public String showUserStatistics(Model model) {
        long adminbdCount = userService.countByRoleName("AdminBD");
        long adminsalonaCount = userService.countByRoleName("AdminSalona");
        long employeeCount = userService.countByRoleName("Employee");

        model.addAttribute("adminbdCount", adminbdCount);
        model.addAttribute("adminsalonaCount", adminsalonaCount);
        model.addAttribute("employeeCount", employeeCount);

        return "adminbd/userStats"; // Шаблон для отображения статистики
    }

}
