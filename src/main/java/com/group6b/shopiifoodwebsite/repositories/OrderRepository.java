package com.group6b.shopiifoodwebsite.repositories;

import com.group6b.shopiifoodwebsite.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface OrderRepository extends JpaRepository<Order,Long>
{
    Optional<Order> findByIdAndUserId(Long id, Long userId);

    List<Order> findOrderByUserId(Long userId);

    Optional<Order> findOrderById(Long id);
}
