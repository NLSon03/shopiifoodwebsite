package com.group6b.shopiifoodwebsite.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.ArrayList;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderItems = new ArrayList<>();

    @Column(name = "deliveryAddress", length = 300, nullable = false)
    private String deliveryAddress;

    @Column(name = "totalPrice",nullable = false)
    @Positive(message = "Phải là số dương")
    private double totalPrice;
    @Column(name = "order_date")
    private Date orderDate = new Date();

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private OrderStatus status;

    @Column(name = "last_status_update")
    private Date lastStatusUpdate = new Date();

    public com.group6b.shopiifoodwebsite.constants.OrderStatus getOrderStatus() {
        return com.group6b.shopiifoodwebsite.constants.OrderStatus.fromValue(this.status.getId());
    }
}
