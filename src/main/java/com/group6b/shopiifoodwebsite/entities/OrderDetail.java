package com.group6b.shopiifoodwebsite.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
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
    private Double price;

    public OrderDetail(Order order, FoodItem foodName, int quantity, Double price) {
        this.order = order;
        this.foodItem = foodName;
        this.quantity = quantity;
        this.price = price;
    }

}
