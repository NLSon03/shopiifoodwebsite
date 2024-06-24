package com.group6b.shopiifoodwebsite.services;


import com.group6b.shopiifoodwebsite.entities.*;
import com.group6b.shopiifoodwebsite.repositories.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderStatusScheduler {
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;

    public OrderStatusScheduler(OrderRepository orderRepository, OrderStatusRepository orderStatusRepository) {
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
    }

    @Scheduled(fixedRate = 60000) // Chạy mỗi 60 giây
    public void updateOrderStatuses() {
        var pendingStatus = orderStatusRepository.findByStatusName("PENDING");
        var confirmedStatus = orderStatusRepository.findByStatusName("CONFIRMED");
        var inProgressStatus = orderStatusRepository.findByStatusName("IN_PROGRESS");

        var orders = orderRepository.findAll();
        for (Order order : orders) {
            long elapsedTime = (new Date().getTime() - order.getLastStatusUpdate().getTime()) / 1000; // Thời gian trôi qua tính bằng giây

            if (order.getStatus().equals(pendingStatus) && elapsedTime >= 60) { // Chuyển từ PENDING sang CONFIRMED sau 60 giây
                order.setStatus(confirmedStatus);
                order.setLastStatusUpdate(new Date());
                orderRepository.save(order);
            } else if (order.getStatus().equals(confirmedStatus) && elapsedTime >= 120) { // Chuyển từ CONFIRMED sang IN_PROGRESS sau 120 giây
                order.setStatus(inProgressStatus);
                order.setLastStatusUpdate(new Date());
                orderRepository.save(order);
            }
        }
    }
}
