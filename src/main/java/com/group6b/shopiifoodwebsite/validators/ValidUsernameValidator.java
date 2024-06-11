package com.group6b.shopiifoodwebsite.validators;


import com.group6b.shopiifoodwebsite.services.UserService;
import com.group6b.shopiifoodwebsite.validators.annotations.ValidUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidUsernameValidator implements
        ConstraintValidator<ValidUsername, String> {
    @Autowired
    private UserService userService;
    @Override
    public boolean isValid(String username, ConstraintValidatorContext
            context) {
        if (userService == null) {
            return true; // Skip validation if the service is not available
        }
        return userService.findByUsername(username).isEmpty();
    }
}