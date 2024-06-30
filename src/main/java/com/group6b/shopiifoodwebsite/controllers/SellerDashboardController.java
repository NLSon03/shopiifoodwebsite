package com.group6b.shopiifoodwebsite.controllers;


import com.group6b.shopiifoodwebsite.entities.Category;
import com.group6b.shopiifoodwebsite.entities.FoodItem;
import com.group6b.shopiifoodwebsite.entities.Restaurant;
import com.group6b.shopiifoodwebsite.services.CategoryService;
import com.group6b.shopiifoodwebsite.services.FoodItemService;
import com.group6b.shopiifoodwebsite.services.RestaurantService;
import com.group6b.shopiifoodwebsite.services.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/seller")
public class SellerDashboardController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private SellerService sellerService;

    // --- Category Management ---

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return sellerService.getAllCategories();
    }

    @GetMapping("/categories/{id}")
    public Optional<Category> getCategoryById(@PathVariable Long id) {
        return sellerService.getCategoryById(id);
    }

//    @PostMapping("/categories")
//    public Category createCategory(@RequestBody Category category) {
//        return categoryService.addCategory(category);
//    }

//    @PutMapping("/categories/{id}")
//    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
//        category.setId(id);
//        return categoryService.saveCategory(category);
//    }

//    @DeleteMapping("/categories/{id}")
//    public void deleteCategory(@PathVariable Long id) {
//        categoryService.deleteCategory(id);
//    }

    // --- FoodItem Management ---

    // FoodItem Endpoints
    @GetMapping("/fooditems")
    public List<FoodItem> getAllFoodItems() {
        return sellerService.getAllFoodItems();
    }

    @GetMapping("/fooditems/{id}")
    public FoodItem getFoodItemById(@PathVariable Long id) {
        return sellerService.getFoodItemById(id);
    }

    @PostMapping("/fooditems")
    public FoodItem createFoodItem(@RequestBody FoodItem foodItem) {
        return sellerService.saveFoodItem(foodItem);
    }

    @PutMapping("/fooditems/{id}")
    public FoodItem updateFoodItem(@PathVariable Long id, @RequestBody FoodItem foodItem) {
        return sellerService.updateFoodItem(id, foodItem);
    }

    @DeleteMapping("/fooditems/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable Long id) {
        sellerService.deleteFoodItem(id);
        return ResponseEntity.noContent().build();
    }
}
