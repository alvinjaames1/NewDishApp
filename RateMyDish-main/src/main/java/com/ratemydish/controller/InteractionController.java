package com.ratemydish.controller;

import com.ratemydish.dto.CommentRequest;
import com.ratemydish.dto.RatingRequest;
import com.ratemydish.service.CommentService;
import com.ratemydish.service.LikeService;
import com.ratemydish.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/posts/{postId}")
public class InteractionController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private CommentService commentService;

    @PostMapping("/likes")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails currentUser) {

        boolean liked = likeService.toggleLike(postId, currentUser);
        long likeCount = likeService.getLikeCount(postId);

        return ResponseEntity.ok(Map.of(
                "liked", liked,
                "likeCount", likeCount
        ));
    }

    @PostMapping("/ratings")
    public ResponseEntity<Map<String, Object>> rateDish(
            @PathVariable Long postId,
            @Valid @RequestBody RatingRequest ratingRequest,
            @AuthenticationPrincipal UserDetails currentUser) {

        Double average = ratingService.rateDish(
                postId,
                ratingRequest.getValue(),
                currentUser
        );

        Integer userRating = ratingService.getUserRating(postId, currentUser);

        return ResponseEntity.ok(Map.of(
                "averageRating", average,
                "userRating", userRating
        ));
    }

    @GetMapping("/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByDish(postId));
    }

    @PostMapping("/comments")
    public ResponseEntity<?> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest commentRequest,
            @AuthenticationPrincipal UserDetails currentUser) {

        return ResponseEntity.ok(
                commentService.addComment(
                        postId,
                        commentRequest.getText(),
                        currentUser
                )
        );
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails currentUser) {

        commentService.deleteComment(commentId, currentUser);

        return ResponseEntity.noContent().build();
    }
}