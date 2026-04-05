package com.ratemydish.repository;

import com.ratemydish.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByDishIdAndUserId(Long dishId, Long userId);

    boolean existsByDishIdAndUserId(Long dishId, Long userId);

    long countByDishId(Long dishId);
}