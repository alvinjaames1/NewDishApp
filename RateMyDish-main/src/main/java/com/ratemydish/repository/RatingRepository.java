package com.ratemydish.repository;

import com.ratemydish.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByDishIdAndUserId(Long dishId, Long userId);

    @Query("SELECT AVG(r.value) FROM Rating r WHERE r.dish.id = :dishId")
    Double findAverageRatingByDishId(Long dishId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.dish.id = :dishId")
    Long countByDishId(Long dishId);
}