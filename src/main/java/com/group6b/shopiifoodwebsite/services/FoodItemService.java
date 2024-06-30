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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE,
        rollbackFor = {Exception.class, Throwable.class})
@Service
public class FoodItemService {
    @Autowired
    private FoodItemRepository foodItemRepository;

    public List<FoodItem> getRandomFoodItems(int count) {
        List<FoodItem> allFoods = foodItemRepository.findAll();
        Random random = new Random();
        return IntStream.range(0, count)
                .mapToObj(i -> allFoods.get(random.nextInt(allFoods.size())))
                .collect(Collectors.toList());
    }
    public List<FoodItem> getAllFood(Integer pageNo, Integer pageSize, String sortBy) {
        return foodItemRepository.findAllFoods(pageNo, pageSize, sortBy);
    }
    public List<FoodItem> getAllFood(){
        return foodItemRepository.findAll();
    }
    public Optional<FoodItem> getFoodById(Long id) {
        return foodItemRepository.findById(id);
    }


    public void addFood(FoodItem foodItem) {
        foodItemRepository.save(foodItem);
    }

    public FoodItem updateFood(@NotNull FoodItem foodItem, MultipartFile mainPicture,List<MultipartFile> pictures) throws IOException {
        FoodItem existingFood = foodItemRepository.findById(foodItem.getId())
                .orElseThrow(() -> new IllegalArgumentException("Food with id " + foodItem.getId() + " not found."));

        // Cập nhật các trường khác
        existingFood.setFoodName(foodItem.getFoodName());
        existingFood.setDescription(foodItem.getDescription());
        existingFood.setPrice(foodItem.getPrice());
        existingFood.setCategory(foodItem.getCategory());
        existingFood.setRestaurant(foodItem.getRestaurant());
        // Cập nhật ảnh nếu có
        if (mainPicture != null && !mainPicture.isEmpty()) {
            String imageSavePath = "src/main/resources/static/foodimages/";  // Đường dẫn lưu ảnh
            String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(mainPicture.getOriginalFilename());
            Path imagePath = Paths.get(imageSavePath + fileName);
            mainPicture.transferTo(imagePath);
            existingFood.setMainPicture("/foodimages/" + fileName);  // Đường dẫn truy cập ảnh
        }
        if (pictures != null && !pictures.isEmpty()) {
            // Xóa các ảnh cũ
            existingFood.getPictures().clear();

            // Thêm các ảnh mới
            for (MultipartFile picture : pictures) {
                if (!picture.isEmpty()) {
                    String imageSavePath = "src/main/resources/static/foodimages/";
                    String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(picture.getOriginalFilename());
                    Path imagePath = Paths.get(imageSavePath + fileName);
                    picture.transferTo(imagePath);

                    PictureList pictureList = new PictureList();
                    pictureList.setFoodItem(existingFood);
                    pictureList.setUrl("/foodimages/" + fileName);

                    existingFood.getPictures().add(pictureList);
                }
            }
        }
        return foodItemRepository.save(existingFood);
    }


    public void deleteFoodById(Long id) {
        foodItemRepository.deleteById(id);
    }

    public List<FoodItem> searchFood(String keyword) {
        return foodItemRepository.searchFood(keyword);
    }

    public List<FoodItem> getFoodItemsByCategoryId(long categoryId) {
        return foodItemRepository.findByCategoryId(categoryId);
    }
    public List<FoodItem> getFoodItemsByRestaurantId(long restaurantId) {
        return foodItemRepository.findByRestaurantId(restaurantId);
    }


    public void save(FoodItem foodItem) {
        foodItemRepository.save(foodItem);
    }
}
