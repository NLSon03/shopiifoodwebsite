package com.group6b.shopiifoodwebsite.controllers;


import com.group6b.shopiifoodwebsite.entities.*;
import com.group6b.shopiifoodwebsite.services.AdminService;
import com.group6b.shopiifoodwebsite.services.CategoryService;
import com.group6b.shopiifoodwebsite.services.ImageStorageService;
import com.group6b.shopiifoodwebsite.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminService adminService;
    private final ImageStorageService imageStorageService;
    private final UserService userService;

    public AdminApiController(AdminService adminService, CategoryService categoryService, ImageStorageService imageStorageService, UserService userService) {
        this.adminService = adminService;
        this.imageStorageService = imageStorageService;
        this.userService = userService;
    }

    @GetMapping("/categories/{id}")
    public Optional<Category> getCategory(@PathVariable Long id) {
        return adminService.getCategory(id);
    }

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return adminService.getAllCategories();
    }

    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestParam("categoryDescription") String categoryDescription,
                                            @RequestParam("icon") MultipartFile categoryIcon) {
        try {
            Category category = new Category();
            category.setCategoryDescription(categoryDescription);
            adminService.createCategory(category, categoryIcon);
            return ResponseEntity.ok(Collections.singletonMap("message", "Category added successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id,
                                                   @RequestParam("categoryDescription") String categoryDescription,
                                                   @RequestParam("icon") MultipartFile categoryIcon) throws Exception {
        imageStorageService.deleteAllIconCategory(id);
        Category updatedCategory = adminService.updateCategory(id, categoryDescription, categoryIcon);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // Return 204 No Content status
    }

    @GetMapping("/foods/categories/{id}")
    public List<FoodItem> getFoodByCategory(@PathVariable Long id) {
        return adminService.getFoodByCategoryId(id);
    }

    @GetMapping("/orders")
    public List<Order> getAllOrder()
    {
        return adminService.getALlOrder();
    }

    @GetMapping("/orders/{id}")
    public List<OrderDetail> get(@PathVariable Long id){
        return adminService.getOrderDetailByOrderId(id);
    }

    @GetMapping("/foods/{id}")
    public ResponseEntity<FoodItem> getFoodById(@PathVariable Long id) {
        FoodItem foodItem = adminService.getFoodItemById(id);
        return ResponseEntity.ok(foodItem);
    }

    @GetMapping("/users")
    public List<User> getUsers(){
        return userService.getAll();
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<User> updateUserEnabled(@PathVariable Long id, @RequestBody Map<String, Boolean> updates) {
        User updatedUser = userService.updateUserEnabled(id, updates.get("enabled"));
        return ResponseEntity.ok(updatedUser);
    }
}
