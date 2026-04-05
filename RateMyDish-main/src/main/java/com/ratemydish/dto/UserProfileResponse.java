package com.ratemydish.dto;

public class UserProfileResponse {

    private Long id;
    private String username;
    private String displayName;

    public UserProfileResponse() {
    }


    public UserProfileResponse(Long id, String username) {
        this.id = id;
        this.username = username;
    }


    public UserProfileResponse(Long id, String username, String displayName) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}