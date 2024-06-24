package com.group6b.shopiifoodwebsite.controllers;


import com.group6b.shopiifoodwebsite.daos.CartItem;
import com.group6b.shopiifoodwebsite.entities.*;

import com.group6b.shopiifoodwebsite.repositories.OrderStatusRepository;
import com.group6b.shopiifoodwebsite.services.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping
    public String showCart(HttpSession session,
                           @NotNull Model model) {
        model.addAttribute("cart", cartService.getCart(session));
        model.addAttribute("totalPrice",
                cartService.getSumPrice(session));
        model.addAttribute("totalQuantity",
                cartService.getSumQuantity(session));
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
    public String increase(HttpSession session,@PathVariable Long id, @PathVariable int quantity) {
        var cart = cartService.getCart(session);
        cart.increaseItems(id, 1);
        return "redirect:/cart";
    }
    @GetMapping("decrease/{id}/{quantity}")
    public String decrease(HttpSession session,@PathVariable Long id, @PathVariable int quantity) {
        var cart = cartService.getCart(session);
        cart.decreaseItems(id, 1);
        return "redirect:/cart";
    }
    @GetMapping("/clearCart")
    public String clearCart(HttpSession session) {
        cartService.removeCart(session);
        return "redirect:/cart";
    }

}
