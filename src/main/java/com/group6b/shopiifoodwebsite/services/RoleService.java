package com.group6b.shopiifoodwebsite.services;

import com.group6b.shopiifoodwebsite.constants.Role;
import com.group6b.shopiifoodwebsite.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public void initializeRoles() {
        for (Role roleEnum : Role.values()) {
            if (!roleRepository.existsRoleByName(roleEnum.name())) {
                com.group6b.shopiifoodwebsite.entities.Role role = new com.group6b.shopiifoodwebsite.entities.Role();
                role.setName(roleEnum.name());
                roleRepository.save(role);
            }
        }
    }
}