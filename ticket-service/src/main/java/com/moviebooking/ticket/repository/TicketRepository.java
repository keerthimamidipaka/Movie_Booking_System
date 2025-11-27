package com.moviebooking.ticket.repository;

import com.moviebooking.ticket.entity.Ticket;
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
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    List<Ticket> findByBookingId(Long bookingId);

    List<Ticket> findByShowtimeId(Long showtimeId);

    List<Ticket> findByMovieId(Long movieId);

    List<Ticket> findByTheaterId(Long theaterId);

    List<Ticket> findByCustomerEmail(String customerEmail);

    List<Ticket> findByCustomerPhone(String customerPhone);

    List<Ticket> findByStatus(Ticket.TicketStatus status);

    @Query("SELECT t FROM Ticket t WHERE t.customerEmail = :email AND t.status = :status ORDER BY t.showDateTime DESC")
    List<Ticket> findByCustomerEmailAndStatus(@Param("email") String email, @Param("status") Ticket.TicketStatus status);

    @Query("SELECT t FROM Ticket t WHERE t.showDateTime BETWEEN :startDate AND :endDate ORDER BY t.showDateTime")
    List<Ticket> findByShowDateTimeBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Ticket t WHERE t.movieId = :movieId AND t.showDateTime BETWEEN :startDate AND :endDate ORDER BY t.showDateTime")
    List<Ticket> findByMovieIdAndShowDateTimeBetween(
        @Param("movieId") Long movieId,
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t FROM Ticket t WHERE t.theaterId = :theaterId AND t.showDateTime BETWEEN :startDate AND :endDate ORDER BY t.showDateTime")
    List<Ticket> findByTheaterIdAndShowDateTimeBetween(
        @Param("theaterId") Long theaterId,
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t FROM Ticket t WHERE t.showtimeId = :showtimeId AND t.seatNumber = :seatNumber")
    Optional<Ticket> findByShowtimeIdAndSeatNumber(@Param("showtimeId") Long showtimeId, @Param("seatNumber") String seatNumber);

    @Query("SELECT t FROM Ticket t WHERE t.seatType = :seatType AND t.status = :status ORDER BY t.showDateTime DESC")
    List<Ticket> findBySeatTypeAndStatus(@Param("seatType") Ticket.SeatType seatType, @Param("status") Ticket.TicketStatus status);

    @Query("SELECT t FROM Ticket t WHERE t.validUntil < :currentTime AND t.status = :status")
    List<Ticket> findExpiredTickets(@Param("currentTime") LocalDateTime currentTime, @Param("status") Ticket.TicketStatus status);

    Page<Ticket> findByStatusOrderByShowDateTimeDesc(Ticket.TicketStatus status, Pageable pageable);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.movieId = :movieId AND t.status = :status")
    Long countByMovieIdAndStatus(@Param("movieId") Long movieId, @Param("status") Ticket.TicketStatus status);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.theaterId = :theaterId AND t.status = :status")
    Long countByTheaterIdAndStatus(@Param("theaterId") Long theaterId, @Param("status") Ticket.TicketStatus status);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.showtimeId = :showtimeId AND t.status = :status")
    Long countByShowtimeIdAndStatus(@Param("showtimeId") Long showtimeId, @Param("status") Ticket.TicketStatus status);

    @Query("SELECT SUM(t.price) FROM Ticket t WHERE t.movieId = :movieId AND t.status = :status")
    Double sumPriceByMovieIdAndStatus(@Param("movieId") Long movieId, @Param("status") Ticket.TicketStatus status);

    @Query("SELECT SUM(t.price) FROM Ticket t WHERE t.theaterId = :theaterId AND t.status = :status")
    Double sumPriceByTheaterIdAndStatus(@Param("theaterId") Long theaterId, @Param("status") Ticket.TicketStatus status);
}