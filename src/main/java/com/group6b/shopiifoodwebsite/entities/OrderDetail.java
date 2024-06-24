package com.group6b.shopiifoodwebsite.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@Entity
@Data
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "food_item_id")
    private FoodItem foodItem;

    @Column(name = "quantity",nullable = false)
    private int quantity;
    @Column(name = "price",nullable = false)
    private double price;

    public OrderDetail(Long id, Order order, FoodItem foodItem, int quantity, double price) {
        this.id = id;
        this.order = order;
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderDetail(Long foodId, String foodName, int quantity, Double price) {
    }
}
