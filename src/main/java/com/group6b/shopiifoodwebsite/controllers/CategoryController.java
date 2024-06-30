package com.group6b.shopiifoodwebsite.controllers;

import com.group6b.shopiifoodwebsite.entities.Category;
import com.group6b.shopiifoodwebsite.entities.Restaurant;
import com.group6b.shopiifoodwebsite.services.CategoryService;
import com.group6b.shopiifoodwebsite.services.FoodItemService;
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
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final FoodItemService foodItemService;

    @GetMapping
    public String showAllCategories(@NotNull Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "category/list-of-categories";
    }


    @GetMapping("/add")
    public String showAddForm(@NotNull Model model) {
        model.addAttribute("category", new Category());
        return "category/add";
    }

    // Create a new food item
    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute Category category,
                              @RequestParam("icon") MultipartFile icon, Model model,
                              BindingResult result) throws IOException {
        if (result.hasErrors()) {
            var errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "category/add";
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
                return "redirect:/categories/add";
            }
        }
        categoryService.addCategory(category);
        return "redirect:/categories";

    }

    @GetMapping("/edit/{id}")
    public String editCategoryForm(@NotNull Model model, @PathVariable long id) {
        var category = categoryService.getCategoryById(id);
        model.addAttribute("category", category.orElseThrow(() -> new
                IllegalArgumentException("Book not found")));
        return "category/edit";
    }

    @PostMapping("/edit")
    public String editCategory(@Valid @ModelAttribute("category") Category category,
                               @RequestParam("icon") MultipartFile icon,
                               @NotNull BindingResult bindingResult,
                               Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "category/edit";

        }
        categoryService.updateCategory(category,icon);
        return "redirect:/categories";
    }

    @GetMapping("/details/{id}")
    public String viewCategoryDetails(@PathVariable long id, Model model) {
        var category = categoryService.getCategoryById(id).orElseThrow(() -> new IllegalArgumentException("Food not found"));
        model.addAttribute("category", category);
        model.addAttribute("foodsByCategory", foodItemService.getFoodItemsByCategoryId(id));
        return "category/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable long id) {
        if (categoryService.getCategoryById(id).isPresent())
            categoryService.deleteCategoryById(id);
        return "redirect:/categories";
    }

}
