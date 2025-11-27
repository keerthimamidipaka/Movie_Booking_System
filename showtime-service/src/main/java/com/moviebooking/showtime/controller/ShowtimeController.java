package com.moviebooking.showtime.controller;

import com.moviebooking.showtime.entity.Showtime;
import com.moviebooking.showtime.service.ShowtimeService;
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
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "*")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PostMapping
    public ResponseEntity<Showtime> createShowtime(@Valid @RequestBody Showtime showtime) {
        log.info("Creating showtime for movie: {} at theater: {}", showtime.getMovieId(), showtime.getTheaterId());
        Showtime createdShowtime = showtimeService.createShowtime(showtime);
        return new ResponseEntity<>(createdShowtime, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Showtime> updateShowtime(@PathVariable Long id, @Valid @RequestBody Showtime showtime) {
        log.info("Updating showtime with id: {}", id);
        Showtime updatedShowtime = showtimeService.updateShowtime(id, showtime);
        return ResponseEntity.ok(updatedShowtime);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Showtime> getShowtimeById(@PathVariable Long id) {
        log.info("Fetching showtime with id: {}", id);
        Optional<Showtime> showtime = showtimeService.getShowtimeById(id);
        return showtime.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Showtime>> getAllActiveShowtimes() {
        log.info("Fetching all active showtimes");
        List<Showtime> showtimes = showtimeService.getAllActiveShowtimes();
        return ResponseEntity.ok(showtimes);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Showtime>> getAllActiveShowtimesPaginated(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Fetching paginated showtimes - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Showtime> showtimes = showtimeService.getAllActiveShowtimesPaginated(pageable);
        return ResponseEntity.ok(showtimes);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Showtime>> getShowtimesByMovie(@PathVariable @NotNull Long movieId) {
        log.info("Fetching showtimes for movie: {}", movieId);
        List<Showtime> showtimes = showtimeService.getShowtimesByMovie(movieId);
        return ResponseEntity.ok(showtimes);
    }

    @GetMapping("/theater/{theaterId}")
    public ResponseEntity<List<Showtime>> getShowtimesByTheater(@PathVariable @NotNull Long theaterId) {
        log.info("Fetching showtimes for theater: {}", theaterId);
        List<Showtime> showtimes = showtimeService.getShowtimesByTheater(theaterId);
        return ResponseEntity.ok(showtimes);
    }

    @GetMapping("/movie/{movieId}/theater/{theaterId}")
    public ResponseEntity<List<Showtime>> getShowtimesByMovieAndTheater(
            @PathVariable @NotNull Long movieId,
            @PathVariable @NotNull Long theaterId) {
        log.info("Fetching showtimes for movie: {} at theater: {}", movieId, theaterId);
        List<Showtime> showtimes = showtimeService.getShowtimesByMovieAndTheater(movieId, theaterId);
        return ResponseEntity.ok(showtimes);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Showtime>> getShowtimesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching showtimes between {} and {}", startDate, endDate);
        List<Showtime> showtimes = showtimeService.getShowtimesByDateRange(startDate, endDate);
        return ResponseEntity.ok(showtimes);
    }

    @GetMapping("/movie/{movieId}/date-range")
    public ResponseEntity<List<Showtime>> getShowtimesByMovieAndDateRange(
            @PathVariable @NotNull Long movieId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching showtimes for movie: {} between {} and {}", movieId, startDate, endDate);
        List<Showtime> showtimes = showtimeService.getShowtimesByMovieAndDateRange(movieId, startDate, endDate);
        return ResponseEntity.ok(showtimes);
    }

    @GetMapping("/theater/{theaterId}/screen/{screenNumber}")
    public ResponseEntity<List<Showtime>> getShowtimesByTheaterAndScreen(
            @PathVariable @NotNull Long theaterId,
            @PathVariable String screenNumber) {
        log.info("Fetching showtimes for theater: {} screen: {}", theaterId, screenNumber);
        List<Showtime> showtimes = showtimeService.getShowtimesByTheaterAndScreen(theaterId, screenNumber);
        return ResponseEntity.ok(showtimes);
    }

    @GetMapping("/available-seats")
    public ResponseEntity<List<Showtime>> getShowtimesWithAvailableSeats(
            @RequestParam @Min(1) Integer minSeats) {
        log.info("Fetching showtimes with at least {} available seats", minSeats);
        List<Showtime> showtimes = showtimeService.getShowtimesWithAvailableSeats(minSeats);
        return ResponseEntity.ok(showtimes);
    }

    @GetMapping("/type/{showType}")
    public ResponseEntity<List<Showtime>> getShowtimesByType(@PathVariable Showtime.ShowType showType) {
        log.info("Fetching showtimes of type: {}", showType);
        List<Showtime> showtimes = showtimeService.getShowtimesByType(showType);
        return ResponseEntity.ok(showtimes);
    }

    @PatchMapping("/{id}/reserve-seats")
    public ResponseEntity<String> reserveSeats(
            @PathVariable Long id,
            @RequestParam @Min(1) Integer seats) {
        log.info("Reserving {} seats for showtime: {}", seats, id);
        boolean success = showtimeService.reserveSeats(id, seats);
        if (success) {
            return ResponseEntity.ok("Seats reserved successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to reserve seats");
        }
    }

    @PatchMapping("/{id}/release-seats")
    public ResponseEntity<String> releaseSeats(
            @PathVariable Long id,
            @RequestParam @Min(1) Integer seats) {
        log.info("Releasing {} seats for showtime: {}", seats, id);
        boolean success = showtimeService.releaseSeats(id, seats);
        if (success) {
            return ResponseEntity.ok("Seats released successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to release seats");
        }
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelShowtime(@PathVariable Long id) {
        log.info("Cancelling showtime with id: {}", id);
        showtimeService.cancelShowtime(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> completeShowtime(@PathVariable Long id) {
        log.info("Completing showtime with id: {}", id);
        showtimeService.completeShowtime(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/housefull")
    public ResponseEntity<Void> markAsHousefull(@PathVariable Long id) {
        log.info("Marking showtime as housefull with id: {}", id);
        showtimeService.markAsHousefull(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/movie/{movieId}/count")
    public ResponseEntity<Long> getShowtimeCountByMovie(@PathVariable @NotNull Long movieId) {
        log.info("Getting showtime count for movie: {}", movieId);
        Long count = showtimeService.getShowtimeCountByMovie(movieId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/theater/{theaterId}/count")
    public ResponseEntity<Long> getShowtimeCountByTheater(@PathVariable @NotNull Long theaterId) {
        log.info("Getting showtime count for theater: {}", theaterId);
        Long count = showtimeService.getShowtimeCountByTheater(theaterId);
        return ResponseEntity.ok(count);
    }
}