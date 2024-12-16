package com.example.project2.controller;

import com.example.project2.model.Product_and_materialsModel;
import com.example.project2.service.Product_and_materialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manufacturers")
public class ManufacturerController {

    @Autowired
    private Product_and_materialsService productandmaterialsService;

    @GetMapping
    public String listManufacturers(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Product_and_materialsModel> manufacturers;
        if (search != null && !search.isEmpty()) {
            manufacturers = productandmaterialsService.findByNameContainingIgnoreCase(search); // Поиск производителей по имени
        } else {
            manufacturers = productandmaterialsService.findAll(); // Получить всех производителей
        }
        model.addAttribute("manufacturers", manufacturers);
        model.addAttribute("search", search); // Передать поисковый запрос в модель
        return "listManufacturers"; // Имя HTML-шаблона для списка производителей
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("manufacturer", new Product_and_materialsModel());
        return "createManufacturer"; // Имя HTML-шаблона для создания производителя
    }

    @PostMapping
    public String createManufacturer(@ModelAttribute Product_and_materialsModel manufacturer) {
        productandmaterialsService.save(manufacturer);
        return "redirect:/manufacturers"; // Перенаправление после создания
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product_and_materialsModel manufacturer = productandmaterialsService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid manufacturer ID:" + id));
        model.addAttribute("manufacturer", manufacturer);
        return "editManufacturer"; // Имя HTML-шаблона для редактирования производителя
    }

    @PostMapping("/{id}")
    public String updateManufacturer(@PathVariable Long id, @ModelAttribute Product_and_materialsModel manufacturer) {
        manufacturer.setId(id);
        productandmaterialsService.save(manufacturer);
        return "redirect:/manufacturers"; // Перенаправление после редактирования
    }

    @GetMapping("/delete/{id}")
    public String deleteManufacturer(@PathVariable Long id) {
        productandmaterialsService.deleteById(id);
        return "redirect:/manufacturers"; // Перенаправление после удаления
    }
}
