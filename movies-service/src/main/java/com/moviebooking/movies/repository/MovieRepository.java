package com.moviebooking.movies.repository;

import com.moviebooking.movies.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByIsActiveTrue();

    Page<Movie> findByIsActiveTrueOrderByReleaseDateDesc(Pageable pageable);

    List<Movie> findByGenreAndIsActiveTrue(String genre);

    List<Movie> findByLanguageAndIsActiveTrue(String language);

    @Query("SELECT m FROM Movie m WHERE m.title LIKE %:title% AND m.isActive = true")
    List<Movie> findByTitleContainingIgnoreCaseAndIsActiveTrue(@Param("title") String title);

    List<Movie> findByDirectorAndIsActiveTrue(String director);

    @Query("SELECT m FROM Movie m WHERE m.releaseDate BETWEEN :startDate AND :endDate AND m.isActive = true")
    List<Movie> findByReleaseDateBetweenAndIsActiveTrue(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT DISTINCT m.genre FROM Movie m WHERE m.isActive = true ORDER BY m.genre")
    List<String> findDistinctGenres();

    @Query("SELECT DISTINCT m.language FROM Movie m WHERE m.isActive = true ORDER BY m.language")
    List<String> findDistinctLanguages();

    Optional<Movie> findByIdAndIsActiveTrue(Long id);

    @Query("SELECT m FROM Movie m WHERE m.duration BETWEEN :minDuration AND :maxDuration AND m.isActive = true")
    List<Movie> findByDurationBetween(@Param("minDuration") Integer minDuration, @Param("maxDuration") Integer maxDuration);
}