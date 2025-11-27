package com.moviebooking.movies.service;

import com.moviebooking.movies.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovieService {

    Movie createMovie(Movie movie);

    Movie updateMovie(Long id, Movie movie);

    Optional<Movie> getMovieById(Long id);

    List<Movie> getAllActiveMovies();

    Page<Movie> getAllActiveMoviesPaginated(Pageable pageable);

    List<Movie> getMoviesByGenre(String genre);

    List<Movie> getMoviesByLanguage(String language);

    List<Movie> searchMoviesByTitle(String title);

    List<Movie> getMoviesByDirector(String director);

    List<Movie> getMoviesByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<Movie> getMoviesByDuration(Integer minDuration, Integer maxDuration);

    List<String> getAllGenres();

    List<String> getAllLanguages();

    void deleteMovie(Long id);

    void deactivateMovie(Long id);

    void activateMovie(Long id);
}