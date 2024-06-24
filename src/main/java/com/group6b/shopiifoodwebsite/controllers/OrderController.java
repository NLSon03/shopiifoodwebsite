package com.group6b.shopiifoodwebsite.controllers;

import com.group6b.shopiifoodwebsite.entities.Order;
import com.group6b.shopiifoodwebsite.entities.OrderDetail;
import com.group6b.shopiifoodwebsite.entities.User;
import com.group6b.shopiifoodwebsite.services.CartService;
import com.group6b.shopiifoodwebsite.services.OrderService;
import com.group6b.shopiifoodwebsite.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;


@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;

    public OrderController(OrderService orderService, UserService userService, CartService cartService) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
    }

    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, User userPrincipal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User not authenticated");
        }
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        orderService.cancelOrder(id, user.getId());
        return "redirect:/orders";
    }

    @PostMapping("/complete/{id}")
    public String completeOrder(@PathVariable Long id, @AuthenticationPrincipal User userPrincipal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User not authenticated");
        }
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        orderService.completeOrder(id, user.getId());
        return "redirect:/orders";
    }
    @GetMapping("/checkout")
    public String showCheckoutPage(@AuthenticationPrincipal User userPrincipal, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User not authenticated");
        }
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("user", user);
        return "cart/checkout";
    }
    @PostMapping("/checkout")
    public String checkout(HttpSession session,
                           @RequestParam(required = false) String deliveryAddress,
                           @AuthenticationPrincipal User userPrincipal,
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
                return "cart/checkout";
            }
        }
        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderItems(cart.getCartItems().stream()
                .map(cartItem -> new OrderDetail(cartItem.getFoodId(), cartItem.getFoodName(), cartItem.getQuantity(), cartItem.getPrice()))
                .collect(Collectors.toList()));
        order.setTotalPrice(cartService.getSumPrice(session));
        orderService.createOrder(order);

        cartService.removeCart(session);
        return "redirect:/orders/confirmation";
    }
    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Your order has been successfully placed.");
        return "cart/order-confirmation";
    }
}
