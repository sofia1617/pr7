package com.example.project2.controller;

import com.example.project2.model.ChartModel;
import com.example.project2.model.ClientsModel;
import com.example.project2.model.RecordsModel;
import com.example.project2.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

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

    @GetMapping("/listChart")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
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

        return "employee/listChart";  // Путь к шаблону
    }


    @GetMapping("/listRecords")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
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

        return "employee/listRecords";  // Шаблон для отображения списка
    }

    @GetMapping("/listRecords/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public String showEditFormRecords(@PathVariable Long id, Model model) {
        RecordsModel records = recordsService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Record ID: " + id));
        model.addAttribute("records", records);
        model.addAttribute("clients", clientsService.findAll());
        model.addAttribute("product_and_materials", productandmaterialsService.findAll());
        return "employee/editRecords"; // Путь к шаблону редактирования записи
    }

    @PostMapping("/listRecords/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public String updateRecords(@PathVariable Long id, @ModelAttribute RecordsModel records) {
        records.setId(id);
        recordsService.save(records);
        return "redirect:/employee/listRecords"; // Перенаправление на список записей
    }

    // Список клиентов
    @GetMapping("/listClients")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
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
        return "employee/listClients"; // Убедитесь, что этот шаблон существует
    }

    // Показывает форму редактирования клиента
    @GetMapping("/listClients/edit/{id}")
    public String showEditClientForm(@PathVariable Long id, Model model) {
        ClientsModel client = clientsService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid client ID: " + id));
        model.addAttribute("client", client);
        return "employee/editClients"; // Убедитесь, что шаблон существует
    }

    @PostMapping("/listClients/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public String updateClient(@PathVariable Long id, @ModelAttribute("client") ClientsModel client) {
        if (client.getId() == null || !client.getId().equals(id)) {
            client.setId(id);
        }
        clientsService.save(client);
        return "redirect:/employee/listClients";
    }


}
