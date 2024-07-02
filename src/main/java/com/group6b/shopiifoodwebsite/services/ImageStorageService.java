package com.group6b.shopiifoodwebsite.services;

import com.group6b.shopiifoodwebsite.repositories.PictureListRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageStorageService {
    private static final String IMAGE_DIR = "src/main/resources/static/images/foods/";
    private final PictureListRepository pictureRepository;
    private static final String CATEGORIES_IMAGE_DIR = "src/main/resources/static/images/categories/";

    public ImageStorageService(PictureListRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    public static String generateRandomFileName(String originalFilename) {
        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i >= 0) {
            extension = originalFilename.substring(i);
        }
        return UUID.randomUUID().toString() + extension;
    }

    public String saveImage(MultipartFile image, Long foodId) throws IOException {
        if (image == null || foodId == null) {
            throw new IllegalArgumentException("Image or food ID must not be null");
        }

        String folderPath = IMAGE_DIR + foodId;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String randomFileName = generateRandomFileName(Objects.requireNonNull(image.getOriginalFilename()));
        Path filePath = Paths.get(folderPath, randomFileName);

        Files.copy(image.getInputStream(), filePath);

        return "/images/foods/" + foodId + "/" + randomFileName;
    }

    public String saveIconCategory(MultipartFile image,Long categoryID) throws IOException{
        String folderPath = CATEGORIES_IMAGE_DIR + categoryID;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String randomFileName = generateRandomFileName(Objects.requireNonNull(image.getOriginalFilename()));
        Path filePath = Paths.get(folderPath, randomFileName);
        Files.copy(image.getInputStream(), filePath);
        return "/images/categories/" + categoryID + "/" + randomFileName;
    }

    public void deleteAllIconCategory(Long categoryId){
        String folderPath = CATEGORIES_IMAGE_DIR + categoryId;
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    // Log the exception and continue
                    e.printStackTrace();
                }
            }
        }
        // Xóa dữ liệu hình ảnh trong DB
        pictureRepository.deleteByFoodItemId(categoryId);
    }

    public void deleteAllImages(Long foodId) {
        String folderPath = IMAGE_DIR + foodId;
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    // Log the exception and continue
                    e.printStackTrace();
                }
            }
        }
        // Xóa dữ liệu hình ảnh trong DB
        pictureRepository.deleteByFoodItemId(foodId);
    }

}
