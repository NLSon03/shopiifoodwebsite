package com.group6b.shopiifoodwebsite.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 250,min = 4, message = "Tên phải có ít nhất 4 ký tự")
    @Column(name = "name", length = 250,nullable = false)
    private String name;
    @Size(max = 250,min = 10, message = "Địa chỉ phải có ít nhất 20 ký tự")
    @Column(name = "address", length = 250,nullable = false)
    private String address;
    @Length(min = 10, max = 10, message = "Số điện thoại phải có 10 ký tự")
    @Pattern(regexp = "^[0-9]*$", message = "Số điện thoại phải là số")
    @Column(name = "phoneNumber",nullable = false)
    private String phoneNumber;

    @Column(name = "restaurantpicture",nullable = false)
    private String RestaurantPicture;

    
    @OneToMany(mappedBy = "restaurant")
    @JsonIgnore
    private List<FoodItem> foodItems;

    @OneToMany(mappedBy = "restaurant")
    @JsonIgnore
    private List<Order> orders;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

}
