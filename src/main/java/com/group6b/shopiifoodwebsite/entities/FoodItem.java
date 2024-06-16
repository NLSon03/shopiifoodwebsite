package com.group6b.shopiifoodwebsite.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "fooditems")
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "foodname", length = 200, nullable = false)
    private String FoodName;
    @Column(name = "price")
    private double price;
    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "mainPicture")
    private String mainPicture;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

}
