package com.example.project2.controller;

import com.example.project2.model.ServicesModel;
import com.example.project2.model.ChartModel;
import com.example.project2.model.UserModel;
import com.example.project2.service.ServicesService;
import com.example.project2.service.ChartService;
import com.example.project2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final ServicesService servicesService;
    private final UserService userService; // Assuming you have a UserService for fetching users
    private final ChartService chartService; // Assuming you have a StatusService for fetching statuses

    @Autowired
    public OrderController(ServicesService servicesService, UserService userService, ChartService chartService) {
        this.servicesService = servicesService;
        this.userService = userService;
        this.chartService = chartService;
    }

    @GetMapping
    public String listOrders(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<ServicesModel> orders;
        if (keyword != null && !keyword.isEmpty()) {
            orders = servicesService.search(keyword); // Searching for orders
        } else {
            orders = servicesService.findAll(); // Fetch all orders
        }
        model.addAttribute("orders", orders);
        model.addAttribute("keyword", keyword); // Pass the search keyword to the model
        return "listOrders";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("order", new ServicesModel());
        // Fetch users and statuses to populate the form
        List<UserModel> users = userService.findAll(); // Fetch all users
        List<ChartModel> statuses = chartService.findAll(); // Fetch all statuses
        model.addAttribute("users", users);
        model.addAttribute("statuses", statuses);
        return "createOrder";
    }

    @PostMapping
    public String createOrder(@ModelAttribute ServicesModel order) {
        servicesService.save(order); // Save the order
        return "redirect:/orders"; // Redirect to the list of orders
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ServicesModel order = servicesService.findById(id);
        model.addAttribute("order", order);
        // Fetch users and statuses to populate the form
        List<UserModel> users = userService.findAll();
        List<ChartModel> statuses = chartService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("statuses", statuses);
        return "editOrder"; // Path to your edit order HTML template
    }

    @PostMapping("/edit/{id}")
    public String updateOrder(@PathVariable Long id, @ModelAttribute ServicesModel order) {
        order.setId(id); // Ensure the ID is set for the update
        servicesService.save(order); // Update the order
        return "redirect:/orders"; // Redirect to the list of orders
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        servicesService.deleteById(id); // Delete the order
        return "redirect:/orders"; // Redirect to the list of orders
    }
}
