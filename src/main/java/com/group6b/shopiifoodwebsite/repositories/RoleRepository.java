package com.group6b.shopiifoodwebsite.repositories;

import com.group6b.shopiifoodwebsite.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleById(Long id);
    boolean existsRoleByName(String name);
}

