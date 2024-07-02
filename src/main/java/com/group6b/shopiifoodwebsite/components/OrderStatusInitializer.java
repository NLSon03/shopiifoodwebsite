package com.group6b.shopiifoodwebsite.components;

import com.group6b.shopiifoodwebsite.entities.OrderStatus;
import com.group6b.shopiifoodwebsite.repositories.OrderStatusRepository;
import com.group6b.shopiifoodwebsite.services.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusInitializer implements CommandLineRunner {
    @Autowired
    private OrderStatusService orderStatusService;

    @Override
    public void run(String... args) throws Exception {
        orderStatusService.initializeStatus();
    }
}
