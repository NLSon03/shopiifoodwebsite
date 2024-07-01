package com.group6b.shopiifoodwebsite.repositories;

import com.group6b.shopiifoodwebsite.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository  extends        JpaRepository<Category, Long> {
    default List<Category> findAllCategories() {
        return findAll();
    }
    List<Category> findByCategoryDescriptionContainingIgnoreCase(String description);
}