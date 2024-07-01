package com.group6b.shopiifoodwebsite.controllers;

import com.group6b.shopiifoodwebsite.entities.Category;
import com.group6b.shopiifoodwebsite.entities.FoodItem;
import com.group6b.shopiifoodwebsite.entities.Restaurant;
import com.group6b.shopiifoodwebsite.services.CategoryService;
import com.group6b.shopiifoodwebsite.services.FoodItemService;
import com.group6b.shopiifoodwebsite.services.RestaurantService;
import com.group6b.shopiifoodwebsite.services.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/")
public class HomeController {
    private final CategoryService categoryService;
    private final FoodItemService  foodItemService;
    private final UserService userService;
    private final RestaurantService restaurantService;
    @GetMapping
    public String home(@NotNull Model model ) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("foods",foodItemService.getAllFood());
        model.addAttribute("restaurants",restaurantService.getAllRestaurants());
        model.addAttribute("random3",foodItemService.getRandomFoodItems(3));
        return "home/index";
    }

    @GetMapping("foodItem/detail/{id}")
    public String viewFoodItemDetails(@PathVariable long id, Model model) {
        var foodItem = foodItemService.getFoodById(id).orElseThrow(() -> new IllegalArgumentException("Food not found"));

        model.addAttribute("foodItem", foodItem);
        return "home/details";
    }

    @GetMapping("/category/{id}")
    public String viewCategoryDetails(@PathVariable long id, @RequestParam(value = "foodItemId", required = false) Long foodItemId,
                                      @NotNull Model model) {
        var category = categoryService.getCategoryById(id).orElseThrow(() -> new IllegalArgumentException("Category not found"));
        List<FoodItem> foodItemsList = foodItemService.getFoodItemsByCategoryId(id);
        model.addAttribute("foodItems", foodItemsList);
        model.addAttribute("category", category);
        if (foodItemId != null) {
            var selectedFoodItem = foodItemService.getFoodById(foodItemId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid food item Id:" + foodItemId));
            model.addAttribute("selectedFoodItem", selectedFoodItem);
        }
        return "home/categorydetail";
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        List<Restaurant> restaurants = restaurantService.searchRestaurants(query);
        List<Category> categories = categoryService.searchCategories(query);
        List<FoodItem> foodItems = foodItemService.searchFoodItems(query);

        model.addAttribute("restaurants", restaurants);
        model.addAttribute("categories", categories);
        model.addAttribute("foodItems", foodItems);
        model.addAttribute("query", query);
        return "home/search";
    }


    @GetMapping("/restaurant/detail/{id}")
    public String viewRestaurantDetails(@PathVariable long id, Model model) {
        List<FoodItem> foodItems = foodItemService.getFoodItemsByRestaurantId(id);
        model.addAttribute("foodItems", foodItems);
        return "home/restaurantdetails";
    }

    @GetMapping("foodItem/index")
    public String viewFoodItem(Model model) {
        return "product/index";
    }
    @GetMapping("foodItem/detail")
    public String viewFoodItemDetails(Model model) {
        return "product/details";
    }

}
