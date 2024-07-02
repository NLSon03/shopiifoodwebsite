package com.group6b.shopiifoodwebsite.services;

import com.group6b.shopiifoodwebsite.entities.*;
import com.group6b.shopiifoodwebsite.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
    @Autowired
    private ImageStorageService imageStorageService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

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

    public void saveFoodItem(FoodItem foodItem, MultipartFile mainImage, MultipartFile[] images) throws IOException {
        if (foodItem == null || mainImage == null || images == null) {
            throw new IllegalArgumentException("Food item, main image, and images must not be null");
        }

        User currentUser = getCurrentUser();
        if (currentUser == null || currentUser.getRestaurant() == null) {
            throw new IllegalStateException("Current user or user's restaurant must not be null");
        }
        foodItem.setRestaurant(currentUser.getRestaurant());

        // Save food item to get an ID
        FoodItem savedFoodItem = foodItemRepository.save(foodItem);
        Long foodId = savedFoodItem.getId();

        // Save main image
        String mainImageUrl = imageStorageService.saveImage(mainImage, foodId);
        savedFoodItem.setMainPicture(mainImageUrl);

        // Save additional images
        List<PictureList> pictureList = new ArrayList<>();
        for (MultipartFile image : images) {
            String imageUrl = imageStorageService.saveImage(image, foodId);
            PictureList picture = new PictureList();
            picture.setUrl(imageUrl);
            picture.setFoodItem(savedFoodItem);
            pictureList.add(picture);
        }
        savedFoodItem.setPictures(pictureList);

        // Save food item with updated pictures
        foodItemRepository.save(savedFoodItem);
    }

/*
    public FoodItem saveFoodItem(FoodItem foodItem) {
        User currentUser = getCurrentUser();
        foodItem.setRestaurant(currentUser.getRestaurant());
        return foodItemRepository.save(foodItem);
    }
*/

    @Transactional
    public FoodItem updateFoodItem(Long id, FoodItem foodItem, MultipartFile mainImage, MultipartFile[] images) throws IOException {
        User currentUser = getCurrentUser();
        FoodItem existingFoodItem = getFoodItemById(id);
        if (!existingFoodItem.getRestaurant().getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Unauthorized access");
        }

        // Update fields
        existingFoodItem.setFoodName(foodItem.getFoodName());
        existingFoodItem.setPrice(foodItem.getPrice());
        existingFoodItem.setDescription(foodItem.getDescription());
        existingFoodItem.setCategory(foodItem.getCategory());

        // Update main image
        if (mainImage != null && !mainImage.isEmpty()) {
            String mainImageUrl = imageStorageService.saveImage(mainImage, id);
            existingFoodItem.setMainPicture(mainImageUrl);
        }

        // Update additional images
        if (images != null && images.length > 0) {
            List<PictureList> pictureList = new ArrayList<>();
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    String imageUrl = imageStorageService.saveImage(image, id);
                    PictureList picture = new PictureList();
                    picture.setUrl(imageUrl);
                    picture.setFoodItem(existingFoodItem);
                    pictureList.add(picture);
                }
            }
            existingFoodItem.setPictures(pictureList);
        }
        return foodItemRepository.save(existingFoodItem);
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

    public List<Order> getALlOrder() {
        User currentUser = getCurrentUser();
        return orderRepository.findOrdersByRestaurantId(currentUser.getRestaurant().getId());
    }

    public List<OrderDetail> getOrderDetailByOrderId(Long id) {
        return orderDetailRepository.findAllByOrderId(id);
    }
}
