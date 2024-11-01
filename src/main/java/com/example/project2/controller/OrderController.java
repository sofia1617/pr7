package com.example.project2.controller;

import com.example.project2.model.OrderModel;
import com.example.project2.model.StatusModel;
import com.example.project2.model.UserModel;
import com.example.project2.service.OrderService;
import com.example.project2.service.StatusService;
import com.example.project2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService; // Assuming you have a UserService for fetching users
    private final StatusService statusService; // Assuming you have a StatusService for fetching statuses

    @Autowired
    public OrderController(OrderService orderService, UserService userService, StatusService statusService) {
        this.orderService = orderService;
        this.userService = userService;
        this.statusService = statusService;
    }

    @GetMapping
    public String listOrders(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<OrderModel> orders;
        if (keyword != null && !keyword.isEmpty()) {
            orders = orderService.search(keyword); // Searching for orders
        } else {
            orders = orderService.findAll(); // Fetch all orders
        }
        model.addAttribute("orders", orders);
        model.addAttribute("keyword", keyword); // Pass the search keyword to the model
        return "listOrders";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("order", new OrderModel());
        // Fetch users and statuses to populate the form
        List<UserModel> users = userService.findAll(); // Fetch all users
        List<StatusModel> statuses = statusService.findAll(); // Fetch all statuses
        model.addAttribute("users", users);
        model.addAttribute("statuses", statuses);
        return "createOrder";
    }

    @PostMapping
    public String createOrder(@ModelAttribute OrderModel order) {
        orderService.save(order); // Save the order
        return "redirect:/orders"; // Redirect to the list of orders
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        OrderModel order = orderService.findById(id);
        model.addAttribute("order", order);
        // Fetch users and statuses to populate the form
        List<UserModel> users = userService.findAll();
        List<StatusModel> statuses = statusService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("statuses", statuses);
        return "editOrder"; // Path to your edit order HTML template
    }

    @PostMapping("/edit/{id}")
    public String updateOrder(@PathVariable Long id, @ModelAttribute OrderModel order) {
        order.setId(id); // Ensure the ID is set for the update
        orderService.save(order); // Update the order
        return "redirect:/orders"; // Redirect to the list of orders
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteById(id); // Delete the order
        return "redirect:/orders"; // Redirect to the list of orders
    }
}
