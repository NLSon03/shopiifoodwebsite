package com.group6b.shopiifoodwebsite.validators;


import com.group6b.shopiifoodwebsite.entities.Category;
import com.group6b.shopiifoodwebsite.validators.annotations.ValidCategoryId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class ValidCategoryIdValidator  implements ConstraintValidator<ValidCategoryId, Category> {
    @Override
    public boolean isValid(Category category,
                           ConstraintValidatorContext context) {
        return category != null && category.getId() != null;
    }
}
