package com.group6b.shopiifoodwebsite.services;

import com.group6b.shopiifoodwebsite.entities.Category;
import com.group6b.shopiifoodwebsite.entities.FoodItem;
import com.group6b.shopiifoodwebsite.entities.User;
import com.group6b.shopiifoodwebsite.repositories.CategoryRepository;
import com.group6b.shopiifoodwebsite.repositories.FoodItemRepository;
import com.group6b.shopiifoodwebsite.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SellerService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private UserRepository userRepository;

    // Helper method to get current user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // Category Methods
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // FoodItem Methods
    public List<FoodItem> getAllFoodItems() {
        User currentUser = getCurrentUser();
        // Logic to get food items for the current seller
        return foodItemRepository.findAllByRestaurantUserId(currentUser.getId());
    }

    public FoodItem getFoodItemById(Long id) {
        User currentUser = getCurrentUser();
        // Logic to get food item by ID if it belongs to the current seller
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Food item not found"));
        if (!foodItem.getRestaurant().getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Unauthorized access");
        }
        return foodItem;
    }

    public FoodItem saveFoodItem(FoodItem foodItem) {
        User currentUser = getCurrentUser();
        foodItem.setRestaurant(currentUser.getRestaurant());
        return foodItemRepository.save(foodItem);
    }

    public FoodItem updateFoodItem(Long id, FoodItem foodItem) {
        User currentUser = getCurrentUser();
        FoodItem existingFoodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Food item not found"));
        if (!existingFoodItem.getRestaurant().getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Unauthorized access");
        }
        foodItem.setId(id);
        foodItem.setRestaurant(currentUser.getRestaurant());
        return foodItemRepository.save(foodItem);
    }

    public void deleteFoodItem(Long id) {
        User currentUser = getCurrentUser();
        FoodItem existingFoodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Food item not found"));
        if (!existingFoodItem.getRestaurant().getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Unauthorized access");
        }
        foodItemRepository.deleteById(id);
    }
}
