package com.group6b.shopiifoodwebsite.services;

import com.group6b.shopiifoodwebsite.entities.Category;
import com.group6b.shopiifoodwebsite.repositories.CategoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE,
        rollbackFor = {Exception.class, Throwable.class})
public class CategoryService {
    private final CategoryRepository categoryRepository;
    @PersistenceContext
    private EntityManager em;
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
    public void updateCategory(@NotNull Category category, MultipartFile icon) throws IOException{
        Category existingCategory = categoryRepository.findById(category.getId())
                .orElse(null);
        Objects.requireNonNull(existingCategory).setCategoryDescription(category.getCategoryDescription());
        existingCategory.setCategoryDescription(category.getCategoryDescription());
        if (icon != null && !icon.isEmpty()) {
            String imageSavePath = "src/main/resources/static/categoryicons/";  // Replace with your actual path
            String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(icon.getOriginalFilename());
            Path imagePath = Paths.get(imageSavePath + fileName);
            icon.transferTo(imagePath);
            existingCategory.setCategoryIcon("/categoryicons/" + fileName);
        }
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
    public List<Category> searchCategories(String query) {
        return categoryRepository.findByCategoryDescriptionContainingIgnoreCase(query);
    }

    public boolean categoryExists(String description) {
        return categoryRepository.existsByCategoryDescription(description);
    }
    public boolean categoryIconExists(String icon) {
        return categoryRepository.existsByCategoryIcon(icon);
    }

    public void deleteAllCategories() {
        categoryRepository.deleteAll();
    }
    @Transactional
    public void resetAutoIncrement() {
        em.createNativeQuery("ALTER TABLE category AUTO_INCREMENT = 1").executeUpdate();
    }
}
