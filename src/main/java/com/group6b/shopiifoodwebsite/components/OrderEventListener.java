package com.group6b.shopiifoodwebsite.components;

import com.group6b.shopiifoodwebsite.entities.Order;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderEventListener {
    private final ApplicationEventPublisher eventPublisher;

    public OrderEventListener(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @TransactionalEventListener
    public void handleOrderStatusUpdate(Order order) {
        eventPublisher.publishEvent(new OrderStatusUpdateEvent(order));
    }
}