package com.ratemydish.dto;

import java.util.List;

public class ProfileWithDishesResponse {
    private Long id;
    private String username;
    private String displayName;
    private String bio;
    private String profilePhotoUrl;
    private List<DishSummaryResponse> dishes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public List<DishSummaryResponse> getDishes() { return dishes; }
    public void setDishes(List<DishSummaryResponse> dishes) { this.dishes = dishes; }
}