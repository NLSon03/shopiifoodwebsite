package com.group6b.shopiifoodwebsite.services;


import com.group6b.shopiifoodwebsite.entities.Order;
import com.group6b.shopiifoodwebsite.entities.OrderStatus;
import com.group6b.shopiifoodwebsite.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
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
    private final TaskScheduler taskScheduler;

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
        var pendingStatus = orderStatusRepository.findByStatusName("PENDING");
        order.setStatus(pendingStatus);
        order.setLastStatusUpdate(new Date());
        orderRepository.save(order);
        scheduleOrderStatusUpdate(order);
    }

    public List<Order> findByUserId(Long orderId) {
        return orderRepository.findOrderByUserId(orderId);
    }

    public Optional<Order> findOrdersById(Long id) {
        return orderRepository.findOrderById(id);
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

        // Schedule IN_PROGRESS status update
        taskScheduler.schedule(() -> updateOrderStatuses(orderId, "IN_PROGRESS"), new Date(System.currentTimeMillis() + 15000));

        return true;
    }
    private void scheduleOrderStatusUpdate(Order order) {
        long currentTime = System.currentTimeMillis();

        taskScheduler.schedule(() -> updateOrderStatuses(order.getId(), "CONFIRMED"), new Date(currentTime + 45000));
        taskScheduler.schedule(() -> updateOrderStatuses(order.getId(), "IN_PROGRESS"), new Date(currentTime + 75000));
        taskScheduler.schedule(() -> enableUserCompleteOrder(order.getId()), new Date(currentTime + 135000));
    }
    private void enableUserCompleteOrder(Long orderId) {
        // Logic to enable the 'Complete Order' button for the user
        // You might set a flag or update a status to indicate that the user can now mark the order as completed
    }
    @Transactional
    public void updateOrderStatuses(Long orderId, String statusName) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        var status = orderStatusRepository.findByStatusName(statusName);
        order.setStatus(status);
        order.setLastStatusUpdate(new Date());
        orderRepository.save(order);
    }
}
