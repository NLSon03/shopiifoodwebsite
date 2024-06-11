package com.group6b.shopiifoodwebsite.services;


import com.group6b.shopiifoodwebsite.entities.FoodItem;
import com.group6b.shopiifoodwebsite.repositories.FoodItemRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE,
        rollbackFor = {Exception.class, Throwable.class})
@Service
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;

    public List<FoodItem> getAllFood(Integer pageNo,    Integer pageSize,    String sortBy) {
        return foodItemRepository.findAllFoods(pageNo, pageSize, sortBy);
    }

    public Optional<FoodItem> getFoodById(Long id){
        return foodItemRepository.findById(id);
    }

    public void addFood(FoodItem foodItem){
        foodItemRepository.save(foodItem);
    }

    public void updateBook(@NotNull FoodItem foodItem) {
        FoodItem existingFood = foodItemRepository.findById(foodItem.getId())
                .orElse(null);
        Objects.requireNonNull(existingFood).setFoodName(foodItem.getFoodName());
        existingFood.setDescription(foodItem.getDescription());
        existingFood.setPrice(foodItem.getPrice());
        existingFood.setCategory(foodItem.getCategory());
        foodItemRepository.save(existingFood);
    }

    public void deleteBookById(Long id) {
        foodItemRepository.deleteById(id);
    }
    public List<FoodItem> searchBook(String keyword) {
        return foodItemRepository.searchFood(keyword);
    }
}
