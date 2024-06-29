package com.group6b.shopiifoodwebsite.controllers;

import com.group6b.shopiifoodwebsite.entities.*;
import com.group6b.shopiifoodwebsite.services.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;


    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User not authenticated");
        }
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        try {
            boolean cancelled = orderService.cancelOrder(id, user.getId());
            if (cancelled) {
                redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã được hủy thành công.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể hủy đơn hàng do đã quá thời hạn cho phép.");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/orders/order";
    }


    @PostMapping("/complete/{id}")
    public String completeOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User not authenticated");
        }
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        try {
            orderService.completeOrder(id, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã được hoàn thành.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/orders/order";
    }


    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Bạn đã đặt đơn thành công.");
        return "cart/order-confirmation";
    }

    @GetMapping("/order")
    public String showOrderList(@NotNull Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User not authenticated");
        }
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Order> orders = orderService.findByUserId(user.getId());
        model.addAttribute("orderLists", orders);
        return "order/order-list";
    }

    @GetMapping("/order/details/{id}")
    public String viewOrderDetails(@PathVariable Long id, Model model) {
        Optional<Order> orderOptional = orderService.findOrdersById(id);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            model.addAttribute("order", order);
            // Add logging to check order details
        } else {
            // Handle case where order is not found
            model.addAttribute("error", "Order not found!");
        }

        return "order/details";
    }

}
