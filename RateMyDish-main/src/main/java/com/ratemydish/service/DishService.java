package com.ratemydish.service;

import com.ratemydish.dto.DishRequest;
import com.ratemydish.dto.DishResponse;
import com.ratemydish.entity.Dish;
import com.ratemydish.entity.User;
import com.ratemydish.exception.ResourceNotFoundException;
import com.ratemydish.exception.UnauthorizedException;
import com.ratemydish.repository.DishRepository;
import com.ratemydish.repository.LikeRepository;
import com.ratemydish.repository.RatingRepository;
import com.ratemydish.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DishService {

    private final DishRepository dishRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final LikeRepository likeRepository;

    public DishService(DishRepository dishRepository,
                       UserRepository userRepository,
                       RatingRepository ratingRepository,
                       LikeRepository likeRepository) {
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.likeRepository = likeRepository;
    }

    @Transactional
    public DishResponse createDish(DishRequest request, String imageUrl, UserDetails currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Authentication required");
        }

        User author = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Dish dish = new Dish();
        dish.setAuthor(author);
        dish.setTitle(request.getTitle());
        dish.setCuisine(request.getCuisine());
        dish.setDescription(request.getDescription());
        dish.setIngredientsText(request.getIngredientsText());
        dish.setRecipeText(request.getRecipeText());
        dish.setImageUrl(safeImageUrl(imageUrl));
        dish.setDeleted(false);

        Dish saved = dishRepository.save(dish);
        return convertToResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<DishResponse> getFeed(Pageable pageable, String sort) {
        Page<Dish> page = dishRepository.findByDeletedFalseOrderByCreatedAtDesc(pageable);
        return page.map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public List<DishResponse> searchDishes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        List<Dish> dishes = dishRepository.searchAll(keyword.trim());
        return dishes.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DishResponse> getUserDishes(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new ResourceNotFoundException("Username is required");
        }

        List<Dish> dishes = dishRepository.findByAuthorUsernameAndDeletedFalse(username.trim());
        return dishes.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DishResponse getDishById(Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found"));

        if (dish.isDeleted()) {
            throw new ResourceNotFoundException("Dish not found");
        }

        return convertToResponse(dish);
    }

    @Transactional
    public DishResponse updateDish(Long id, DishRequest request, UserDetails currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Authentication required");
        }

        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found"));

        if (dish.isDeleted()) {
            throw new ResourceNotFoundException("Dish not found");
        }

        if (!dish.getAuthor().getUsername().equals(currentUser.getUsername())) {
            throw new UnauthorizedException("You can only edit your own dishes");
        }

        dish.setTitle(request.getTitle());
        dish.setCuisine(request.getCuisine());
        dish.setDescription(request.getDescription());
        dish.setIngredientsText(request.getIngredientsText());
        dish.setRecipeText(request.getRecipeText());
        dish.setImageUrl(safeImageUrl(request.getImageUrl()));
        dish.setUpdatedAt(LocalDateTime.now());

        Dish updated = dishRepository.save(dish);
        return convertToResponse(updated);
    }

    @Transactional
    public void deleteDish(Long id, UserDetails currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Authentication required");
        }

        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found"));

        if (dish.isDeleted()) {
            throw new ResourceNotFoundException("Dish not found");
        }

        if (!dish.getAuthor().getUsername().equals(currentUser.getUsername())) {
            throw new UnauthorizedException("You can only delete your own dishes");
        }

        dish.setDeleted(true);
        dish.setUpdatedAt(LocalDateTime.now());
        dishRepository.save(dish);
    }

    private DishResponse convertToResponse(Dish dish) {
        DishResponse response = new DishResponse();

        response.setId(dish.getId());
        response.setTitle(dish.getTitle());
        response.setCuisine(dish.getCuisine());
        response.setDescription(dish.getDescription());
        response.setIngredientsText(dish.getIngredientsText());
        response.setRecipeText(dish.getRecipeText());
        response.setImageUrl(dish.getImageUrl());
        response.setAuthorUsername(dish.getAuthor() != null ? dish.getAuthor().getUsername() : null);

        Double avg = ratingRepository.findAverageRatingByDishId(dish.getId());
        response.setAverageRating(avg != null ? avg : 0.0);

        Long ratingCount = ratingRepository.countByDishId(dish.getId());
        response.setRatingCount(ratingCount != null ? ratingCount : 0L);

        Long likeCount = likeRepository.countByDishId(dish.getId());
        response.setLikeCount(likeCount != null ? likeCount : 0L);

        return response;
    }

    private String safeImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return "https://via.placeholder.com/400x250?text=No+Image";
        }
        return imageUrl.trim();
    }
}