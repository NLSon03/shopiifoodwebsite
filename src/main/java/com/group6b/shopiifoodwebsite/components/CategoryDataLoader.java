package com.group6b.shopiifoodwebsite.components;

import com.group6b.shopiifoodwebsite.entities.Category;
import com.group6b.shopiifoodwebsite.repositories.CategoryRepository;
import com.group6b.shopiifoodwebsite.services.CategoryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategoryDataLoader implements CommandLineRunner {

    @Autowired
    private CategoryService categoryService;

    @Override
    public void run(String... args) throws Exception {
        categoryService.deleteAllCategories();
        categoryService.resetAutoIncrement();
        String[] descriptions = {
                "Hamburger", "Café", "Trà sữa", "Mì", "Thịt nướng",
                "Pizza", "Kem", "Nước ngọt", "Món chay", "Bánh kem",
                "Khoai tây chiên", "Súp", "Phở", "Bún", "Hải sản"
        };
        String[] icons = {
                "https://i.pinimg.com/736x/59/ba/14/59ba1449e7de374d3a9c6bcdd3974d8b.jpg",//burger
                "https://cdn-icons-png.flaticon.com/512/751/751621.png",//cafe
                "https://cdn-icons-png.flaticon.com/512/3080/3080866.png",//bubble tea
                "https://cdn-icons-png.flaticon.com/512/1046/1046893.png",//mì
                "https://cdn-icons-png.flaticon.com/512/931/931909.png",//thit
                "https://cdn-icons-png.flaticon.com/512/4058/4058619.png",//pizza
                "https://cdn-icons-png.flaticon.com/512/1046/1046885.png",//kem
                "https://cdn-icons-png.flaticon.com/512/7491/7491338.png",//nuoc
                "https://cdn-icons-png.flaticon.com/512/8775/8775411.png",//chay
                "https://png.pngtree.com/png-clipart/20190924/original/pngtree-birthday-cake-icon-for-your-project-png-image_4813613.jpg",//bánh kem
                "https://cdn-icons-png.flaticon.com/512/6468/6468954.png",//khoai
                "https://cdn-icons-png.flaticon.com/512/3823/3823394.png",//sup
                "https://www.shutterstock.com/image-vector/pho-vietnamese-noodle-soup-recipe-260nw-2343909403.jpg",//pho
                "https://png.pngtree.com/element_our/20200610/ourmid/pngtree-fishmeal-vermicelli-image_2239091.jpg",//bun
                "https://cdn-icons-png.flaticon.com/512/4293/4293130.png"//hai san
        };

        for (int i = 0; i < descriptions.length; i++) {
            if (!categoryService.categoryExists(descriptions[i])) {
                Category category = new Category();
                category.setCategoryDescription(descriptions[i]);
                category.setCategoryIcon(icons[i]);
                categoryService.addCategory(category);
            }
        }
    }
}
