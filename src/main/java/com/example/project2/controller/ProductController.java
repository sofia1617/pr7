package com.example.project2.controller;

import com.example.project2.model.ProductModel;
import com.example.project2.service.CategoryService;
import com.example.project2.service.ManufacturerService;
import com.example.project2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ManufacturerService manufacturerService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_SUPERUSER')")
    public String listProducts(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "name") String sortField,
                               @RequestParam(defaultValue = "asc") String sortDir,
                               Model model) {
        List<ProductModel> products = productService.findAll();
        model.addAttribute("products", products);
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Page<ProductModel> productsPage = productService.findPaginated(PageRequest.of(page, size, sort));
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
        model.addAttribute("product", new ProductModel());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("manufacturers", manufacturerService.findAll());
        return "createProduct"; // Имя HTML-шаблона для создания продукта
    }


    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPERUSER')")
    public String createProduct(@ModelAttribute ProductModel product) {
        productService.save(product);
        return "redirect:/products"; // Перенаправление после создания
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProductModel product = productService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("manufacturers", manufacturerService.findAll());
        return "editProduct"; // Имя HTML-шаблона для редактирования продукта
    }

    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute ProductModel product) {
        product.setId(id);
        productService.save(product);
        return "redirect:/products"; // Перенаправление после редактирования
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
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

        return "listProducts";
    }


}
