package com.group6b.shopiifoodwebsite.services;


import com.group6b.shopiifoodwebsite.entities.Order;
import com.group6b.shopiifoodwebsite.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE,
        rollbackFor = {Exception.class, Throwable.class})

public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;

    public void cancelOrder(Long orderId, Long userId) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        var cancelledStatus = orderStatusRepository.findByStatusName("CANCELLED");
        var inProgressStatus = orderStatusRepository.findByStatusName("IN_PROGRESS");

        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User not authorized to cancel this order");
        }

        if (order.getStatus().equals(inProgressStatus)) {
            throw new IllegalArgumentException("Cannot cancel order in progress");
        }

        order.setStatus(cancelledStatus);
        order.setLastStatusUpdate(new Date());
        orderRepository.save(order);
    }

    public void completeOrder(Long orderId, Long userId) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        var completedStatus = orderStatusRepository.findByStatusName("COMPLETED");

        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User not authorized to complete this order");
        }

        order.setStatus(completedStatus);
        order.setLastStatusUpdate(new Date());
        orderRepository.save(order);
    }
    @Transactional(readOnly = true)
    public Optional<Order> findByIdAndUserId(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId);
    }
    public void save(Order order) {
        orderRepository.save(order);
    }
    public void createOrder(Order order) {
        var pendingStatus = orderStatusRepository.findByStatusName("PENDING");
        order.setStatus(pendingStatus);
        order.setLastStatusUpdate(new Date());
        orderRepository.save(order);
    }
}
