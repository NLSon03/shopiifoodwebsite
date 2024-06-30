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
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "categoryDescription", length = 50, nullable = false)
    private String categoryDescription;

    @Column(name = "category_icon")
    private String categoryIcon;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<FoodItem> foodItems;

}
