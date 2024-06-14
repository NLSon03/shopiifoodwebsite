package com.group6b.shopiifoodwebsite.entities;


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
@Table(name = "FoodPicture")
public class FoodPicture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url; // URL or path to the picture

    @ManyToOne
    @JoinColumn(name = "food_item_id")
    private FoodItem foodItem;
}
