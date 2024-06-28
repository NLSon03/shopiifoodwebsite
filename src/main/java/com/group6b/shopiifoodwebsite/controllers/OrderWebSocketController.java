package com.group6b.shopiifoodwebsite.controllers;

import com.group6b.shopiifoodwebsite.components.OrderStatusUpdateEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class OrderWebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    public OrderWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleOrderStatusUpdate(OrderStatusUpdateEvent event) {
        messagingTemplate.convertAndSend("/topic/orders", event.getOrder());
    }
}
