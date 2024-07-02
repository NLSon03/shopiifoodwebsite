package com.group6b.shopiifoodwebsite.services;


import com.group6b.shopiifoodwebsite.constants.OrderStatus;
import com.group6b.shopiifoodwebsite.constants.Role;
import com.group6b.shopiifoodwebsite.repositories.OrderStatusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusService {

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    public void initializeStatus() {
        for (OrderStatus statusEnum : OrderStatus.values()) {
            if (!orderStatusRepository.existsByStatusName(statusEnum.name())) {
                com.group6b.shopiifoodwebsite.entities.OrderStatus  status = new com.group6b.shopiifoodwebsite.entities.OrderStatus();
                status.setStatusName(statusEnum.name());
                orderStatusRepository.save(status);
            }
        }
    }
}
