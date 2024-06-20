package com.group6b.shopiifoodwebsite.controllers;

import com.group6b.shopiifoodwebsite.services.CategoryService;
import com.group6b.shopiifoodwebsite.services.FoodItemService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Controller
@RequestMapping("/")
public class HomeController {
    private final CategoryService categoryService;
    private final FoodItemService  foodItemService;
    @GetMapping
    public String home(@NotNull Model model ) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("foods",foodItemService.getAllFood());
        model.addAttribute("random3",foodItemService.getRandomFoodItems(3));
        return "home/index";
    }
}
