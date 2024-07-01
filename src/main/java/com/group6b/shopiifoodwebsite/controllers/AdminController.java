package com.group6b.shopiifoodwebsite.controllers;

import com.group6b.shopiifoodwebsite.entities.FoodItem;
import com.group6b.shopiifoodwebsite.services.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final FoodItemService foodItemService;
    private final OrderService orderService;
    private final CategoryService categoryService;

    public AdminController(UserService userService, FoodItemService foodItemService, OrderService orderService, CategoryService categoryService, RestaurantService restaurantService) {
        this.userService = userService;
        this.foodItemService = foodItemService;
        this.orderService = orderService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String admin() {
        return "adminDashboard/dashboard";
    }

    @GetMapping("/list-foods")
    public String getFoodItems(@NotNull Model model) {
        model.addAttribute("foods", foodItemService.getAllFood());
        return "adminDashboard/list-foods";
    }

    @GetMapping("/statistical")
    public String showStatistical(Model model) {
        return "adminDashboard/statistical";
    }

    // Delete a food item
    @GetMapping("/list-foods/delete/{id}")
    public String deleteFoodItem(@PathVariable Long id) {
        foodItemService.getFoodById(id).ifPresentOrElse(book -> foodItemService.deleteFoodById(id), () -> {
            throw new IllegalArgumentException("Food not found");
        });
        return "redirect:/admin/list-foods";
    }

    @GetMapping("/foods/{id}")
    public String getFoodById(@PathVariable Long id) {
        return "adminDashboard/details";
    }


    @GetMapping("/foods/categories/{id}")
    public String getFoodByCategory(@PathVariable Long id) {
        return "adminDashboard/list-foods-by-category";
    }

    //api
    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable Long id) {
        return "adminDashboard/edit-category";
    }

    //api
    @GetMapping("/categories/create")
    public String createCategory() {
        return "adminDashboard/createCategory";
    }

    @GetMapping("/list-categories")
    public String getCategories() {
        return "adminDashboard/list-categories";
    }

    @GetMapping("/orders")
    public String getOrderLists() {
        return "adminDashboard/order-list";
    }

    @GetMapping("/orders/{id}")
    public String getOrderDetails(@PathVariable String id){
        return "adminDashboard/order-detail";
    }
}
