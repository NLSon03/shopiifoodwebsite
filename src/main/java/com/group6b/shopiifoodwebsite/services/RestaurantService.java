package com.group6b.shopiifoodwebsite.services;

import com.group6b.shopiifoodwebsite.entities.*;
import com.group6b.shopiifoodwebsite.repositories.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;

import lombok.RequiredArgsConstructor;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE,
        rollbackFor = {Exception.class, Throwable.class})
@Service
public class RestaurantService {
    @Autowired
    private final RestaurantRepository restaurantRepository;

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Optional<Restaurant> getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }

    public Restaurant addRestaurant(@Valid Restaurant restaurant)  throws IOException  {
        return restaurantRepository.save(restaurant);
    }
    public Optional<Restaurant> getRestaurantByName(String name) {
        return Optional.ofNullable(restaurantRepository.findByName(name));
    }
    public Restaurant updateRestaurant(@NotNull Restaurant restaurant,MultipartFile multipartFile)throws IOException {
        // Check if the restaurant exists
        Restaurant existingRestaurant = restaurantRepository.findById(restaurant.getId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurant with id " + restaurant.getId() + " not found."));

        // Update the fields
        existingRestaurant.setName(restaurant.getName());
        existingRestaurant.setAddress(restaurant.getAddress());
        existingRestaurant.setPhoneNumber(restaurant.getPhoneNumber());
        if(multipartFile !=null  && !multipartFile.isEmpty())
        {
            String imageSavePath = "src/main/resources/static/restaurantpictures/";  // Đường dẫn lưu ảnh
            String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
            Path imagePath = Paths.get(imageSavePath + fileName);
            multipartFile.transferTo(imagePath);
            existingRestaurant.setRestaurantPicture("/restaurantpictures/" + fileName);  // Đường dẫn truy cập ảnh
        }

        // Save and return the updated restaurant
        return restaurantRepository.save(existingRestaurant);
    }

    public void deleteRestaurantById(Long id) {
        restaurantRepository.deleteById(id);
    }
    private final String IMAGE_PATH = "src/main/resources/static/restaurantpictures/";

    private String saveImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }
        byte[] bytes = image.getBytes();
        Path path = Paths.get(IMAGE_PATH + image.getOriginalFilename());
        Files.write(path, bytes);
        return "/restaurantpictures/" + image.getOriginalFilename();
    }
}
