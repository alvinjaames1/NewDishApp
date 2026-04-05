package com.ratemydish.controller;

import com.ratemydish.dto.DishResponse;
import com.ratemydish.dto.UserProfileResponse;
import com.ratemydish.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/dishes")
    public Page<DishResponse> searchDishes(
            @RequestParam String keyword,
            @RequestParam(required = false) String cuisine,
            Pageable pageable) {

        return searchService.searchDishes(keyword, cuisine, pageable);
    }

    @GetMapping("/users")
    public Page<UserProfileResponse> searchUsers(
            @RequestParam String keyword,
            Pageable pageable) {

        return searchService.searchUsers(keyword, pageable);
    }
}