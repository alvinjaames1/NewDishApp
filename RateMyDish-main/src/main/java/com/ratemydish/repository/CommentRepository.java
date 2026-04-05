package com.ratemydish.repository;

import com.ratemydish.entity.Comment;
import com.ratemydish.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByDish(Dish dish);

    List<Comment> findByDishIdAndDeletedFalseOrderByCreatedAtDesc(Long dishId);
}