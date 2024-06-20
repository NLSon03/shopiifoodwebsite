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
@Table(name = "PictureList")
public class PictureList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url; // URL or path to the picture

    @ManyToOne
    @JoinColumn(name = "fooditems_id")
    private FoodItem foodItem;
}