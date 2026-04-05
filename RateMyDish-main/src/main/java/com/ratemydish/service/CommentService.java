package com.ratemydish.service;

import com.ratemydish.dto.CommentResponse;
import com.ratemydish.entity.Comment;
import com.ratemydish.entity.Dish;
import com.ratemydish.entity.User;
import com.ratemydish.exception.ResourceNotFoundException;
import com.ratemydish.exception.UnauthorizedException;
import com.ratemydish.repository.CommentRepository;
import com.ratemydish.repository.DishRepository;
import com.ratemydish.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final DishRepository dishRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          DishRepository dishRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
    }

    public List<CommentResponse> getCommentsByDish(Long dishId) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found"));

        if (dish.isDeleted()) {
            throw new ResourceNotFoundException("Dish not found");
        }

        return commentRepository.findByDishIdAndDeletedFalseOrderByCreatedAtDesc(dishId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public CommentResponse addComment(Long dishId, String text, UserDetails currentUser) {
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

        Comment comment = new Comment();
        comment.setDish(dish);
        comment.setUser(user);
        comment.setText(text);

        Comment saved = commentRepository.save(comment);

        return convertToResponse(saved);
    }

    @Transactional
    public void deleteComment(Long commentId, UserDetails currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Authentication required");
        }

        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (comment.isDeleted()) {
            throw new ResourceNotFoundException("Comment not found");
        }

        if (!comment.getUser().getUsername().equals(user.getUsername())) {
            throw new UnauthorizedException("You can only delete your own comments");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    private CommentResponse convertToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getUser() != null ? comment.getUser().getUsername() : "User",
                comment.getText()
        );
    }
}