package com.ratemydish.service;

import com.ratemydish.entity.Follow;
import com.ratemydish.entity.User;
import com.ratemydish.exception.BadRequestException;
import com.ratemydish.exception.ResourceNotFoundException;
import com.ratemydish.exception.UnauthorizedException;
import com.ratemydish.repository.FollowRepository;
import com.ratemydish.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository,
                         UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void follow(Long followeeId, UserDetails currentUser) {
        User follower = getCurrentUser(currentUser);

        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new ResourceNotFoundException("User to follow not found"));

        if (follower.getId().equals(followee.getId())) {
            throw new BadRequestException("You cannot follow yourself");
        }

        if (followRepository.findByFollowerIdAndFolloweeId(
                follower.getId(), followee.getId()).isPresent()) {
            throw new BadRequestException("Already following this user");
        }

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowee(followee);

        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Long followeeId, UserDetails currentUser) {
        User follower = getCurrentUser(currentUser);

        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new ResourceNotFoundException("User to unfollow not found"));

        Follow follow = followRepository.findByFollowerIdAndFolloweeId(
                        follower.getId(), followee.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not following this user"));

        followRepository.delete(follow);
    }

    private User getCurrentUser(UserDetails currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Authentication required");
        }

        return userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}