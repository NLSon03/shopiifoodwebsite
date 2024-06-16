package com.group6b.shopiifoodwebsite.controllers;

import com.group6b.shopiifoodwebsite.entities.Category;
import com.group6b.shopiifoodwebsite.services.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String showAllRestaurant(@NotNull Model model){
        model.addAttribute("categories",categoryService.getAllCategories());
        return "category/list-of-categories";
    }
   /* @GetMapping("/add")
    public String addCategoryForm(@NotNull Model model) {
        model.addAttribute("category", new Category());
        return "category/add";
    }
    @PostMapping("/add")
    public String addCategory(
            @Valid @ModelAttribute("book") Category category,
            @NotNull BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "category/add";
        }
        categoryService.addCategory(category);
        return "redirect:/categories";
    }
    @GetMapping("/edit/{id}")
    public String editCategoryForm(@NotNull Model model, @PathVariable long id) {
        var bookCategory = categoryService.getCategoryById(id);
        model.addAttribute("category", bookCategory.orElseThrow(() -> new
                IllegalArgumentException("Book not found")));
        return "category/edit";
    }

    @PostMapping("/edit")
    public String editCategory(@Valid @ModelAttribute("category") Category bookCategory,@NotNull BindingResult bindingResult,
                               Model model) {
        if(bindingResult.hasErrors()){
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "category/edit";

        }
        categoryService.updateCategory(bookCategory);
        return "redirect:/categories";
    }


    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable long id) {
        if (categoryService.getCategoryById(id).isPresent())
            categoryService.deleteCategoryById(id);
        return "redirect:/categories";
    }*/
}
