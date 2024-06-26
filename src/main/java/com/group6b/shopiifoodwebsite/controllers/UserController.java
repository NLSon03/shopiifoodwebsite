package com.group6b.shopiifoodwebsite.controllers;

import com.group6b.shopiifoodwebsite.entities.Restaurant;
import com.group6b.shopiifoodwebsite.entities.User;
import com.group6b.shopiifoodwebsite.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "auth/register";
        }
        userService.save(user);
        return "redirect:/login";
    }
    @GetMapping("/register/restaurant")
    public String showRestaurantRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("restaurant", new Restaurant());
        return "auth/register-restaurant";
    }

    @PostMapping("/register/restaurant")
    public String registerRestaurant(@ModelAttribute("user") User user,
                                     @ModelAttribute("restaurant") Restaurant restaurant,
                                     @RequestParam("image") MultipartFile image,
                                     RedirectAttributes redirectAttributes) {
        try {
            if (!image.isEmpty()) {
                    String imageSavePath = "src/main/resources/static/restaurantpictures/";  // Replace with your actual path
                    String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
                    Path imagePath = Paths.get(imageSavePath + fileName);
                    image.transferTo(imagePath);
                    restaurant.setRestaurantPicture("/restaurantpictures/" + fileName);  // Adjust the path based on your storage location
            }
            userService.createRestaurantAccount(user, restaurant);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký tài khoản nhà hàng thành công.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đăng ký tài khoản nhà hàng thất bại: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/register/restaurant";
        }
    }
}