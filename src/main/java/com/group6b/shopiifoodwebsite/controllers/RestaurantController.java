package com.group6b.shopiifoodwebsite.controllers;

import com.group6b.shopiifoodwebsite.entities.FoodItem;
import com.group6b.shopiifoodwebsite.entities.Order;
import com.group6b.shopiifoodwebsite.entities.PictureList;
import com.group6b.shopiifoodwebsite.entities.Restaurant;
import com.group6b.shopiifoodwebsite.repositories.FoodItemRepository;
import com.group6b.shopiifoodwebsite.services.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final FoodItemService foodItemService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final FoodItemRepository foodItemRepository;
    private final ImageStorageService imageStorageService;
    private final SellerService sellerService;

    @GetMapping
    public String showAllRestaurant(@NotNull Model model) {
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "restaurant/list-of-restaurants";
    }

    private String saveImageStatic(MultipartFile image) throws IOException {
        File saveFile = new ClassPathResource("static/restaurantimages").getFile();
        String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
        Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
        Files.copy(image.getInputStream(), path);
        return fileName;
    }

    @GetMapping("/add")
    public String showAddForm(@NotNull Model model) {
        model.addAttribute("restaurant", new Restaurant());
        return "restaurant/add";
    }

    // Create a new food item
    @PostMapping("/add")
    public String addRestaurant(@Valid @ModelAttribute Restaurant restaurant,
                                @RequestParam("image") MultipartFile restaurantPicture,
                                BindingResult result) throws IOException {
        if (result.hasErrors()) {
            return "restaurant/add";
        }
        if (!restaurantPicture.isEmpty()) {
            try {
                String imageName = saveImageStatic(restaurantPicture);
                restaurant.setRestaurantPicture("/restaurantimages/" + imageName);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        restaurantService.addRestaurant(restaurant);
        return "redirect:/restaurants";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateRestaurantForm(@PathVariable long id, @NotNull Model model) {
        var restaurant = restaurantService.getRestaurantById(id);
        model.addAttribute("restaurant", restaurant.orElseThrow(() -> new
                IllegalArgumentException("Food not found")));
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "restaurant/edit";
    }

    @GetMapping("/details/{id}")
    public String viewRestaurantDetails(@PathVariable long id, Model model) {
        var restaurant = restaurantService.getRestaurantById(id).orElseThrow(() -> new IllegalArgumentException("Food not found"));
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("foodByRestaurant", foodItemService.getFoodItemsByRestaurantId(id));
        return "restaurant/details";
    }

    // Update an existing food item
    @PostMapping("/edit")
    public String updateRestaurantInfo(
            @Valid @ModelAttribute("restaurant") Restaurant restaurant,
            @NotNull BindingResult result, Model model,
            @RequestParam("image") MultipartFile mainPicture) throws IOException {
        if (result.hasErrors()) {
            var errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
            model.addAttribute("errors", errors);

            model.addAttribute("restaurants", restaurantService.getAllRestaurants());
            return "restaurant/edit";
        }

        restaurantService.updateRestaurant(restaurant, mainPicture);
        return "redirect:/restaurants";
    }

    // Delete a food item
    @GetMapping("/delete/{id}")
    public String deleteRestaurant(@PathVariable Long id) {
        restaurantService.getRestaurantById(id).ifPresentOrElse(restaurant -> restaurantService.deleteRestaurantById(id), () -> {
            throw new IllegalArgumentException("Food not found");
        });
        return "redirect:/restaurants";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Thêm bất kỳ logic nào bạn cần ở đây, ví dụ: lấy dữ liệu cần thiết để hiển thị trên dashboard
        return "sellerDashboard/dashboard";
    }

    @GetMapping("/dashboard/list-foods")
    public String getFoodItemsByRestaurantId(@NotNull Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long restaurantId = userService.getRestaurantByUsername(username).getId();
        model.addAttribute("foodsByRestaurant", foodItemService.getFoodItemsByRestaurantId(restaurantId));
        return "sellerDashboard/list-foods";
    }

    @GetMapping("/dashboard/statistical")
    public String showStatistical(Model model) {
        return "sellerDashboard/statistical";
    }

    @GetMapping("/dashboard/order-lists")
    public String getOrderLists(@NotNull Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long restaurantId = userService.getRestaurantByUsername(username).getId();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Restaurant not authenticated");
        }

        List<Order> orders = orderService.getOrdersByRestaurant(restaurantId);
        model.addAttribute("orderLists", orders);
        return "sellerDashboard/order-list";
    }


    @GetMapping("/dashboard/order-lists/confirm/{id}")
    public String confirmOrder(@PathVariable Long id) {
        orderService.confirmOrder(id);
        return "redirect:/restaurants/dashboard/order-lists";  // Cần chỉnh {restaurantId} thành id của nhà hàng hiện tại
    }

    @GetMapping("/dashboard/order-lists/accept/{id}")
    public String acceptOrder(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Restaurant not authenticated");
        }
        String username = authentication.getName();
        Restaurant restaurant = restaurantService.getRestaurantByName(username).orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        try {
            boolean accepted = orderService.acceptOrder(id, restaurant.getId());
            if (accepted) {
                redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã được chấp nhận thành công.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể chấp nhận đơn hàng do trạng thái không hợp lệ.");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/restaurants/dashboard/order-lists";
    }

    @GetMapping("/dashboard/add-food-item")
    public String showAddFoodItemForm(Model model) {
        model.addAttribute("food", new FoodItem());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "sellerDashboard/create";
    }

    /*
        @PostMapping("/dashboard/add-food-item")
        public String addFoodItem(@Valid @ModelAttribute FoodItem foodItem,
                                  @RequestParam("image") MultipartFile image,
                                  @RequestParam("images") List<MultipartFile> pictures,Model model,
                                  BindingResult result)  throws  IOException{
            // Lấy thông tin người dùng hiện tại
            if (result.hasErrors()) {
                var errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
                model.addAttribute("errors", errors);
                return "sellerDashboard/create";
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            // Lấy thông tin nhà hàng của người dùng hiện tại
            Restaurant restaurant = userService.getRestaurantByUsername(username);
            // Liên kết FoodItem với Restaurant
            foodItem.setRestaurant(restaurant);
            // Lưu FoodItem

            if (!image.isEmpty()) {
                try {
                    String imageSavePath = "src/main/resources/static/foodimages/";  // Replace with your actual path
                    String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
                    Path imagePath = Paths.get(imageSavePath + fileName);
                    image.transferTo(imagePath);
                    foodItem.setMainPicture("/foodimages/" + fileName);  // Adjust the path based on your storage location
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // Handle any exceptions during image saving
                    return "redirect:/restaurants/dashboard/add-food-item";  // Redirect back to the add form with error handling
                }
            }

            List<PictureList> pictureList = new ArrayList<>();
            if (pictures != null && !pictures.isEmpty()) {
                for (MultipartFile picture : pictures) {
                    if (!picture.isEmpty()) {
                        try {
                            String imageSavePath = "src/main/resources/static/foodimages/";
                            String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(picture.getOriginalFilename());
                            Path imagePath = Paths.get(imageSavePath + fileName);
                            picture.transferTo(imagePath);

                            PictureList newPicture = new PictureList();
                            newPicture.setFoodItem(foodItem);
                            newPicture.setUrl("/foodimages/" + fileName);
                            pictureList.add(newPicture);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            // Xử lý ngoại lệ khi lưu ảnh
                            return "redirect:/restaurants/dashboard/add-food-item";  // Chuyển hướng lại form thêm với thông báo lỗi
                        }
                    }
                }
            }
            foodItem.setPictures(pictureList);
            foodItemService.addFood(foodItem);
            return "redirect:/restaurants/dashboard/list-foods";
        }
    */
    @GetMapping("/dashboard/list-foods/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, @NotNull Model model) {
        var foodItem = foodItemService.getFoodById(id);
        model.addAttribute("food", foodItem.orElseThrow(() -> new
                IllegalArgumentException("Food not found")));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "sellerDashboard/edit";
    }

    @PostMapping("/dashboard/list-foods/edit")
    public String updateFoodItem(
            @Valid @ModelAttribute("food") FoodItem foodItem,
            @NotNull BindingResult result, Model model,
            @RequestParam("image") MultipartFile mainPicture,
            @RequestParam("images") List<MultipartFile> pictures) throws IOException {

        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            return "sellerDashboard/edit";
        }

        FoodItem existingFood = foodItemService.getFoodById(foodItem.getId())
                .orElseThrow(() -> new IllegalArgumentException("Food with id " + foodItem.getId() + " not found."));

        // Cập nhật các trường khác
        existingFood.setFoodName(foodItem.getFoodName());
        existingFood.setDescription(foodItem.getDescription());
        existingFood.setPrice(foodItem.getPrice());
        existingFood.setCategory(foodItem.getCategory());

        // Xóa ảnh cũ và thêm ảnh mới
        imageStorageService.deleteAllImages(existingFood.getId());
        String mainImageUrl = imageStorageService.saveImage(mainPicture, existingFood.getId());
        existingFood.setMainPicture(mainImageUrl);

        if (pictures != null && !pictures.isEmpty()) {
            List<PictureList> pictureList = new ArrayList<>();
            for (MultipartFile picture : pictures) {
                if (!picture.isEmpty()) {
                    String imageUrl = imageStorageService.saveImage(picture, existingFood.getId());
                    PictureList newPicture = new PictureList();
                    newPicture.setUrl(imageUrl);
                    newPicture.setFoodItem(existingFood);
                    pictureList.add(newPicture);
                }
            }
            existingFood.setPictures(pictureList);
        }

        foodItemService.save(existingFood);
        return "redirect:/restaurants/dashboard/list-foods";
    }


    // Delete a food item
    @GetMapping("/dashboard/list-foods/delete/{id}")
    public String deleteFoodItem(@PathVariable Long id) {
        foodItemService.getFoodById(id).ifPresentOrElse(book -> foodItemService.deleteFoodById(id), () -> {
            throw new IllegalArgumentException("Food not found");
        });
        return "redirect:/restaurants/dashboard/list-foods";
    }

    @GetMapping("/dashboard/list-foods/details/{id}")
    public String viewFoodItemDetails(@PathVariable long id, Model model) {
        var foodItem = foodItemService.getFoodById(id).orElseThrow(() -> new IllegalArgumentException("Food not found"));
        model.addAttribute("foodItem", foodItem);
        return "sellerDashboard/details";
    }

    @GetMapping("/dashboard/orders/{id}")
    public String viewOrderDetail(@PathVariable Long id){
        return "sellerDashboard/order-detail";
    }
}
