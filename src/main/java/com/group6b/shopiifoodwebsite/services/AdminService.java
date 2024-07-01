package com.group6b.shopiifoodwebsite.services;

import com.group6b.shopiifoodwebsite.entities.*;
import com.group6b.shopiifoodwebsite.repositories.CategoryRepository;
import com.group6b.shopiifoodwebsite.repositories.FoodItemRepository;
import com.group6b.shopiifoodwebsite.repositories.OrderDetailRepository;
import com.group6b.shopiifoodwebsite.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private final ImageStorageService imageStorageService;
    @Autowired
    private FoodItemRepository foodItemRepository;

    public AdminService(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    //Category
    public Optional<Category> getCategory(Long id){
        return categoryRepository.findById(id);
    }

    //List categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    //Tạo category
    @Transactional
    public void createCategory(Category category, MultipartFile icon) throws IOException {
        Category savCategory = categoryRepository.save(category);
        Long categoryId = savCategory.getId();

        String mainImageUrl = imageStorageService.saveImage(icon, categoryId);
        savCategory.setCategoryIcon(mainImageUrl);

        categoryRepository.save(savCategory);
    }

    //UpdateCategory
    @Transactional
    public Category updateCategory(Long id, String categoryDescription, MultipartFile categoryIcon) throws Exception{
        Category saveCategory = getCategory(id).orElseThrow(() -> new IllegalArgumentException("Category not found"));
        saveCategory.setCategoryDescription(categoryDescription);

        String iconUrl = imageStorageService.saveIconCategory(categoryIcon,id);
        saveCategory.setCategoryIcon(iconUrl);
        return categoryRepository.save(saveCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Delete all associated icons if necessary
        imageStorageService.deleteAllIconCategory(id);

        // Delete the category from the repository
        categoryRepository.delete(category);
    }

    public List<FoodItem> getFoodByCategoryId(Long id) {
        return foodItemRepository.findByCategoryId(id);
    }

    public List<Order> getALlOrder() {
        return orderRepository.findAll();
    }

    public List<OrderDetail> getOrderDetailByOrderId(Long id) {
        return orderDetailRepository.findAllByOrderId(id);
    }

    public FoodItem getFoodItemById(Long id) {
        return foodItemRepository.findFoodItemById(id);
    }
}
