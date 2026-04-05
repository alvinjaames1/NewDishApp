package com.ratemydish.repository;

import com.ratemydish.entity.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DishRepository extends JpaRepository<Dish, Long> {

    List<Dish> findByAuthorUsernameAndDeletedFalse(String username);

    Page<Dish> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
        SELECT d
        FROM Dish d
        WHERE d.deleted = false
          AND (
                LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%'))
             OR LOWER(d.description) LIKE LOWER(CONCAT('%', :description, '%'))
          )
    """)
    Page<Dish> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            @Param("title") String title,
            @Param("description") String description,
            Pageable pageable
    );

    @Query("""
        SELECT d
        FROM Dish d
        WHERE d.deleted = false
          AND (
                LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%'))
             OR LOWER(d.description) LIKE LOWER(CONCAT('%', :description, '%'))
          )
          AND LOWER(d.cuisine) = LOWER(:cuisine)
    """)
    Page<Dish> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndCuisineIgnoreCase(
            @Param("title") String title,
            @Param("description") String description,
            @Param("cuisine") String cuisine,
            Pageable pageable
    );

    @Query("""
        SELECT d
        FROM Dish d
        WHERE d.deleted = false
          AND (
                LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(d.cuisine) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    """)
    List<Dish> searchAll(@Param("keyword") String keyword);
}