package com.ratemydish.controller;

import com.ratemydish.dto.UserProfileResponse;
import com.ratemydish.dto.UserProfileUpdateRequest;
import com.ratemydish.service.FollowService;
import com.ratemydish.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final FollowService followService;

    public UserController(UserService userService, FollowService followService) {
        this.userService = userService;
        this.followService = followService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestBody UserProfileUpdateRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {

        return ResponseEntity.ok(
                userService.updateProfile(request, currentUser)
        );
    }

    @PostMapping("/{userId}/follow")
    public ResponseEntity<Void> followUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails currentUser) {

        followService.follow(userId, currentUser);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails currentUser) {

        followService.unfollow(userId, currentUser);
        return ResponseEntity.noContent().build();
    }
}