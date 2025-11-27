package com.moviebooking.showtime.service;
import com.moviebooking.showtime.enums.ShowStatus;

import com.moviebooking.showtime.entity.Showtime;
import com.moviebooking.showtime.exception.ShowtimeNotFoundException;
import com.moviebooking.showtime.exception.ShowtimeConflictException;
import com.moviebooking.showtime.exception.InsufficientSeatsException;
import com.moviebooking.showtime.repository.ShowtimeRepository;
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
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;

    @Override
    public Showtime createShowtime(Showtime showtime) {
        log.info("Creating new showtime for movie: {} at theater: {}", showtime.getMovieId(), showtime.getTheaterId());

        // Check for conflicting showtimes
        if (hasConflictingShowtimes(showtime.getMovieId(), showtime.getTheaterId(), 
                                   showtime.getStartTime(), showtime.getEndTime())) {
            throw new ShowtimeConflictException("Showtime conflicts with existing schedule");
        }

        showtime.setCreatedAt(LocalDateTime.now());
        showtime.setAvailableSeats(showtime.getTotalSeats());
        showtime.setStatus(Showtime.ShowStatus.ACTIVE);

        return showtimeRepository.save(showtime);
    }

    @Override
    public Showtime updateShowtime(Long id, Showtime showtime) {
        log.info("Updating showtime with id: {}", id);
        Showtime existingShowtime = showtimeRepository.findByIdAndStatus(id, Showtime.ShowStatus.ACTIVE)
            .orElseThrow(() -> new ShowtimeNotFoundException("Showtime not found with id: " + id));

        existingShowtime.setStartTime(showtime.getStartTime());
        existingShowtime.setEndTime(showtime.getEndTime());
        existingShowtime.setTotalSeats(showtime.getTotalSeats());
        existingShowtime.setPrice(showtime.getPrice());
        existingShowtime.setScreenNumber(showtime.getScreenNumber());
        existingShowtime.setShowType(showtime.getShowType());
        existingShowtime.setUpdatedAt(LocalDateTime.now());

        return showtimeRepository.save(existingShowtime);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Showtime> getShowtimeById(Long id) {
        log.info("Fetching showtime with id: {}", id);
        return showtimeRepository.findByIdAndStatus(id, Showtime.ShowStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Showtime> getAllActiveShowtimes() {
        log.info("Fetching all active showtimes");
        return showtimeRepository.findByStatusOrderByStartTime(Showtime.ShowStatus.ACTIVE, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Showtime> getAllActiveShowtimesPaginated(Pageable pageable) {
        log.info("Fetching paginated active showtimes");
        return showtimeRepository.findByStatusOrderByStartTime(Showtime.ShowStatus.ACTIVE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Showtime> getShowtimesByMovie(Long movieId) {
        log.info("Fetching showtimes for movie: {}", movieId);
        return showtimeRepository.findByMovieIdAndStatus(movieId, Showtime.ShowStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Showtime> getShowtimesByTheater(Long theaterId) {
        log.info("Fetching showtimes for theater: {}", theaterId);
        return showtimeRepository.findByTheaterIdAndStatus(theaterId, Showtime.ShowStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Showtime> getShowtimesByMovieAndTheater(Long movieId, Long theaterId) {
        log.info("Fetching showtimes for movie: {} at theater: {}", movieId, theaterId);
        return showtimeRepository.findByMovieIdAndTheaterIdAndStatus(movieId, theaterId, Showtime.ShowStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Showtime> getShowtimesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching showtimes between {} and {}", startDate, endDate);
        return showtimeRepository.findByStartTimeBetweenAndStatus(startDate, endDate, Showtime.ShowStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Showtime> getShowtimesByMovieAndDateRange(Long movieId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching showtimes for movie: {} between {} and {}", movieId, startDate, endDate);
        return showtimeRepository.findByMovieIdAndStartTimeBetweenAndStatus(movieId, startDate, endDate, Showtime.ShowStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Showtime> getShowtimesByTheaterAndScreen(Long theaterId, String screenNumber) {
        log.info("Fetching showtimes for theater: {} screen: {}", theaterId, screenNumber);
        return showtimeRepository.findByTheaterIdAndScreenNumberAndStatus(theaterId, screenNumber, Showtime.ShowStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Showtime> getShowtimesWithAvailableSeats(Integer minSeats) {
        log.info("Fetching showtimes with at least {} available seats", minSeats);
        return showtimeRepository.findByAvailableSeatsGreaterThanEqualAndStatus(minSeats, Showtime.ShowStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Showtime> getShowtimesByType(Showtime.ShowType showType) {
        log.info("Fetching showtimes of type: {}", showType);
        return showtimeRepository.findByShowTypeAndStatus(showType, Showtime.ShowStatus.ACTIVE);
    }

    @Override
    public boolean reserveSeats(Long showtimeId, Integer seatsToReserve) {
        log.info("Reserving {} seats for showtime: {}", seatsToReserve, showtimeId);
        Showtime showtime = showtimeRepository.findByIdAndStatus(showtimeId, Showtime.ShowStatus.ACTIVE)
            .orElseThrow(() -> new ShowtimeNotFoundException("Showtime not found with id: " + showtimeId));

        if (!showtime.hasAvailableSeats(seatsToReserve)) {
            throw new InsufficientSeatsException("Not enough available seats. Available: " + showtime.getAvailableSeats());
        }

        showtime.reserveSeats(seatsToReserve);

        // Check if showtime is now housefull
        if (showtime.getAvailableSeats() == 0) {
            showtime.setStatus(Showtime.ShowStatus.HOUSEFULL);
        }

        showtimeRepository.save(showtime);
        return true;
    }

    @Override
    public boolean releaseSeats(Long showtimeId, Integer seatsToRelease) {
        log.info("Releasing {} seats for showtime: {}", seatsToRelease, showtimeId);
        Showtime showtime = showtimeRepository.findById(showtimeId)
            .orElseThrow(() -> new ShowtimeNotFoundException("Showtime not found with id: " + showtimeId));

        showtime.releaseSeats(seatsToRelease);

        // If showtime was housefull, change status back to active
        if (showtime.getStatus() == Showtime.ShowStatus.HOUSEFULL && showtime.getAvailableSeats() > 0) {
            showtime.setStatus(Showtime.ShowStatus.ACTIVE);
        }

        showtimeRepository.save(showtime);
        return true;
    }

    @Override
    public void cancelShowtime(Long id) {
        log.info("Cancelling showtime with id: {}", id);
        Showtime showtime = showtimeRepository.findById(id)
            .orElseThrow(() -> new ShowtimeNotFoundException("Showtime not found with id: " + id));
        showtime.setStatus(Showtime.ShowStatus.CANCELLED);
        showtime.setUpdatedAt(LocalDateTime.now());
        showtimeRepository.save(showtime);
    }

    @Override
    public void completeShowtime(Long id) {
        log.info("Completing showtime with id: {}", id);
        Showtime showtime = showtimeRepository.findById(id)
            .orElseThrow(() -> new ShowtimeNotFoundException("Showtime not found with id: " + id));
        showtime.setStatus(Showtime.ShowStatus.COMPLETED);
        showtime.setUpdatedAt(LocalDateTime.now());
        showtimeRepository.save(showtime);
    }

    @Override
    public void markAsHousefull(Long id) {
        log.info("Marking showtime as housefull with id: {}", id);
        Showtime showtime = showtimeRepository.findById(id)
            .orElseThrow(() -> new ShowtimeNotFoundException("Showtime not found with id: " + id));
        showtime.setStatus(Showtime.ShowStatus.HOUSEFULL);
        showtime.setAvailableSeats(0);
        showtime.setUpdatedAt(LocalDateTime.now());
        showtimeRepository.save(showtime);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasConflictingShowtimes(Long movieId, Long theaterId, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("Checking for conflicting showtimes");
        List<Showtime> conflictingShowtimes = showtimeRepository.findConflictingShowtimes(movieId, theaterId, startTime, endTime);
        return !conflictingShowtimes.isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getShowtimeCountByMovie(Long movieId) {
        log.info("Getting showtime count for movie: {}", movieId);
        return showtimeRepository.countByMovieIdAndStatus(movieId, Showtime.ShowStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getShowtimeCountByTheater(Long theaterId) {
        log.info("Getting showtime count for theater: {}", theaterId);
        return showtimeRepository.countByTheaterIdAndStatus(theaterId, Showtime.ShowStatus.ACTIVE);
    }
}