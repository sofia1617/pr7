package com.example.project2.controller;

import com.example.project2.model.CategoryModel;
import com.example.project2.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(@RequestParam(value = "sort", required = false, defaultValue = "asc") String sortDirection, Model model) {
        List<CategoryModel> categories = categoryService.findAllSorted(sortDirection);
        model.addAttribute("categories", categories);
        model.addAttribute("currentSort", sortDirection);
        model.addAttribute("deleted", false);
        return "listCategories";
    }

    @GetMapping("/search")
    public String searchCategories(@RequestParam("name") String name,
                                   @RequestParam(value = "sort", required = false, defaultValue = "asc") String sortDirection,
                                   Model model) {
        List<CategoryModel> categories = categoryService.searchByName(name, sortDirection);
        model.addAttribute("categories", categories);
        model.addAttribute("currentSort", sortDirection);
        model.addAttribute("deleted", false); // Добавьте это, если оно требуется в шаблоне
        return "listCategories";
    }

    @GetMapping("/filter")
    public String filterCategories(@RequestParam(value = "deleted", required = false, defaultValue = "false") boolean showDeleted, Model model) {
        List<CategoryModel> categories;
        if (showDeleted) {
            categories = categoryService.findAll(); // Заменено на findAll() для получения всех категорий
        } else {
            categories = categoryService.findActiveCategories();
        }
        model.addAttribute("categories", categories);
        model.addAttribute("deleted", !showDeleted);
        return "listCategories";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new CategoryModel());
        return "createCategory";
    }

    @PostMapping // Здесь мы обрабатываем создание категории
    public String createCategory(@ModelAttribute CategoryModel category) {
        categoryService.save(category);
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<CategoryModel> category = categoryService.findById(id); // Изменено на Optional
        if (category.isPresent()) {
            model.addAttribute("category", category.get()); // Получаем значение из Optional
            return "editCategory";
        } else {
            return "redirect:/categories"; // Перенаправление, если категория не найдена
        }
    }

    @PostMapping("/edit/{id}") // Обработка обновления категории
    public String updateCategory(@PathVariable Long id, @ModelAttribute CategoryModel category) {
        category.setId(id);
        categoryService.save(category);
        return "redirect:/categories";
    }

    // Логическое удаление
    @GetMapping("/soft-delete/{id}")
    public String softDeleteCategory(@PathVariable Long id) {
        categoryService.softDeleteById(id);
        return "redirect:/categories";
    }

    // Восстановление
    @GetMapping("/restore/{id}")
    public String restoreCategory(@PathVariable Long id) {
        categoryService.restoreById(id);
        return "redirect:/categories";
    }

    // Физическое удаление
    @GetMapping("/hard-delete/{id}")
    public String hardDeleteCategory(@PathVariable Long id) {
        categoryService.hardDeleteById(id);
        return "redirect:/categories";
    }
}
