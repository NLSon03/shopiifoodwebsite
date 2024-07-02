package com.group6b.shopiifoodwebsite.controllers;

import com.group6b.shopiifoodwebsite.entities.Category;
import com.group6b.shopiifoodwebsite.entities.FoodItem;
import com.group6b.shopiifoodwebsite.entities.PictureList;
import com.group6b.shopiifoodwebsite.services.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final FoodItemService foodItemService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final RestaurantService restaurantService;

    public AdminController(UserService userService, FoodItemService foodItemService, OrderService orderService, CategoryService categoryService, RestaurantService restaurantService) {
        this.userService = userService;
        this.foodItemService = foodItemService;
        this.orderService = orderService;
        this.categoryService = categoryService;
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String admin() {
        return "adminDashboard/dashboard";
    }

    @GetMapping("/list-foods")
    public String getFoodItems(@NotNull Model model) {
        model.addAttribute("foods", foodItemService.getAllFood());
        return "adminDashboard/list-foods";
    }

    @GetMapping("/statistical")
    public String showStatistical(Model model) {
        return "adminDashboard/statistical";
    }
    @GetMapping("/order-lists")
    public String getOrderLists(Model model) {
        model.addAttribute("orders",orderService.getAllOrders());
        return "adminDashboard/order-list";
    }
    @GetMapping("/add-food-item")
    public String showAddForm(@NotNull Model model) {
        model.addAttribute("foodItem", new FoodItem());
        model.addAttribute("categories",categoryService.getAllCategories());
        model.addAttribute("restaurants",restaurantService.getAllRestaurants());
        return "adminDashboard/create";
    }

    // Create a new food item
    @PostMapping("/add-food-item")
    public String addFoodItem(@Valid @ModelAttribute FoodItem foodItem,
                              @RequestParam("image") MultipartFile image,
                              @RequestParam("images") List<MultipartFile> pictures, Model model,
                              BindingResult result) throws IOException {
        if (result.hasErrors()) {
            var errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "adminDashboard/create";
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
                return "redirect:/admin/add-food-item";  // Redirect back to the add form with error handling
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
                        return "redirect:/admin/add-food-item"; // Chuyển hướng lại form thêm với thông báo lỗi
                    }
                }
            }
        }

        foodItem.setPictures(pictureList);
        foodItemService.addFood(foodItem);
        return "redirect:/admin/list-foods";
    }
    @GetMapping("/list-foods/edit/{id}")
    public String showUpdateForm(@PathVariable Long id,@NotNull Model model) {
        var foodItem = foodItemService.getFoodById(id);
        model.addAttribute("food", foodItem.orElseThrow(() -> new
                IllegalArgumentException("Food not found")));
        model.addAttribute("categories",categoryService.getAllCategories());
        model.addAttribute("restaurants",restaurantService.getAllRestaurants());
        return "adminDashboard/edit";
    }
    // Update an existing food item
    @PostMapping("/list-foods/edit")
    public String updateFoodItem(
            @Valid @ModelAttribute("food") FoodItem foodItem,
            @NotNull BindingResult result, Model model,
            @RequestParam("image") MultipartFile mainPicture,
            @RequestParam("images") List<MultipartFile> pictures) throws IOException {
        if(result.hasErrors()){
            var errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
            model.addAttribute("errors", errors);
            model.addAttribute("categories",categoryService.getAllCategories());
            model.addAttribute("restaurants",restaurantService.getAllRestaurants());
            return "adminDashboard/edit";
        }
        foodItemService.updateFood(foodItem, mainPicture, pictures);
        return "redirect:/admin/list-foods";
    }

    // Delete a food item
    @GetMapping("/list-foods/delete/{id}")
    public String deleteFoodItem(@PathVariable Long id) {
        foodItemService.getFoodById(id).ifPresentOrElse(book -> foodItemService.deleteFoodById(id), () -> {
            throw new IllegalArgumentException("Food not found");
        });
        return "redirect:/admin/list-foods";
    }
    @GetMapping("/list-foods/details/{id}")
    public String viewFoodItemDetails(@PathVariable long id, Model model) {
        var foodItem = foodItemService.getFoodById(id).orElseThrow(() -> new IllegalArgumentException("Food not found"));
        model.addAttribute("foodItem", foodItem);
        return "sellerDashboard/details";
    }
    @GetMapping("/foods/{id}")
    public String getFoodById(@PathVariable Long id) {
        return "adminDashboard/details";
    }


    @GetMapping("/foods/categories/{id}")
    public String getFoodByCategory(@PathVariable Long id) {
        return "adminDashboard/list-foods-by-category";
    }
    //api
    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable Long id) {
        return "adminDashboard/edit-category";
    }

    //api
    @GetMapping("/categories/create")
    public String createCategory() {
        return "adminDashboard/createCategory";
    }

    @GetMapping("/list-categories")
    public String getCategories() {
        return "adminDashboard/list-categories";
    }

    @GetMapping("/add-category")
    public String showAddCategoryForm(@NotNull Model model) {
        model.addAttribute("category", new Category());
        return "adminDashboard/createCategory";
    }

    // Create a new food item
    @PostMapping("/add-category")
    public String addCategory(@Valid @ModelAttribute Category category,
                              @RequestParam("icon") MultipartFile icon, Model model,
                              BindingResult result) throws IOException {
        if (result.hasErrors()) {
            var errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "adminDashboard/createCategory";
        }
        if (!icon.isEmpty()) {
            try {
                String imageSavePath = "src/main/resources/static/categoryicons/";  // Replace with your actual path
                String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(icon.getOriginalFilename());
                Path imagePath = Paths.get(imageSavePath + fileName);
                icon.transferTo(imagePath);
                category.setCategoryIcon("/categoryicons/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/admin/add-category";
            }
        }
        categoryService.addCategory(category);
        return "redirect:/admin/list-categories";
    }
    @GetMapping("/list-categories/delete/{id}")
    public String deleteCategory(@PathVariable long id) {
        if (categoryService.getCategoryById(id).isPresent())
            categoryService.deleteCategoryById(id);
        return "redirect:/admin/list-categories";
    }

    @GetMapping("/list-categories/foodByCategories/{id}")
    public String getFoodByCategory(@PathVariable long id, Model model) {
        var category = categoryService.getCategoryById(id).orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        model.addAttribute("category",category);
        model.addAttribute("FoodByCategories",foodItemService.getFoodItemsByCategoryId(category.getId()));
        return "adminDashboard/list-foods-by-category";
    }
    @GetMapping("/orders")
    public String getOrderLists() {
        return "adminDashboard/order-list";
    }

    @GetMapping("/orders/{id}")
    public String getOrderDetails(@PathVariable String id){
        return "adminDashboard/order-detail";
    }

    @GetMapping("/users")
    public String getUsers(){
        return "adminDashboard/users";
    }
}
