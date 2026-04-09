package com.ratemydish.controller;

import com.ratemydish.dto.DishRequest;
import com.ratemydish.service.DishService;
import com.ratemydish.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService dishService;
    private final FileStorageService fileStorageService;

    public DishController(DishService dishService, FileStorageService fileStorageService) {
        this.dishService = dishService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<?> createDish(
            @Valid @RequestBody DishRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(
                dishService.createDish(request, request.getImageUrl(), currentUser)
        );
    }

    @GetMapping
    public ResponseEntity<?> getFeed(
            Pageable pageable,
            @RequestParam(name = "feedSort", defaultValue = "latest") String feedSort) {
        return ResponseEntity.ok(dishService.getFeed(pageable, feedSort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDishById(@PathVariable Long id) {
        return ResponseEntity.ok(dishService.getDishById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDish(
            @PathVariable Long id,
            @Valid @RequestBody DishRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(dishService.updateDish(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDish(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        dishService.deleteDish(id, currentUser);
        return ResponseEntity.ok(Map.of("message", "Dish deleted successfully"));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getDishesByUser(@PathVariable String username) {
        return ResponseEntity.ok(dishService.getUserDishes(username));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchDishes(@RequestParam String query) {
        return ResponseEntity.ok(dishService.searchDishes(query));
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDishImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Authentication required"));
        }

        String imageUrl = fileStorageService.storeFile(file);
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }
}