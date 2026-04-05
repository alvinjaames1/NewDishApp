package com.ratemydish.service;

import com.ratemydish.entity.User;
import com.ratemydish.entity.Dish;
import com.ratemydish.entity.Rating;
import com.ratemydish.exception.BadRequestException;
import com.ratemydish.exception.ResourceNotFoundException;
import com.ratemydish.exception.UnauthorizedException;
import com.ratemydish.repository.RatingRepository;
import com.ratemydish.repository.DishRepository;
import com.ratemydish.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final DishRepository dishRepository;
    private final UserRepository userRepository;

    public RatingService(RatingRepository ratingRepository,
                         DishRepository dishRepository,
                         UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Double rateDish(Long dishId, Integer value, UserDetails currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Authentication required");
        }

        validateRating(value);

        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found"));

        if (dish.isDeleted()) {
            throw new ResourceNotFoundException("Dish not found");
        }

        Rating rating = ratingRepository.findByDishIdAndUserId(dishId, user.getId())
                .orElse(new Rating());

        rating.setDish(dish);
        rating.setUser(user);
        rating.setValue(value);
        rating.setUpdatedAt(LocalDateTime.now());

        ratingRepository.save(rating);

        Double avg = ratingRepository.findAverageRatingByDishId(dishId);
        return avg != null ? avg : 0.0;
    }

    @Transactional(readOnly = true)
    public Integer getUserRating(Long dishId, UserDetails currentUser) {
        if (currentUser == null) {
            return null;
        }

        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElse(null);

        if (user == null) {
            return null;
        }

        return ratingRepository.findByDishIdAndUserId(dishId, user.getId())
                .map(Rating::getValue)
                .orElse(null);
    }

    private void validateRating(Integer value) {
        if (value == null || value < 1 || value > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }
    }
}