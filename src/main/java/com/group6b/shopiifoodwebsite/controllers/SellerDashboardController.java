package com.group6b.shopiifoodwebsite.controllers;


import com.group6b.shopiifoodwebsite.entities.*;
import com.group6b.shopiifoodwebsite.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/seller")
public class SellerDashboardController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private ImageStorageService imageStorageService;

    // --- Category Management ---

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return sellerService.getAllCategories();
    }

    @GetMapping("/categories/{id}")
    public Optional<Category> getCategoryById(@PathVariable Long id) {
        return sellerService.getCategoryById(id);
    }

    // --- FoodItem Management ---

    // FoodItem Endpoints
    @GetMapping("/fooditems")
    public List<FoodItem> getAllFoodItems() {
        return sellerService.getAllFoodItems();
    }

    @GetMapping("/fooditem/{id}")
    public ResponseEntity<FoodItem> getFoodById(@PathVariable Long id) {
        FoodItem foodItem = sellerService.getFoodItemById(id);
        return ResponseEntity.ok(foodItem);
    }

    @GetMapping("/fooditems/{id}")
    public FoodItem getFoodItemById(@PathVariable Long id) {
        return sellerService.getFoodItemById(id);
    }

    @PostMapping("/fooditems")
    public ResponseEntity<?> createFoodItem(
            @RequestParam("foodName") String foodName,
            @RequestParam("price") double price,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("mainImage") MultipartFile mainImage,
            @RequestParam("images") MultipartFile[] images) {

        if (foodName == null || description == null || categoryId == null || mainImage == null || images == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All fields are required");
        }

        try {
            FoodItem foodItem = new FoodItem();
            foodItem.setFoodName(foodName);
            foodItem.setPrice(price);
            foodItem.setDescription(description);
            Category category = categoryService.getCategoryById(categoryId).orElse(null);
            foodItem.setCategory(category);

            sellerService.saveFoodItem(foodItem, mainImage, images);
            return ResponseEntity.ok(Collections.singletonMap("message", "Food item added successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/fooditems/{id}")
    public ResponseEntity<FoodItem> updateFoodItem(
            @PathVariable Long id,
            @RequestPart("foodItem") FoodItem foodItem,
            @RequestPart("mainImage") MultipartFile mainImage,
            @RequestPart("images") MultipartFile[] images) throws IOException {
        imageStorageService.deleteAllImages(id); // Gọi hàm xóa hình ảnh cũ
        FoodItem updatedFoodItem = sellerService.updateFoodItem(id, foodItem, mainImage, images);
        return ResponseEntity.ok(updatedFoodItem);
    }
    @DeleteMapping("/fooditems/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable Long id) {
        sellerService.deleteFoodItem(id);
        return ResponseEntity.noContent().build();
    }

    //order
    @GetMapping("/orders")
    public List<Order> getAllOrder()
    {
        return sellerService.getALlOrder();
    }
    //order-detail
    @GetMapping("/orders/{id}")
    public List<OrderDetail> getOrderDetail(@PathVariable Long id){
        return sellerService.getOrderDetailByOrderId(id);
    }
}
