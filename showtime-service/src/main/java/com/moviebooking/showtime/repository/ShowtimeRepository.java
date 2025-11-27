package com.moviebooking.showtime.repository;
import com.moviebooking.showtime.enums.ShowStatus;

import com.moviebooking.showtime.entity.Showtime;
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
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    List<Showtime> findByMovieIdAndStatus(Long movieId, Showtime.ShowStatus status);

    List<Showtime> findByTheaterIdAndStatus(Long theaterId, Showtime.ShowStatus status);

    @Query("SELECT s FROM Showtime s WHERE s.movieId = :movieId AND s.theaterId = :theaterId AND s.status = :status ORDER BY s.startTime")
    List<Showtime> findByMovieIdAndTheaterIdAndStatus(
        @Param("movieId") Long movieId, 
        @Param("theaterId") Long theaterId, 
        @Param("status") Showtime.ShowStatus status
    );

    @Query("SELECT s FROM Showtime s WHERE s.startTime BETWEEN :startDate AND :endDate AND s.status = :status ORDER BY s.startTime")
    List<Showtime> findByStartTimeBetweenAndStatus(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate, 
        @Param("status") Showtime.ShowStatus status
    );

    @Query("SELECT s FROM Showtime s WHERE s.movieId = :movieId AND s.startTime BETWEEN :startDate AND :endDate AND s.status = :status ORDER BY s.startTime")
    List<Showtime> findByMovieIdAndStartTimeBetweenAndStatus(
        @Param("movieId") Long movieId,
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate, 
        @Param("status") Showtime.ShowStatus status
    );

    @Query("SELECT s FROM Showtime s WHERE s.theaterId = :theaterId AND s.screenNumber = :screenNumber AND s.status = :status ORDER BY s.startTime")
    List<Showtime> findByTheaterIdAndScreenNumberAndStatus(
        @Param("theaterId") Long theaterId, 
        @Param("screenNumber") String screenNumber, 
        @Param("status") Showtime.ShowStatus status
    );

    @Query("SELECT s FROM Showtime s WHERE s.availableSeats >= :minSeats AND s.status = :status ORDER BY s.startTime")
    List<Showtime> findByAvailableSeatsGreaterThanEqualAndStatus(
        @Param("minSeats") Integer minSeats, 
        @Param("status") Showtime.ShowStatus status
    );

    @Query("SELECT s FROM Showtime s WHERE s.showType = :showType AND s.status = :status ORDER BY s.startTime")
    List<Showtime> findByShowTypeAndStatus(
        @Param("showType") Showtime.ShowType showType, 
        @Param("status") Showtime.ShowStatus status
    );

    @Query("SELECT s FROM Showtime s WHERE s.movieId = :movieId AND s.theaterId = :theaterId AND " +
           "((s.startTime BETWEEN :startTime AND :endTime) OR (s.endTime BETWEEN :startTime AND :endTime) OR " +
           "(s.startTime <= :startTime AND s.endTime >= :endTime))")
    List<Showtime> findConflictingShowtimes(
        @Param("movieId") Long movieId,
        @Param("theaterId") Long theaterId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    Page<Showtime> findByStatusOrderByStartTime(Showtime.ShowStatus status, Pageable pageable);

    Optional<Showtime> findByIdAndStatus(Long id, Showtime.ShowStatus status);

    @Query("SELECT COUNT(s) FROM Showtime s WHERE s.movieId = :movieId AND s.status = :status")
    Long countByMovieIdAndStatus(@Param("movieId") Long movieId, @Param("status") Showtime.ShowStatus status);

    @Query("SELECT COUNT(s) FROM Showtime s WHERE s.theaterId = :theaterId AND s.status = :status")
    Long countByTheaterIdAndStatus(@Param("theaterId") Long theaterId, @Param("status") Showtime.ShowStatus status);
}