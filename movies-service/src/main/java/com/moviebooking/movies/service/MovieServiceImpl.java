package com.moviebooking.movies.service;

import com.moviebooking.movies.entity.Movie;
import com.moviebooking.movies.exception.MovieNotFoundException;
import com.moviebooking.movies.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public Movie createMovie(Movie movie) {
        log.info("Creating new movie: {}", movie.getTitle());
        movie.setCreatedAt(LocalDateTime.now());
        movie.setIsActive(true);
        return movieRepository.save(movie);
    }

    @Override
    public Movie updateMovie(Long id, Movie movie) {
        log.info("Updating movie with id: {}", id);
        Movie existingMovie = movieRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));

        existingMovie.setTitle(movie.getTitle());
        existingMovie.setDescription(movie.getDescription());
        existingMovie.setGenre(movie.getGenre());
        existingMovie.setDuration(movie.getDuration());
        existingMovie.setLanguage(movie.getLanguage());
        existingMovie.setDirector(movie.getDirector());
        existingMovie.setCast(movie.getCast());
        existingMovie.setReleaseDate(movie.getReleaseDate());
        existingMovie.setPosterUrl(movie.getPosterUrl());
        existingMovie.setTrailerUrl(movie.getTrailerUrl());
        existingMovie.setRating(movie.getRating());
        existingMovie.setUpdatedAt(LocalDateTime.now());

        return movieRepository.save(existingMovie);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Movie> getMovieById(Long id) {
        log.info("Fetching movie with id: {}", id);
        return movieRepository.findByIdAndIsActiveTrue(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getAllActiveMovies() {
        log.info("Fetching all active movies");
        return movieRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Movie> getAllActiveMoviesPaginated(Pageable pageable) {
        log.info("Fetching paginated active movies");
        return movieRepository.findByIsActiveTrueOrderByReleaseDateDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getMoviesByGenre(String genre) {
        log.info("Fetching movies by genre: {}", genre);
        return movieRepository.findByGenreAndIsActiveTrue(genre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getMoviesByLanguage(String language) {
        log.info("Fetching movies by language: {}", language);
        return movieRepository.findByLanguageAndIsActiveTrue(language);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> searchMoviesByTitle(String title) {
        log.info("Searching movies by title: {}", title);
        return movieRepository.findByTitleContainingIgnoreCaseAndIsActiveTrue(title);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getMoviesByDirector(String director) {
        log.info("Fetching movies by director: {}", director);
        return movieRepository.findByDirectorAndIsActiveTrue(director);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getMoviesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching movies between {} and {}", startDate, endDate);
        return movieRepository.findByReleaseDateBetweenAndIsActiveTrue(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getMoviesByDuration(Integer minDuration, Integer maxDuration) {
        log.info("Fetching movies with duration between {} and {} minutes", minDuration, maxDuration);
        return movieRepository.findByDurationBetween(minDuration, maxDuration);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllGenres() {
        log.info("Fetching all movie genres");
        return movieRepository.findDistinctGenres();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllLanguages() {
        log.info("Fetching all movie languages");
        return movieRepository.findDistinctLanguages();
    }

    @Override
    public void deleteMovie(Long id) {
        log.info("Deleting movie with id: {}", id);
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
        movieRepository.delete(movie);
    }

    @Override
    public void deactivateMovie(Long id) {
        log.info("Deactivating movie with id: {}", id);
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
        movie.setIsActive(false);
        movie.setUpdatedAt(LocalDateTime.now());
        movieRepository.save(movie);
    }

    @Override
    public void activateMovie(Long id) {
        log.info("Activating movie with id: {}", id);
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
        movie.setIsActive(true);
        movie.setUpdatedAt(LocalDateTime.now());
        movieRepository.save(movie);
    }
}