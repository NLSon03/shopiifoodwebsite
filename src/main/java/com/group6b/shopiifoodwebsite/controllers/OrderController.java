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

import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;
    private final FoodItemService foodItemService;
    private final RestaurantService restaurantService;

    public OrderController(OrderService orderService, UserService userService, CartService cartService, FoodItemService foodItemService, RestaurantService restaurantService) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
        this.foodItemService = foodItemService;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/cancel/{id}")
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
    @GetMapping("/accept/{id}")
    public String acceptOrder(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User not authenticated");
        }
        String username = authentication.getName();
        Restaurant restaurant = restaurantService.getRestaurantByName(username).orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        try {
            boolean accepted = orderService.acceptOrder(id, restaurant.getId());
            if (accepted) {
                redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã được chấp nhận.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể chấp nhận đơn hàng.");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/orders/order";
    }
    @GetMapping("/complete/{id}")
    public String completeOrder(@PathVariable Long id, @AuthenticationPrincipal User userPrincipal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User not authenticated");
        }
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        orderService.completeOrder(id, user.getId());
        return "redirect:/orders/order";
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

        List<OrderDetail> orderDetails = cart.getCartItems().stream()
                .map(cartItem -> {
                    FoodItem foodItem = foodItemService.getFoodById(cartItem.getFoodId())
                        .orElseThrow(() -> new IllegalArgumentException("Food item not found"));
        return new OrderDetail(order, foodItem, cartItem.getQuantity(), cartItem.getPrice());
            }).collect(Collectors.toList());

        order.setTotalPrice(cartService.getSumPrice(session));
        order.setOrderItems(orderDetails);
        orderService.createOrder(order);

        cartService.removeCart(session);
        return "redirect:/orders/confirmation";
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Your order has been successfully placed.");
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
        Order order = orderService.findOrdersById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        model.addAttribute("order", order);
        return "order/details";
    }
}
