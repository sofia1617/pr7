package com.example.project2.controller;

import com.example.project2.model.ManufacturerModel;
import com.example.project2.service.ManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manufacturers")
public class ManufacturerController {

    @Autowired
    private ManufacturerService manufacturerService;

    @GetMapping
    public String listManufacturers(@RequestParam(value = "search", required = false) String search, Model model) {
        List<ManufacturerModel> manufacturers;
        if (search != null && !search.isEmpty()) {
            manufacturers = manufacturerService.findByNameContainingIgnoreCase(search); // Поиск производителей по имени
        } else {
            manufacturers = manufacturerService.findAll(); // Получить всех производителей
        }
        model.addAttribute("manufacturers", manufacturers);
        model.addAttribute("search", search); // Передать поисковый запрос в модель
        return "listManufacturers"; // Имя HTML-шаблона для списка производителей
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("manufacturer", new ManufacturerModel());
        return "createManufacturer"; // Имя HTML-шаблона для создания производителя
    }

    @PostMapping
    public String createManufacturer(@ModelAttribute ManufacturerModel manufacturer) {
        manufacturerService.save(manufacturer);
        return "redirect:/manufacturers"; // Перенаправление после создания
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ManufacturerModel manufacturer = manufacturerService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid manufacturer ID:" + id));
        model.addAttribute("manufacturer", manufacturer);
        return "editManufacturer"; // Имя HTML-шаблона для редактирования производителя
    }

    @PostMapping("/{id}")
    public String updateManufacturer(@PathVariable Long id, @ModelAttribute ManufacturerModel manufacturer) {
        manufacturer.setId(id);
        manufacturerService.save(manufacturer);
        return "redirect:/manufacturers"; // Перенаправление после редактирования
    }

    @GetMapping("/delete/{id}")
    public String deleteManufacturer(@PathVariable Long id) {
        manufacturerService.deleteById(id);
        return "redirect:/manufacturers"; // Перенаправление после удаления
    }
}
