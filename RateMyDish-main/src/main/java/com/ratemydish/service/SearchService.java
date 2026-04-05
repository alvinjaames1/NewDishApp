package com.ratemydish.service;

import com.ratemydish.dto.DishResponse;
import com.ratemydish.dto.UserProfileResponse;
import com.ratemydish.entity.Dish;
import com.ratemydish.entity.User;
import com.ratemydish.repository.DishRepository;
import com.ratemydish.repository.LikeRepository;
import com.ratemydish.repository.RatingRepository;
import com.ratemydish.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final DishRepository dishRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final LikeRepository likeRepository;

    public SearchService(DishRepository dishRepository,
                         UserRepository userRepository,
                         RatingRepository ratingRepository,
                         LikeRepository likeRepository) {
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.likeRepository = likeRepository;
    }

    public Page<DishResponse> searchDishes(String keyword, String cuisine, Pageable pageable) {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        String safeCuisine = cuisine == null ? "" : cuisine.trim();

        if (!safeCuisine.isEmpty()) {
            return dishRepository
                    .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndCuisineIgnoreCase(
                            safeKeyword, safeKeyword, safeCuisine, pageable)
                    .map(this::convertToDishResponse);
        }

        return dishRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        safeKeyword, safeKeyword, pageable)
                .map(this::convertToDishResponse);
    }

    public Page<UserProfileResponse> searchUsers(String keyword, Pageable pageable) {
        String safeKeyword = keyword == null ? "" : keyword.trim();

        return userRepository
                .findByUsernameContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(
                        safeKeyword, safeKeyword, pageable)
                .map(this::convertToUserResponse);
    }

    private DishResponse convertToDishResponse(Dish dish) {
        DishResponse dto = new DishResponse();

        dto.setId(dish.getId());
        dto.setTitle(dish.getTitle());
        dto.setDescription(dish.getDescription());
        dto.setCuisine(dish.getCuisine());
        dto.setIngredientsText(dish.getIngredientsText());
        dto.setRecipeText(dish.getRecipeText());
        dto.setImageUrl(dish.getImageUrl());
        dto.setAuthorUsername(dish.getAuthor() != null ? dish.getAuthor().getUsername() : null);

        Double avg = ratingRepository.findAverageRatingByDishId(dish.getId());
        dto.setAverageRating(avg != null ? avg : 0.0);

        Long ratingCount = ratingRepository.countByDishId(dish.getId());
        dto.setRatingCount(ratingCount != null ? ratingCount : 0L);

        Long likeCount = likeRepository.countByDishId(dish.getId());
        dto.setLikeCount(likeCount != null ? likeCount : 0L);

        return dto;
    }

    private UserProfileResponse convertToUserResponse(User user) {
        UserProfileResponse dto = new UserProfileResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName());
        return dto;
    }
}