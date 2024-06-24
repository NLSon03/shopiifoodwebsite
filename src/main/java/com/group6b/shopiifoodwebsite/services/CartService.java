package com.group6b.shopiifoodwebsite.services;

import com.group6b.shopiifoodwebsite.daos.Cart;
import com.group6b.shopiifoodwebsite.daos.CartItem;
import com.group6b.shopiifoodwebsite.entities.*;
import com.group6b.shopiifoodwebsite.repositories.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE,
        rollbackFor = {Exception.class, Throwable.class})
public class CartService {
    private static final String CART_SESSION_KEY = "cart";
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final FoodItemRepository foodItemRepository;
    private final OrderStatusRepository orderStatusRepository;

    public Cart getCart(@NotNull HttpSession session) {
        return Optional.ofNullable((Cart)
                        session.getAttribute(CART_SESSION_KEY))
                .orElseGet(() -> {
                    Cart cart = new Cart(new ArrayList<>());
                    session.setAttribute(CART_SESSION_KEY, cart);
                    return cart;
                });
    }
    public void addToCart(HttpSession session, Long foodItemId, int quantity) {
        Cart cart = getCart(session);
        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new IllegalArgumentException("Food item not found"));

        CartItem cartItem = new CartItem();
        cartItem.setFoodId(foodItem.getId());
        cartItem.setFoodName(foodItem.getFoodName());
        cartItem.setPrice(foodItem.getPrice());
        cartItem.setQuantity(quantity);
        cartItem.setImageUrl(foodItem.getMainPicture());  // Copy the image URL

        cart.addItems(cartItem);
        updateCart(session, cart);
    }
    public void updateCart(@NotNull HttpSession session, Cart cart) {
        session.setAttribute(CART_SESSION_KEY, cart);
    }
    public void removeCart(@NotNull HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }
    public int getSumQuantity(@NotNull HttpSession session) {
        return getCart(session).getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    public double getSumPrice(@NotNull HttpSession session) {
        return getCart(session).getCartItems().stream()
                .mapToDouble(item -> item.getPrice() *
                        item.getQuantity())
                .sum();
    }

    public void saveCart(@NotNull HttpSession session, User user, String deliveryAddress) {
        var cart = getCart(session);
        if (cart.getCartItems().isEmpty()) return;

        var order = new Order();
        order.setOrderDate(new Date());
        order.setTotalPrice(getSumPrice(session));
        order.setUser(user);
        order.setDeliveryAddress(deliveryAddress);
        order.setStatus(order.getStatus());
        orderRepository.save(order);

        cart.getCartItems().forEach(item -> {
            var orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setPrice(item.getPrice());
            orderDetail.setFoodItem(foodItemRepository.findById(item.getFoodId()).orElseThrow());
            orderDetailRepository.save(orderDetail);
        });

        removeCart(session);
    }
}
