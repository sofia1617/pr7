package com.example.project2.controller;

import com.example.project2.model.RecordsModel;
import com.example.project2.service.ClientsService;
import com.example.project2.service.Product_and_materialsService;
import com.example.project2.service.RecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private RecordsService recordsService;

    @Autowired
    private ClientsService clientsService;

    @Autowired
    private Product_and_materialsService productandmaterialsService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_SUPERUSER')")
    public String listProducts(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "name") String sortField,
                               @RequestParam(defaultValue = "asc") String sortDir,
                               Model model) {
        List<RecordsModel> products = recordsService.findAll();
        model.addAttribute("products", products);
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Page<RecordsModel> productsPage = recordsService.findPaginated(PageRequest.of(page, size, sort));
        model.addAttribute("productsPage", productsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "listProducts";

    }

    @GetMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER') or hasAuthority('ROLE_MANAGER')")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new RecordsModel());
        model.addAttribute("categories", clientsService.findAll());
        model.addAttribute("manufacturers", productandmaterialsService.findAll());
        return "createProduct"; // Имя HTML-шаблона для создания продукта
    }


    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String createProduct(@ModelAttribute RecordsModel product) {
        recordsService.save(product);
        return "redirect:/products"; // Перенаправление после создания
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        RecordsModel product = recordsService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", clientsService.findAll());
        model.addAttribute("manufacturers", productandmaterialsService.findAll());
        return "editProduct"; // Имя HTML-шаблона для редактирования продукта
    }

    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute RecordsModel product) {
        product.setId(id);
        recordsService.save(product);
        return "redirect:/products"; // Перенаправление после редактирования
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        recordsService.deleteById(id);
        return "redirect:/products";
    }


    // Добавляем поиск по имени продукта
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String listProducts(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "name") String sortField,
                               @RequestParam(defaultValue = "asc") String sortDir,
                               @RequestParam(value = "search", required = false) String search, Model model) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Page<RecordsModel> productsPage;

        // Пагинация и поиск по имени
        if (search != null && !search.isEmpty()) {
            productsPage = recordsService.findByNameContainingIgnoreCase(search, PageRequest.of(page, size, sort));
        } else {
            productsPage = recordsService.findPaginated(PageRequest.of(page, size, sort));
        }

        model.addAttribute("productsPage", productsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("search", search);

        return "listProducts";
    }


}
