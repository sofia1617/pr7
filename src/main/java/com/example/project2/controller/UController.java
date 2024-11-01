package com.example.project2.controller;

import com.example.project2.model.ProductModel;
import com.example.project2.service.CategoryService;
import com.example.project2.service.ManufacturerService;
import com.example.project2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ManufacturerService manufacturerService;

    @GetMapping("/listProducts")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String listProducts(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "name") String sortField,
                               @RequestParam(defaultValue = "asc") String sortDir,
                               Model model) {
        try {
            Sort sort = sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
            Page<ProductModel> productsPage = productService.findPaginated(PageRequest.of(page, size, sort));

            model.addAttribute("productsPage", productsPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);

            return "user/listProducts"; // имя вашего шаблона
        }
        catch (Exception e) {
            // Логируем исключение и возвращаем сообщение об ошибке
            System.err.println("Ошибка при получении списка продуктов: " + e.getMessage());
            return "user/listProducts";
        }


    }


    // Запрет доступа к таблице Users
    @GetMapping("/listUsers")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String accessDeniedUsers(Model model) {
        return accessDenied(model);
    }

    // Запрет доступа к таблице Roles
    @GetMapping("/listRoles")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String accessDeniedRoles(Model model) {
        return accessDenied(model);
    }

    // Запрет доступа к таблице Orders
    @GetMapping("/listOrders")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String accessDeniedOrders(Model model) {
        return accessDenied(model);
    }

    @GetMapping("/listManufacturers")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String accessDeniedManufacturers(Model model) {
        return accessDenied(model);
    }

    @GetMapping("/listStatuses")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String accessDeniedStatuses(Model model) {
        return accessDenied(model);
    }

    @GetMapping("/listCategories")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String accessDeniedCategories(Model model) {
        return accessDenied(model);
    }


    // Метод для отображения сообщения о запрете доступа
    private String accessDenied(Model model) {
        model.addAttribute("message", "Доступ запрещен к этой странице.");
        return "user/access-denied"; // Убедитесь, что у вас есть этот шаблон
    }
}
