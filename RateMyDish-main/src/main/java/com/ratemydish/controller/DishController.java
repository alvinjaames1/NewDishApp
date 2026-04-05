package com.ratemydish.controller;

import com.ratemydish.dto.DishRequest;
import com.ratemydish.service.DishService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @PostMapping
    public ResponseEntity<?> createDish(
            @Valid @RequestBody DishRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {

        return ResponseEntity.ok(dishService.createDish(request, request.getImageUrl(), currentUser));
    }

    @GetMapping
    public ResponseEntity<?> getFeed(
            Pageable pageable,
            @RequestParam(defaultValue = "latest") String sort) {

        return ResponseEntity.ok(dishService.getFeed(pageable, sort));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchDishes(@RequestParam String keyword) {
        return ResponseEntity.ok(dishService.searchDishes(keyword));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserDishes(@PathVariable String username) {
        return ResponseEntity.ok(dishService.getUserDishes(username));
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
        return ResponseEntity.noContent().build();
    }
}