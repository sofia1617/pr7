package com.example.project2.controller;

import com.example.project2.model.ChartModel;
import com.example.project2.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/statuses")
public class StatusController {
    private final ChartService chartService;

    @Autowired
    public StatusController(ChartService chartService) {
        this.chartService = chartService;
    }

    @GetMapping
    public String listStatuses(@RequestParam(value = "search", required = false) String search, Model model) {
        List<ChartModel> statuses;
        if (search != null && !search.isEmpty()) {
            statuses = chartService.findByNameContainingIgnoreCase(search); // Поиск статусов по имени
        } else {
            statuses = chartService.findAll(); // Получить все статусы
        }
        model.addAttribute("statuses", statuses);
        model.addAttribute("search", search); // Передать поисковый запрос в модель
        return "listStatuses";  // Путь к HTML-шаблону
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("status", new ChartModel());
        return "createStatus";  // Путь к HTML-шаблону
    }

    @PostMapping
    public String createStatus(@ModelAttribute ChartModel status) {
        chartService.save(status);
        return "redirect:/statuses";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ChartModel status = chartService.findById(id);
        model.addAttribute("status", status);
        return "editStatus";  // Путь к HTML-шаблону
    }

    @PostMapping("/edit/{id}")
    public String updateStatus(@PathVariable Long id, @ModelAttribute ChartModel status) {
        status.setId(id);
        chartService.save(status);
        return "redirect:/statuses";
    }

    @GetMapping("/delete/{id}")
    public String deleteStatus(@PathVariable Long id) {
        chartService.deleteById(id);
        return "redirect:/statuses";
    }
}
