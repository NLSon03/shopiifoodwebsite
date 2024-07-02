package com.group6b.shopiifoodwebsite.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonIgnore
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
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
