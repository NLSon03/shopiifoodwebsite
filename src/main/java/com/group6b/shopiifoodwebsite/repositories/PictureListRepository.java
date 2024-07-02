package com.group6b.shopiifoodwebsite.repositories;

import com.group6b.shopiifoodwebsite.entities.PictureList;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PictureListRepository extends JpaRepository<PictureList, Long> {

    @Transactional
    void deleteByFoodItemId(Long foodId);
}
