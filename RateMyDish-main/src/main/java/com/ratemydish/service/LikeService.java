package com.ratemydish.service;

import com.ratemydish.entity.Dish;
import com.ratemydish.entity.Like;
import com.ratemydish.entity.User;
import com.ratemydish.exception.ResourceNotFoundException;
import com.ratemydish.exception.UnauthorizedException;
import com.ratemydish.repository.DishRepository;
import com.ratemydish.repository.LikeRepository;
import com.ratemydish.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final DishRepository dishRepository;
    private final UserRepository userRepository;

    public LikeService(LikeRepository likeRepository,
                       DishRepository dishRepository,
                       UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean toggleLike(Long dishId, UserDetails currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Authentication required");
        }

        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found"));

        if (dish.isDeleted()) {
            throw new ResourceNotFoundException("Dish not found");
        }

        return likeRepository.findByDishIdAndUserId(dishId, user.getId())
                .map(existingLike -> {
                    likeRepository.delete(existingLike);
                    return false; // now unliked
                })
                .orElseGet(() -> {
                    Like like = new Like();
                    like.setDish(dish);
                    like.setUser(user);
                    likeRepository.save(like);
                    return true; // now liked
                });
    }

    public long getLikeCount(Long dishId) {
        return likeRepository.countByDishId(dishId);
    }

    @Transactional
    public void likeDish(Long dishId, UserDetails currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Authentication required");
        }

        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found"));

        if (dish.isDeleted()) {
            throw new ResourceNotFoundException("Dish not found");
        }

        if (likeRepository.existsByDishIdAndUserId(dishId, user.getId())) {
            throw new IllegalStateException("You already liked this dish");
        }

        Like like = new Like();
        like.setDish(dish);
        like.setUser(user);
        likeRepository.save(like);
    }

    @Transactional
    public void unlikeDish(Long dishId, UserDetails currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Authentication required");
        }

        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Like like = likeRepository.findByDishIdAndUserId(dishId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Like not found"));

        likeRepository.delete(like);
    }
}