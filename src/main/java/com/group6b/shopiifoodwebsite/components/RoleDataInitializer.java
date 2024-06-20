package com.group6b.shopiifoodwebsite.components;

import com.group6b.shopiifoodwebsite.repositories.RoleRepository;
import com.group6b.shopiifoodwebsite.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleDataInitializer implements CommandLineRunner {
    @Autowired
    private RoleService roleService;
    @Override
    public void run(String... args) throws Exception {
        roleService.initializeRoles();
    }
}
