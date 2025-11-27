package com.moviebooking.movies.controller;

import com.moviebooking.movies.entity.Movie;
import com.moviebooking.movies.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody Movie movie) {
        log.info("Creating movie: {}", movie.getTitle());
        Movie createdMovie = movieService.createMovie(movie);
        return new ResponseEntity<>(createdMovie, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @Valid @RequestBody Movie movie) {
        log.info("Updating movie with id: {}", id);
        try {
            Movie updatedMovie = movieService.updateMovie(id, movie);
            if (updatedMovie == null) {
                return ResponseEntity.notFound().build(); 
            }
            return ResponseEntity.ok(updatedMovie); 
        } catch (Exception e) {
            log.error("Error updating movie", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        log.info("Fetching movie with id: {}", id);
        Optional<Movie> movie = movieService.getMovieById(id);
        return movie.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllActiveMovies() {
        log.info("Fetching all active movies");
        List<Movie> movies = movieService.getAllActiveMovies();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Movie>> getAllActiveMoviesPaginated(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Fetching paginated movies - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> movies = movieService.getAllActiveMoviesPaginated(pageable);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Movie>> getMoviesByGenre(@PathVariable @NotBlank String genre) {
        log.info("Fetching movies by genre: {}", genre);
        List<Movie> movies = movieService.getMoviesByGenre(genre);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/language/{language}")
    public ResponseEntity<List<Movie>> getMoviesByLanguage(@PathVariable @NotBlank String language) {
        log.info("Fetching movies by language: {}", language);
        List<Movie> movies = movieService.getMoviesByLanguage(language);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Movie>> searchMoviesByTitle(@RequestParam @NotBlank String title) {
        log.info("Searching movies by title: {}", title);
        List<Movie> movies = movieService.searchMoviesByTitle(title);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/director/{director}")
    public ResponseEntity<List<Movie>> getMoviesByDirector(@PathVariable @NotBlank String director) {
        log.info("Fetching movies by director: {}", director);
        List<Movie> movies = movieService.getMoviesByDirector(director);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Movie>> getMoviesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching movies between {} and {}", startDate, endDate);
        List<Movie> movies = movieService.getMoviesByDateRange(startDate, endDate);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/duration")
    public ResponseEntity<List<Movie>> getMoviesByDuration(
            @RequestParam @Min(1) Integer minDuration,
            @RequestParam @Min(1) Integer maxDuration) {
        log.info("Fetching movies with duration between {} and {} minutes", minDuration, maxDuration);
        List<Movie> movies = movieService.getMoviesByDuration(minDuration, maxDuration);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/genres")
    public ResponseEntity<List<String>> getAllGenres() {
        log.info("Fetching all movie genres");
        List<String> genres = movieService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/languages")
    public ResponseEntity<List<String>> getAllLanguages() {
        log.info("Fetching all movie languages");
        List<String> languages = movieService.getAllLanguages();
        return ResponseEntity.ok(languages);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        log.info("Deleting movie with id: {}", id);
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateMovie(@PathVariable Long id) {
        log.info("Deactivating movie with id: {}", id);
        movieService.deactivateMovie(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateMovie(@PathVariable Long id) {
        log.info("Activating movie with id: {}", id);
        movieService.activateMovie(id);
        return ResponseEntity.ok().build();
    }
}