package com.group6b.shopiifoodwebsite.services;

import com.group6b.shopiifoodwebsite.entities.Category;
import com.group6b.shopiifoodwebsite.repositories.CategoryRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE,
        rollbackFor = {Exception.class, Throwable.class})
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final String IMAGE_PATH = "src/main/resources/static/categoryimages/";

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public void addCategory(Category category) throws IOException {
        categoryRepository.save(category);
    }

    public void updateCategory(@NotNull Category category) {
        Category existingCategory = categoryRepository.findById(category.getId())
                .orElse(null);
        Objects.requireNonNull(existingCategory).setCategoryDescription(category.getCategoryDescription());
        existingCategory.setCategoryDescription(category.getCategoryDescription());

        categoryRepository.save(existingCategory);
    }

    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }

    private String saveImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }
        byte[] bytes = image.getBytes();
        Path path = Paths.get(IMAGE_PATH + image.getOriginalFilename());
        Files.write(path, bytes);
        return "/categoryimages/" + image.getOriginalFilename();
    }
}
