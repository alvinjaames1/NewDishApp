package com.ratemydish.dto;

public class CommentResponse {

    private Long id;
    private String username;
    private String text;

    public CommentResponse() {
    }

    public CommentResponse(Long id, String username, String text) {
        this.id = id;
        this.username = username;
        this.text = text;
    }

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}