package com.group6b.shopiifoodwebsite.controllers;

import com.group6b.shopiifoodwebsite.services.FoodItemService;
import com.group6b.shopiifoodwebsite.services.UserService;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final FoodItemService foodItemService;

    public AdminController(UserService userService, FoodItemService foodItemService) {
        this.userService = userService;
        this.foodItemService = foodItemService;
    }

    @GetMapping
    public String admin() {
        return "adminDashboard/dashboard";
    }

    @GetMapping("/list-foods")
    public String getFoodItemsByRestaurantId(@NotNull Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long restaurantId =userService.getRestaurantByUsername(username).getId();
        model.addAttribute("foodsByRestaurant", foodItemService.getFoodItemsByRestaurantId(restaurantId)) ;
        return "adminDashboard/list-foods";
    }

    @GetMapping("/statistical")
    public String showStatistical(Model model) {
        return "adminDashboard/statistical";
    }
    @GetMapping("/order-lists")
    public String getOrderLists(Model model) {
        return "adminDashboard/order-list";
    }
}
