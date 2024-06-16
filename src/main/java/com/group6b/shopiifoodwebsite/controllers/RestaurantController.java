package com.group6b.shopiifoodwebsite.controllers;

import com.group6b.shopiifoodwebsite.entities.FoodItem;
import com.group6b.shopiifoodwebsite.entities.Restaurant;
import com.group6b.shopiifoodwebsite.services.RestaurantService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final String IMAGE_PATH = "src/main/resources/static/restaurantimages/";

    @GetMapping
    public String showAllRestaurant(@NotNull Model model){
        model.addAttribute("restaurants",restaurantService.getAllRestaurants());
        return "restaurant/list-of-restaurants";
    }
    private String saveImageStatic(MultipartFile image) throws IOException {
        File saveFile = new ClassPathResource("static/restaurantimages").getFile();
        String fileName = UUID.randomUUID()+ "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
        Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
        Files.copy(image.getInputStream(), path);
        return fileName;
    }

    @GetMapping("/add")
    public String showAddForm(@NotNull Model model) {
        model.addAttribute("restaurant", new Restaurant());
        return "restaurant/add";
    }

    // Create a new food item
    @PostMapping("/add")
    public String addFoodItem(@Valid @ModelAttribute Restaurant restaurant, Model model,
                              @RequestParam("restaurantPicture") MultipartFile picture,
                              BindingResult result) throws IOException {
        if(result.hasErrors()){
            var errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
            return "restaurant/add";
        }
        if(!picture.isEmpty()){
            try{
                String imageName = saveImageStatic(picture);
                restaurant.setRestaurantPicture("/restaurantimages/"+imageName);
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
        restaurantService.addRestaurant(restaurant);
        return "redirect:/restaurants";
    }
}
