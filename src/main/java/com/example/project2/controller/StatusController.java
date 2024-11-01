package com.example.project2.controller;

import com.example.project2.model.StatusModel;
import com.example.project2.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/statuses")
public class StatusController {
    private final StatusService statusService;

    @Autowired
    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping
    public String listStatuses(@RequestParam(value = "search", required = false) String search, Model model) {
        List<StatusModel> statuses;
        if (search != null && !search.isEmpty()) {
            statuses = statusService.findByNameContainingIgnoreCase(search); // Поиск статусов по имени
        } else {
            statuses = statusService.findAll(); // Получить все статусы
        }
        model.addAttribute("statuses", statuses);
        model.addAttribute("search", search); // Передать поисковый запрос в модель
        return "listStatuses";  // Путь к HTML-шаблону
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("status", new StatusModel());
        return "createStatus";  // Путь к HTML-шаблону
    }

    @PostMapping
    public String createStatus(@ModelAttribute StatusModel status) {
        statusService.save(status);
        return "redirect:/statuses";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        StatusModel status = statusService.findById(id);
        model.addAttribute("status", status);
        return "editStatus";  // Путь к HTML-шаблону
    }

    @PostMapping("/edit/{id}")
    public String updateStatus(@PathVariable Long id, @ModelAttribute StatusModel status) {
        status.setId(id);
        statusService.save(status);
        return "redirect:/statuses";
    }

    @GetMapping("/delete/{id}")
    public String deleteStatus(@PathVariable Long id) {
        statusService.deleteById(id);
        return "redirect:/statuses";
    }
}
