package com.group6b.shopiifoodwebsite.repositories;

import com.group6b.shopiifoodwebsite.entities.FoodItem;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepository extends
        PagingAndSortingRepository<FoodItem, Long>, JpaRepository<FoodItem, Long> {
    default List<FoodItem> findAllFoods(Integer pageNo,
                                    Integer pageSize,
                                    String sortBy) {
        return findAll(PageRequest.of(pageNo,
                pageSize,
                Sort.by(sortBy)))
                .getContent();
    }

    @Query("""
        SELECT f FROM FoodItem f
        WHERE f.FoodName LIKE %?1%
        OR f.description LIKE %?1%
        OR f.category.categoryDescription LIKE %?1%
        OR f.restaurant.name LIKE %?1%
        """)
    List<FoodItem> searchFood(String keyword);
}
