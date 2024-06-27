package com.group6b.shopiifoodwebsite.controllers;

import com.group6b.shopiifoodwebsite.daos.CartItem;
import com.group6b.shopiifoodwebsite.entities.FoodItem;
import com.group6b.shopiifoodwebsite.entities.PictureList;
import com.group6b.shopiifoodwebsite.services.CartService;
import com.group6b.shopiifoodwebsite.services.CategoryService;
import com.group6b.shopiifoodwebsite.services.FoodItemService;
import com.group6b.shopiifoodwebsite.services.RestaurantService;
import jakarta.jws.WebParam;
import jakarta.servlet.http.HttpSession;
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
    private final CategoryService categoryService;
    private final RestaurantService restaurantService;
    private final CartService cartService;

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
    public String addFoodItem(@Valid @ModelAttribute FoodItem foodItem,
                              @RequestParam("image") MultipartFile image,
                              @RequestParam("images") List<MultipartFile> pictures,
                              BindingResult result) throws IOException {
        if (result.hasErrors()) {
            var errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
            return "food/add";
        }
        if (!image.isEmpty()) {
            try {
                String imageSavePath = "src/main/resources/static/foodimages/";  // Replace with your actual path
                String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
                Path imagePath = Paths.get(imageSavePath + fileName);
                image.transferTo(imagePath);
                foodItem.setMainPicture("/foodimages/" + fileName);  // Adjust the path based on your storage location
            } catch (IOException ex) {
                ex.printStackTrace();
                // Handle any exceptions during image saving
                return "redirect:/foods/add";  // Redirect back to the add form with error handling
            }
        }
        List<PictureList> pictureList = new ArrayList<>();
        if (pictures != null && !pictures.isEmpty()) {
            for (MultipartFile picture : pictures) {
                if (!picture.isEmpty()) {
                    try {
                        String imageSavePath = "src/main/resources/static/foodimages/";
                        String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(picture.getOriginalFilename());
                        Path imagePath = Paths.get(imageSavePath + fileName);
                        picture.transferTo(imagePath);

                        PictureList newPicture = new PictureList();
                        newPicture.setFoodItem(foodItem);
                        newPicture.setUrl("/foodimages/" + fileName);
                        pictureList.add(newPicture);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // Xử lý ngoại lệ khi lưu ảnh
                        return "redirect:/foods/add";  // Chuyển hướng lại form thêm với thông báo lỗi
                    }
                }
            }
        }

        foodItem.setPictures(pictureList);
        foodItemService.addFood(foodItem);
        return "redirect:/foods";
    }
    // Show form to update an existing food item

    // Tìm kiếm món ăn
    @GetMapping("/search")
    public String searchFoods(@RequestParam("keyword") String keyword, Model model) {
        List<FoodItem> searchResults = foodItemService.searchFood(keyword);
        model.addAttribute("foods", searchResults);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "food/list-of-foods";
    }

    @GetMapping("/details/{id}")
    public String viewFoodItemDetails(@PathVariable long id, Model model) {
        var foodItem = foodItemService.getFoodById(id).orElseThrow(() -> new IllegalArgumentException("Food not found"));
        model.addAttribute("foodItem", foodItem);
        return "food/details";
    }
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable long id,@NotNull Model model) {
        var foodItem = foodItemService.getFoodById(id);
        model.addAttribute("food", foodItem.orElseThrow(() -> new
                IllegalArgumentException("Food not found")));
        model.addAttribute("categories",categoryService.getAllCategories());
        model.addAttribute("restaurants",restaurantService.getAllRestaurants());
        return "food/edit";
    }
    // Update an existing food item
    @PostMapping("/edit")
    public String updateFoodItem(
                                 @Valid @ModelAttribute("food") FoodItem foodItem,
                                 @NotNull BindingResult result, Model model,
                                 @RequestParam("image") MultipartFile mainPicture,
                                 @RequestParam("images") List<MultipartFile> pictures) throws IOException {
        if(result.hasErrors()){
            var errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
            model.addAttribute("errors", errors);
            model.addAttribute("categories",  categoryService.getAllCategories());
            model.addAttribute("restaurants",restaurantService.getAllRestaurants());
            return "food/edit";
        }

        foodItemService.updateFood(foodItem,mainPicture,pictures);
        return "redirect:/foods";
    }

    // Delete a food item
    @GetMapping("/delete/{id}")
    public String deleteFoodItem(@PathVariable Long id) {
        foodItemService.getFoodById(id).ifPresentOrElse(foodItem->foodItemService.deleteFoodById(id),()->{ throw new IllegalArgumentException("Food not found"); });
        return "redirect:/foods";
    }
    @PostMapping("/add-to-cart")
    public String addToCart(HttpSession session,
                            @RequestParam long id,
/*                            @RequestParam String name,
                            @RequestParam double price,
                            @RequestParam(required = false) String image,*/
                            @RequestParam(defaultValue = "1") int quantity) {
        var cart = cartService.getCart(session);
        FoodItem foodItem = foodItemService.getFoodById(id)
                .orElseThrow(() -> new IllegalArgumentException("Food item not found"));

        CartItem cartItem = new CartItem();
        cartItem.setFoodId(foodItem.getId());
        cartItem.setFoodName(foodItem.getFoodName());
        cartItem.setPrice(foodItem.getPrice());
        cartItem.setQuantity(quantity);
        cartItem.setImageUrl(foodItem.getMainPicture()); // Lấy URL của ảnh từ FoodItem
        cart.addItems(cartItem);
        cartService.updateCart(session, cart);
        return "redirect:/cart";
    }
}
