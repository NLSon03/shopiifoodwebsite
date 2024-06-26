package com.group6b.shopiifoodwebsite.services;


import com.group6b.shopiifoodwebsite.entities.Order;
import com.group6b.shopiifoodwebsite.entities.OrderStatus;
import com.group6b.shopiifoodwebsite.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;


import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE,
        rollbackFor = {Exception.class, Throwable.class})

public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;

    public boolean cancelOrder(Long orderId, Long userId) {
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
        return true;
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
        var confirmed = orderStatusRepository.findByStatusName("PENDING");
        order.setStatus(confirmed);
        order.setLastStatusUpdate(new Date());
        orderRepository.save(order);
        updateOrderStatuses();
    }
    public List<Order> findByUserId(Long orderId) {
        return orderRepository.findOrderByUserId(orderId);
    }
    public Optional<Order> findOrdersById(Long id){
        return  orderRepository.findOrderById(id);
    }


    @Transactional
    public boolean acceptOrder(Long orderId, Long restaurantId) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        var confirmedStatus = orderStatusRepository.findByStatusName("CONFIRMED");

        if (!order.getRestaurant().getId().equals(restaurantId)) {
            throw new IllegalArgumentException("Restaurant not authorized to accept this order");
        }

        order.setStatus(confirmedStatus);
        order.setLastStatusUpdate(new Date());
        orderRepository.save(order);

        return true;
    }
    @Scheduled(fixedRate = 60000) // Chạy mỗi 60 giây
    public void updateOrderStatuses() {
        var pendingStatus = orderStatusRepository.findByStatusName("PENDING");
        var confirmedStatus = orderStatusRepository.findByStatusName("CONFIRMED");
        var inProgressStatus = orderStatusRepository.findByStatusName("IN_PROGRESS");

        var orders = orderRepository.findAll();
        for (Order order : orders) {
            long elapsedTime = (new Date().getTime() - order.getLastStatusUpdate().getTime()) / 1000; // Thời gian trôi qua tính bằng giây

            if (order.getStatus().equals(pendingStatus) && elapsedTime >= 120) { // Chuyển từ PENDING sang CONFIRMED sau 60 giây
                order.setStatus(confirmedStatus);
                order.setLastStatusUpdate(new Date());
                orderRepository.save(order);
            } else if (order.getStatus().equals(confirmedStatus) && elapsedTime >= 240) { // Chuyển từ CONFIRMED sang IN_PROGRESS sau 120 giây
                order.setStatus(inProgressStatus);
                order.setLastStatusUpdate(new Date());
                orderRepository.save(order);
            }
        }
    }
}
