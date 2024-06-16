package com.group6b.shopiifoodwebsite.services;


import com.group6b.shopiifoodwebsite.entities.*;
import com.group6b.shopiifoodwebsite.repositories.FoodItemRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE,
        rollbackFor = {Exception.class, Throwable.class})
@Service
public class FoodItemService {
    @Autowired
    private FoodItemRepository foodItemRepository;


    public List<FoodItem> getAllFood(Integer pageNo,    Integer pageSize,    String sortBy) {
        return foodItemRepository.findAllFoods(pageNo, pageSize, sortBy);
    }

    public Optional<FoodItem> getFoodById(Long id){
        return foodItemRepository.findById(id);
    }



    public void addFood(FoodItem foodItem) {
        foodItemRepository.save(foodItem);
    }

    public void updateFood(@NotNull FoodItem foodItem, MultipartFile mainPicture) throws IOException {
        FoodItem existingFood = foodItemRepository.findById(foodItem.getId())
                .orElse(null);
        Objects.requireNonNull(existingFood).setFoodName(foodItem.getFoodName());
        existingFood.setDescription(foodItem.getDescription());
        existingFood.setPrice(foodItem.getPrice());
        existingFood.setCategory(foodItem.getCategory());

        if (mainPicture != null && !mainPicture.isEmpty()) {
            String mainPicturePath = saveImage(mainPicture);
            existingFood.setMainPicture(mainPicturePath);
        }
        foodItemRepository.save(existingFood);
    }

    public void deleteFoodById(Long id) {
        foodItemRepository.deleteById(id);
    }
    public List<FoodItem> searchFood(String keyword) {
        return foodItemRepository.searchFood(keyword);
    }

    private final String IMAGE_PATH = "src/main/resources/static/foodimages/";

    private String saveImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }
        byte[] bytes = image.getBytes();
        Path path = Paths.get(IMAGE_PATH + image.getOriginalFilename());
        Files.write(path, bytes);
        return "/foodimages/" + image.getOriginalFilename();
    }

}
