package com.group6b.shopiifoodwebsite.repositories;

import com.group6b.shopiifoodwebsite.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant,Long>
{
    Restaurant findByName(String name);
}
