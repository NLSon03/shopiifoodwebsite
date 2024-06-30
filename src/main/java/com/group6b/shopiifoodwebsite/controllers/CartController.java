package com.group6b.shopiifoodwebsite.controllers;


import com.group6b.shopiifoodwebsite.daos.CartItem;
import com.group6b.shopiifoodwebsite.entities.*;

import com.group6b.shopiifoodwebsite.repositories.OrderStatusRepository;
import com.group6b.shopiifoodwebsite.services.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final HttpSession session;
    private final UserService userService;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderService orderService;
    private final FoodItemService foodItemService;


    @GetMapping
    public String showCart(HttpSession session,
                           @NotNull Model model) {
        model.addAttribute("cart", cartService.getCart(session));
        model.addAttribute("totalPrice",
                cartService.getSumPrice(session));
        model.addAttribute("totalQuantity",
                cartService.getSumQuantity(session));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User not authenticated");
        }
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("user", user);
        return "cart/index";
    }

    @GetMapping("/removeFromCart/{id}")
    public String removeFromCart(HttpSession session,
                                 @PathVariable Long id) {
        var cart = cartService.getCart(session);
        cart.removeItems(id);
        return "redirect:/cart";
    }

    @GetMapping("/updateCart/{id}/{quantity}")
    public String updateCart(HttpSession session,
                             @PathVariable Long id,
                             @PathVariable int quantity) {
        var cart = cartService.getCart(session);

        cart.updateItems(id, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/increase/{id}/{quantity}")
    public String increase(HttpSession session, @PathVariable Long id, @PathVariable int quantity) {
        var cart = cartService.getCart(session);
        cart.increaseItems(id, 1);
        return "redirect:/cart";
    }

    @GetMapping("decrease/{id}/{quantity}")
    public String decrease(HttpSession session, @PathVariable Long id, @PathVariable int quantity) {
        var cart = cartService.getCart(session);
        cart.decreaseItems(id, 1);
        return "redirect:/cart";
    }

    @GetMapping("/clearCart")
    public String clearCart(HttpSession session) {
        cartService.removeCart(session);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session,
                           @RequestParam(required = false) String deliveryAddress,
                           Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User not authenticated");
        }
        var cart = cartService.getCart(session);

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (deliveryAddress == null || deliveryAddress.isBlank()) {
            deliveryAddress = user.getDefaultDeliveryAddress();
            if (deliveryAddress == null || deliveryAddress.isBlank()) {
                model.addAttribute("error", "Vui lòng nhập địa chỉ giao hàng!");
                return "cart";
            }
        }

        // Create a list to store the order details
        List<OrderDetail> orderDetails = new ArrayList<>();
        // Retrieve the restaurant from the first food item in the cart (assuming all items are from the same restaurant)
        Long firstFoodItemId = cart.getCartItems().get(0).getFoodId(); // Assuming cart is not empty
        Optional<FoodItem> firstFoodItemOptional = foodItemService.getFoodById(firstFoodItemId);

        if (firstFoodItemOptional.isPresent()) {
            FoodItem firstFoodItem = firstFoodItemOptional.get();
            Restaurant restaurant = firstFoodItem.getRestaurant();

            // Create order details for each item in the cart


            // Create the order and set its attributes
            Order order = new Order();
            order.setUser(user);
            order.setDeliveryAddress(deliveryAddress);
            order.setTotalPrice(cartService.getSumPrice(session));
            orderDetails = cart.getCartItems().stream()
                    .map(cartItem -> {
                        FoodItem foodItem = foodItemService.getFoodById(cartItem.getFoodId())
                                .orElseThrow(() -> new IllegalArgumentException("Food item not found"));
                        return new OrderDetail(order, foodItem, cartItem.getQuantity(), cartItem.getPrice());
                    }).collect(Collectors.toList());
            order.setOrderItems(orderDetails);
            order.setRestaurant(restaurant); // Set the restaurant
            // Save the order
            orderService.createOrder(order);

            // Remove the cart items from session
            cartService.removeCart(session);
        } else {
            throw new IllegalArgumentException("Food items in cart not found");
        }

        return "redirect:/orders/confirmation";
    }

}
