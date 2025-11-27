package com.moviebooking.showtime.service;

import com.moviebooking.showtime.entity.Showtime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShowtimeService {

    Showtime createShowtime(Showtime showtime);

    Showtime updateShowtime(Long id, Showtime showtime);

    Optional<Showtime> getShowtimeById(Long id);

    List<Showtime> getAllActiveShowtimes();

    Page<Showtime> getAllActiveShowtimesPaginated(Pageable pageable);

    List<Showtime> getShowtimesByMovie(Long movieId);

    List<Showtime> getShowtimesByTheater(Long theaterId);

    List<Showtime> getShowtimesByMovieAndTheater(Long movieId, Long theaterId);

    List<Showtime> getShowtimesByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<Showtime> getShowtimesByMovieAndDateRange(Long movieId, LocalDateTime startDate, LocalDateTime endDate);

    List<Showtime> getShowtimesByTheaterAndScreen(Long theaterId, String screenNumber);

    List<Showtime> getShowtimesWithAvailableSeats(Integer minSeats);

    List<Showtime> getShowtimesByType(Showtime.ShowType showType);

    boolean reserveSeats(Long showtimeId, Integer seatsToReserve);

    boolean releaseSeats(Long showtimeId, Integer seatsToRelease);

    void cancelShowtime(Long id);

    void completeShowtime(Long id);

    void markAsHousefull(Long id);

    boolean hasConflictingShowtimes(Long movieId, Long theaterId, LocalDateTime startTime, LocalDateTime endTime);

    Long getShowtimeCountByMovie(Long movieId);

    Long getShowtimeCountByTheater(Long theaterId);
}