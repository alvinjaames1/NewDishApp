package com.ratemydish.controller;

import com.ratemydish.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dishes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{dishId}/like")
    public ResponseEntity<?> likeDish(@PathVariable Long dishId,
                                      @AuthenticationPrincipal UserDetails currentUser) {
        likeService.likeDish(dishId, currentUser);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{dishId}/like")
    public ResponseEntity<?> unlikeDish(@PathVariable Long dishId,
                                        @AuthenticationPrincipal UserDetails currentUser) {
        likeService.unlikeDish(dishId, currentUser);
        return ResponseEntity.noContent().build();
    }
}