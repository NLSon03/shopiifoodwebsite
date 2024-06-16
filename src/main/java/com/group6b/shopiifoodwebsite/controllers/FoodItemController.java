package com.group6b.shopiifoodwebsite.controllers;

import com.group6b.shopiifoodwebsite.entities.FoodItem;
import com.group6b.shopiifoodwebsite.services.CategoryService;
import com.group6b.shopiifoodwebsite.services.FoodItemService;
import com.group6b.shopiifoodwebsite.services.RestaurantService;
import jakarta.jws.WebParam;
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
import java.util.*;

@Controller
@RequestMapping("/foods")
@RequiredArgsConstructor
public class FoodItemController {

    private final FoodItemService foodItemService;
    private  final CategoryService categoryService;
    private final RestaurantService restaurantService;
    @GetMapping
    public String showAllFoods( @NotNull Model model,
                                     @RequestParam(defaultValue = "0") Integer pageNo,
                                     @RequestParam(defaultValue = "20") Integer pageSize,
                                     @RequestParam(defaultValue = "id") String sortBy ){
        model.addAttribute("foods",foodItemService.getAllFood(pageNo,pageSize,sortBy));
        model.addAttribute("categories",categoryService.getAllCategories());
        model.addAttribute("totalPages",foodItemService.getAllFood(pageNo,pageSize,sortBy).size() / pageSize);
        return "food/list-of-foods";
    }
    private String saveImageStatic(MultipartFile image) throws IOException {
        File saveFile = new ClassPathResource("static/foodimages").getFile();
        String fileName = UUID.randomUUID()+ "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
        Path path = Paths.get(IMAGE_PATH  + fileName);
        Files.copy(image.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }
    // Show form to create a new food item
    @GetMapping("/add")
    public String showAddForm(@NotNull Model model) {
        model.addAttribute("foodItem", new FoodItem());
        model.addAttribute("categories",categoryService.getAllCategories());
        model.addAttribute("restaurants",restaurantService.getAllRestaurants());
        return "food/add";
    }

    // Create a new food item
    @PostMapping("/add")
    public String addFoodItem(@Valid @ModelAttribute FoodItem foodItem, Model model,
                              @RequestParam("mainPicture") MultipartFile picture,
                              BindingResult result) throws IOException {
        if(result.hasErrors()){
            var errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
            return "food/add";
        }
        if(!picture.isEmpty()){
            try{
                String imageName = saveImageStatic(picture);
                foodItem.setMainPicture("/foodimages/"+imageName);
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
        foodItemService.addFood(foodItem);
        return "redirect:/foods";
    }
    private final String IMAGE_PATH = "src/main/resources/static/foodimages/";
    // Show form to update an existing food item
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable long id,@NotNull Model model) {
        var foodItem = foodItemService.getFoodById(id);
        model.addAttribute("food", foodItem.orElseThrow(() -> new
                IllegalArgumentException("Food not found")));
        model.addAttribute("categories",
                categoryService.getAllCategories());
        model.addAttribute("restaurants",restaurantService.getAllRestaurants());

        return "food/edit";
    }


    // Update an existing food item
    @PostMapping("/edit")
    public String updateFoodItem(@NotNull BindingResult result, Model model,
                                 @ModelAttribute FoodItem foodItem,
                                 @RequestParam("mainPicture") MultipartFile mainPicture) throws IOException {
        if(result.hasErrors()){
            var errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
            model.addAttribute("errors", errors);
            model.addAttribute("categories",
                    categoryService.getAllCategories());
            model.addAttribute("restaurants",restaurantService.getAllRestaurants());
            return "food/edit";
        }

        foodItemService.updateFood(foodItem, mainPicture);
        return "redirect:/foods";
    }

    // Delete a food item
    @GetMapping("/delete/{id}")
    public String deleteFoodItem(@PathVariable Long id) {
        foodItemService.getFoodById(id).ifPresentOrElse(book->foodItemService.deleteFoodById(id),()->{ throw new IllegalArgumentException("Food not found"); });
        return "redirect:/foods";
    }
}
