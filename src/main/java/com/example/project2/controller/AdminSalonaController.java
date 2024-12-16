package com.example.project2.controller;

import com.example.project2.model.*;
import com.example.project2.service.*;
import jakarta.validation.Valid;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/adminsalona")
public class AdminSalonaController {

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
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SpecializationService specializationService;

    @GetMapping("/listUsers")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String listUsers(@RequestParam(value = "search", required = false) String search, Model model) {
        List<UserModel> users;
        // Используем метод поиска с учетом регистра
        if (search != null && !search.isEmpty()) {
            users = userService.findByNameContainingIgnoreCase(search);
        } else {
            users = userService.findAll();
        }
        model.addAttribute("users", users);
        return "adminsalona/listUsers";
    }

    @GetMapping("/createUser")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new UserModel());
        model.addAttribute("roles", roleService.findAll());
        return "adminsalona/createUser";
    }

    @PostMapping("/createUser")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
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
        return "redirect:/adminsalona/listUsers";
    }

    @GetMapping("/listUsers/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String showEditForm(@PathVariable Long id, Model model) {
        UserModel user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAll());
        return "adminsalona/editUser";
    }

    @PostMapping("/listUsers/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
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
        return "redirect:/adminsalona/listUsers";
    }

    @GetMapping("/deleteUser/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/adminsalona/listUsers";
    }

    @GetMapping("/listServices")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String listServices(@RequestParam(value = "search", required = false) String search, Model model) {
        List<ServicesModel> services;

        if (search != null && !search.isEmpty()) {
            // Поиск по запросу
            services = servicesService.search(search);
        } else {
            // Получаем только не удалённые услуги
            services = servicesService.findAllNotDeleted();
        }

        // Добавляем данные в модель
        model.addAttribute("services", services);
        model.addAttribute("search", search);

        return "adminsalona/listServices"; // Возвращаем имя шаблона для отображения
    }


    @GetMapping("/createServices")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String showCreateServiceForm(Model model) {
        model.addAttribute("service", new ServicesModel()); // Передаем пустую модель для нового заказа
        model.addAttribute("users", userService.findAll()); // Список пользователей
        model.addAttribute("records", recordsService.findAll()); // Список записей
        model.addAttribute("charts", chartService.findAll()); // Список дней недели
        return "adminsalona/createServices";  // Шаблон для создания услуги
    }


    @PostMapping("/createServices")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String createService(@ModelAttribute("service") ServicesModel service) {
        servicesService.save(service); // Сохраняем заказ в базу данных
        return "redirect:/adminsalona/listServices";  // Перенаправление на список услуг
    }


    @GetMapping("/listServices/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String showEditServiceForm(@PathVariable Long id, Model model) {
        // Найти услугу по ID
        ServicesModel service = servicesService.findById(id);



        // Добавляем данные в модель
        model.addAttribute("services", service);
        model.addAttribute("users", userService.findAll()); // Получаем список пользователей
        model.addAttribute("records", recordsService.findAll()); // Получаем список записей
        model.addAttribute("chart", chartService.findAll()); // Получаем список статусов/графиков
        return "adminsalona/editServices"; // Возвращаем имя шаблона для редактирования
    }

    @PostMapping("/listServices/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String updateService(@PathVariable Long id, @ModelAttribute ServicesModel services) {
        // Найти существующую услугу
        ServicesModel existingService = servicesService.findById(id);

        // Если услуга не найдена или логически удалена, перенаправляем на список
        if (existingService == null || existingService.isDeleted()) {
            return "redirect:/adminsalona/listServices";
        }

        // Обновляем данные услуги
        existingService.setNumberServices(services.getNumberServices());
        existingService.setUser(services.getUser());
        existingService.setServicesRecords(services.getServicesRecords());
        existingService.setChart(services.getChart());

        // Сохраняем изменения
        servicesService.save(existingService); // Сохраняем existingService, а не services
        return "redirect:/adminsalona/listServices";
    }


    @GetMapping("/deleteServices/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String deleteService(@PathVariable Long id) {
        // Найти услугу по ID
        ServicesModel service = servicesService.findById(id);

        // Если услуга существует и не удалена, помечаем её как удалённую
        if (service != null && !service.isDeleted()) {
            service.setDeleted(true); // Логическое удаление
            servicesService.save(service);
        }
        return "redirect:/adminsalona/listServices"; // Редирект на список услуг
    }

    // Методы для управления производителями
    @GetMapping("/listProducts_and_materials")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String listProductAndMaterials(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Product_and_materialsModel> productsAndMaterials;

        // Используем метод поиска с учетом регистра
        if (search != null && !search.isEmpty()) {
            productsAndMaterials = productandmaterialsService.findByNameContainingIgnoreCase(search);
        } else {
            productsAndMaterials = productandmaterialsService.findAllByDeletedFalse();  // Исключаем удаленные элементы
        }

        model.addAttribute("product_and_materials", productsAndMaterials);
        model.addAttribute("search", search);
        return "adminsalona/listProducts_and_materials";  // Путь должен соответствовать шаблону
    }

    @GetMapping("/createProduct_and_materials")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String showCreateFormProductAndMaterial(Model model) {
        model.addAttribute("product_and_materials", new Product_and_materialsModel());
        return "adminsalona/createProduct_and_materials";  // Путь должен соответствовать шаблону
    }

    @PostMapping("/createProduct_and_materials")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String createProductAndMaterial(@ModelAttribute Product_and_materialsModel productAndMaterial) {
        productandmaterialsService.save(productAndMaterial);
        return "redirect:/adminsalona/listProducts_and_materials";  // Перенаправление на список
    }

    @GetMapping("/listProducts_and_materials/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String showEditFormProductAndMaterial(@PathVariable Long id, Model model) {
        Product_and_materialsModel productAndMaterial = productandmaterialsService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Product/Material ID: " + id));
        model.addAttribute("product_and_materials", productAndMaterial);
        return "adminsalona/editProduct_and_materials";  // Путь должен соответствовать шаблону
    }

    @PostMapping("/listProducts_and_materials/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String updateProductAndMaterial(@PathVariable Long id, @ModelAttribute Product_and_materialsModel productAndMaterial) {
        productAndMaterial.setId(id);
        productandmaterialsService.save(productAndMaterial);
        return "redirect:/adminsalona/listProducts_and_materials";  // Перенаправление на список
    }

    @GetMapping("/deleteProducts_and_materials/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String deleteProductAndMaterial(@PathVariable Long id) {
        productandmaterialsService.deleteById(id);
        return "redirect:/adminsalona/listProducts_and_materials";  // Перенаправление на список
    }


    @GetMapping("/listChart")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String listCharts(@RequestParam(value = "search", required = false) String search, Model model) {
        List<ChartModel> charts;

        // Если был передан параметр поиска, ищем по имени графика
        if (search != null && !search.isEmpty()) {
            charts = chartService.findByNameContainingIgnoreCase(search);
        } else {
            // Если поиска нет, возвращаем все графики, не удаленные
            charts = chartService.findAll();  // Исключаем удалённые элементы
        }

        // Добавляем данные в модель
        model.addAttribute("charts", charts);
        model.addAttribute("search", search);

        return "adminsalona/listChart";  // Путь к шаблону
    }


    @GetMapping("/createChart")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String createChart(Model model) {
        List<UserModel> users = userService.findAll();
        model.addAttribute("chart", new ChartModel());
        model.addAttribute("users", users);
        return "adminsalona/createChart";
    }

    @PostMapping("/createChart")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String saveChart(@ModelAttribute ChartModel chart) {
        chartService.save(chart);
        return "redirect:/adminsalona/listChart";
    }

    @GetMapping("/listChart/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String editChart(@PathVariable Long id, Model model) {
        ChartModel chart = chartService.findById(id);
        List<UserModel> users = userService.findAll();
        model.addAttribute("chart", chart);
        model.addAttribute("users", users);
        return "adminsalona/editChart";
    }

    @PostMapping("/listChart/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String updateChart(@PathVariable Long id, @ModelAttribute ChartModel chart) {
        chart.setId(id);
        chartService.save(chart);
        return "redirect:/adminsalona/listChart";
    }

    @GetMapping("/deleteChart/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String deleteChart(@PathVariable Long id) {
        chartService.deleteById(id);
        return "redirect:/adminsalona/listChart";
    }




    @GetMapping("/listRecords")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String listRecords(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(defaultValue = "name") String sortField,
                              @RequestParam(defaultValue = "asc") String sortDir,
                              @RequestParam(value = "search", required = false) String search,
                              Model model) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Page<RecordsModel> recordsPage;

        // Пагинация и поиск по имени
        if (search != null && !search.isEmpty()) {
            recordsPage = recordsService.findByNameContainingIgnoreCase(search, PageRequest.of(page, size, sort));
        } else {
            recordsPage = recordsService.findPaginated(PageRequest.of(page, size, sort));
        }

        model.addAttribute("recordsPage", recordsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("search", search);

        return "adminsalona/listRecords";  // Шаблон для отображения списка
    }


    @GetMapping("/createRecords")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String showAddForm(Model model) {
        model.addAttribute("record", new RecordsModel()); // Создаем пустой объект для добавления
        model.addAttribute("clients", clientsService.findAll()); // Список клиентов
        model.addAttribute("product_and_materials", productandmaterialsService.findAll()); // Список материалов
        return "adminsalona/createRecords"; // Путь к шаблону добавления записи
    }

    @PostMapping("/createRecords")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String saveRecord(@ModelAttribute("record") RecordsModel record) {
        recordsService.save(record); // Сохраняем запись в базе данных
        return "redirect:/adminsalona/listRecords"; // Перенаправляем на список записей
    }


    @GetMapping("/listRecords/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String showEditFormRecords(@PathVariable Long id, Model model) {
        RecordsModel records = recordsService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Record ID: " + id));
        model.addAttribute("records", records);
        model.addAttribute("clients", clientsService.findAll());
        model.addAttribute("product_and_materials", productandmaterialsService.findAll());
        return "adminsalona/editRecords"; // Путь к шаблону редактирования записи
    }

    @PostMapping("/listRecords/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String updateRecords(@PathVariable Long id, @ModelAttribute RecordsModel records) {
        records.setId(id);
        recordsService.save(records);
        return "redirect:/adminsalona/listRecords"; // Перенаправление на список записей
    }


    @GetMapping("/deleteRecords/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String deleteRecords(@PathVariable Long id) {
        recordsService.deleteById(id);
        return "redirect:/adminsalona/listRecords"; // Перенаправление на список записей
    }



    // Список клиентов
    @GetMapping("/listClients")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String listClients(@RequestParam(value = "search", required = false) String search,
                              @RequestParam(value = "sort", required = false, defaultValue = "asc") String sortDirection,
                              @RequestParam(value = "deleted", required = false, defaultValue = "false") boolean showDeleted,
                              Model model) {

        List<ClientsModel> clients;

        if (showDeleted) {
            clients = clientsService.findAll(); // Получаем все клиенты, включая удалённые
        } else {
            clients = clientsService.findActiveClients(); // Только активные
        }

        // Применить поиск по имени
        if (search != null && !search.isEmpty()) {
            clients = clients.stream()
                    .filter(client -> client.getName().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Применить сортировку
        if ("asc".equals(sortDirection)) {
            clients = clients.stream()
                    .sorted(Comparator.comparing(ClientsModel::getName)) // Сортировка по возрастанию (А-Я)
                    .collect(Collectors.toList());
        } else {
            clients = clients.stream()
                    .sorted(Comparator.comparing(ClientsModel::getName).reversed()) // Сортировка по убыванию (Я-А)
                    .collect(Collectors.toList());
        }

        model.addAttribute("clients", clients);
        model.addAttribute("currentSort", sortDirection);
        model.addAttribute("search", search);
        model.addAttribute("deleted", !showDeleted);
        return "adminsalona/listClients"; // Убедитесь, что этот шаблон существует
    }

    // Форма создания нового клиента
    @GetMapping("/createClients")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String showCreateClientForm(Model model) {
        model.addAttribute("client", new ClientsModel());
        return "adminsalona/createClients"; // Убедитесь, что этот шаблон существует
    }

    // Обработка создания нового клиента
    @PostMapping("/createClients")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String createClient(@ModelAttribute ClientsModel client) {
        clientsService.save(client);
        return "redirect:/adminsalona/listClients"; // Перенаправление на список клиентов после создания
    }

    // Показывает форму редактирования клиента
    @GetMapping("/listClients/edit/{id}")
    public String showEditClientForm(@PathVariable Long id, Model model) {
        ClientsModel client = clientsService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid client ID: " + id));
        model.addAttribute("client", client);
        return "adminsalona/editClients"; // Убедитесь, что шаблон существует
    }

    @PostMapping("/listClients/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String updateClient(@PathVariable Long id, @ModelAttribute("client") ClientsModel client) {
        if (client.getId() == null || !client.getId().equals(id)) {
            client.setId(id);
        }
        clientsService.save(client);
        return "redirect:/adminsalona/listClients";
    }



    // Удаление клиента
    @GetMapping("/clients/soft-delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String softDeleteClient(@PathVariable Long id) {
        clientsService.softDeleteById(id);
        return "redirect:/adminsalona/listClients"; // Перенаправление на список клиентов после удаления
    }

    @GetMapping("/clients/hard-delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String hardDeleteClient(@PathVariable Long id) {
        clientsService.hardDeleteById(id);
        return "redirect:/adminsalona/listClients"; // Перенаправление на список клиентов после удаления
    }

    @GetMapping("/clients/restore/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String restoreClient(@PathVariable Long id) {
        clientsService.restoreById(id);
        return "redirect:/adminsalona/listClients"; // Перенаправление на список клиентов после восстановления
    }




    // Список всех ролей с возможностью поиска
    @GetMapping("/listRoles")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
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
        return "adminsalona/listRoles";
    }

    // Форма создания новой роли
    @GetMapping("/createRoles")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String showCreateRoleForm(Model model) {
        model.addAttribute("role", new RoleModel());
        return "adminsalona/createRoles";
    }

    // Обработка создания новой роли
    @PostMapping("/createRoles")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String createRole(@ModelAttribute RoleModel role) {
        roleService.save(role);
        return "redirect:/adminsalona/listRoles";
    }

    // Форма редактирования существующей роли
    @GetMapping("/listRoles/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String showEditRoleForm(@PathVariable Long id, Model model) {
        RoleModel role = roleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + id));
        model.addAttribute("role", role);
        return "adminsalona/editRoles";
    }

    // Обработка редактирования существующей роли
    @PostMapping("/listRoles/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String updateRole(@PathVariable Long id, @ModelAttribute RoleModel role) {
        role.setId(id);
        roleService.save(role);
        return "redirect:/adminsalona/listRoles";
    }

    // Удаление роли
    @GetMapping("/deleteRoles/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String deleteRole(@PathVariable Long id) {
        roleService.deleteById(id);
        return "redirect:/adminsalona/listRoles";
    }

    @GetMapping("/listSpecializations")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String listSpecializations(Model model) {
        List<Specialization> specializations = specializationService.findAll();
        model.addAttribute("specializations", specializations);
        return "adminsalona/listSpecializations";
    }

    @GetMapping("/createSpecialization")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String showCreateForms(Model model) {
        model.addAttribute("specialization", new Specialization());
        model.addAttribute("users", userService.findAll()); // Список всех пользователей
        return "adminsalona/createSpecialization";
    }

    @PostMapping("/createSpecialization")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String createSpecialization(@ModelAttribute Specialization specialization) {
        if (specialization.getUser() == null || specialization.getUser().getId() == null) {
            throw new IllegalArgumentException("Пользователь не выбран");
        }

        UserModel user = userService.findById(specialization.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + specialization.getUser().getId()));
        specialization.setUser(user);

        specializationService.save(specialization);
        return "redirect:/adminsalona/listSpecializations";
    }

    @GetMapping("/listSpecializations/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showEditFormSpecialization(@PathVariable Long id, Model model) {
        Specialization specialization = specializationService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid specialization ID: " + id));
        model.addAttribute("specialization", specialization);
        model.addAttribute("users", specializationService.getUsers());
        return "adminsalona/editSpecialization";
    }

    @PostMapping("/listSpecializations/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String updateSpecialization(@PathVariable Long id, @ModelAttribute Specialization specialization) {
        Specialization existingSpecialization = specializationService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid specialization ID: " + id));

        existingSpecialization.setName(specialization.getName());
        existingSpecialization.setUser(specialization.getUser());

        specializationService.save(existingSpecialization);
        return "redirect:/adminsalona/listSpecializations";
    }

    @GetMapping("/deleteSpecialization/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINSALONA')")
    public String deleteSpecialization(@PathVariable Long id) {
        specializationService.deleteById(id);
        return "redirect:/adminsalona/listSpecializations";
    }
}
