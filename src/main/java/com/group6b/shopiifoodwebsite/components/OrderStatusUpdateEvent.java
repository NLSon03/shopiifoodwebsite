package com.group6b.shopiifoodwebsite.components;

import com.group6b.shopiifoodwebsite.entities.Order;
import org.springframework.context.ApplicationEvent;

public class OrderStatusUpdateEvent extends ApplicationEvent {
    private final Order order;

    public OrderStatusUpdateEvent(Order order) {
        super(order);
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}
