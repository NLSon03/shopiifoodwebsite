package com.group6b.shopiifoodwebsite.repositories;

import com.group6b.shopiifoodwebsite.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long>
{
    Optional<Order> findByIdAndUserId(Long id, Long userId);

    List<Order> findOrderByUserId(Long userId);

    Optional<Order> findOrderById(Long id);

    @Query("SELECT o FROM Order o  WHERE o.restaurant.id = :restaurantId")
    List<Order> findOrdersByRestaurantId(@Param("restaurantId") Long restaurantId);

}
