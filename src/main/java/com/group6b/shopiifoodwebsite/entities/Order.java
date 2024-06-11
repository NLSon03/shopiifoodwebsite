package com.group6b.shopiifoodwebsite.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderItems;

    @Column(name = "deliveryAddress", length = 300, nullable = false)
    private String deliveryAddress;

    @Column(name = "totalPrice",nullable = false)
    private double totalPrice;
    @Column(name = "order_date")
    private Date orderDate = new Date();

    private String status;
}
