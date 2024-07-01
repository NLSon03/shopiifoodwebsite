package com.group6b.shopiifoodwebsite.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.format.annotation.NumberFormat;

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

    @Column(name = "price", nullable = false)
    private double price;
    @Column(name = "description", length = 200, nullable = false)
    private String description;

    @Column(name = "mainPicture", nullable = false)
    private String mainPicture;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonManagedReference
    private Category category;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonManagedReference
    private Restaurant restaurant;

    @JsonManagedReference
    @OneToMany(mappedBy = "foodItem", cascade = CascadeType.ALL)
    private List<PictureList> pictures;

    @OneToMany(mappedBy = "foodItem", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<OrderDetail> orderDetails;

}
