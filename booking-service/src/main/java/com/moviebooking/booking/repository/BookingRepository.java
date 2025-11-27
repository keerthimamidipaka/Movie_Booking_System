package com.moviebooking.booking.repository;

import com.moviebooking.booking.enums.BookingStatus;
import com.moviebooking.booking.entity.Booking;
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
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingReference(String bookingReference);

    List<Booking> findByCustomerEmail(String customerEmail);

    List<Booking> findByCustomerPhone(String customerPhone);

    List<Booking> findByMovieId(Long movieId);

    List<Booking> findByTheaterId(Long theaterId);

    List<Booking> findByShowtimeId(Long showtimeId);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByPaymentStatus(Booking.PaymentStatus paymentStatus);

    @Query("SELECT b FROM Booking b WHERE b.customerEmail = :email AND b.status = :status ORDER BY b.bookingDate DESC")
    List<Booking> findByCustomerEmailAndStatus(@Param("email") String email, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.customerEmail = :email AND b.paymentStatus = :paymentStatus ORDER BY b.bookingDate DESC")
    List<Booking> findByCustomerEmailAndPaymentStatus(@Param("email") String email, @Param("paymentStatus") Booking.PaymentStatus paymentStatus);

    @Query("SELECT b FROM Booking b WHERE b.bookingDate BETWEEN :startDate AND :endDate ORDER BY b.bookingDate DESC")
    List<Booking> findByBookingDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT b FROM Booking b WHERE b.showDateTime BETWEEN :startDate AND :endDate ORDER BY b.showDateTime")
    List<Booking> findByShowDateTimeBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT b FROM Booking b WHERE b.movieId = :movieId AND b.status = :status")
    List<Booking> findByMovieIdAndStatus(@Param("movieId") Long movieId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.theaterId = :theaterId AND b.status = :status")
    List<Booking> findByTheaterIdAndStatus(@Param("theaterId") Long theaterId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.showtimeId = :showtimeId AND b.status = :status")
    List<Booking> findByShowtimeIdAndStatus(@Param("showtimeId") Long showtimeId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.paymentMethod = :paymentMethod AND b.paymentStatus = :paymentStatus")
    List<Booking> findByPaymentMethodAndPaymentStatus(
        @Param("paymentMethod") Booking.PaymentMethod paymentMethod, 
        @Param("paymentStatus") Booking.PaymentStatus paymentStatus
    );

    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.showDateTime < :currentTime")
    List<Booking> findExpiredBookings(@Param("status") BookingStatus status, @Param("currentTime") LocalDateTime currentTime);

    Page<Booking> findByStatusOrderByBookingDateDesc(BookingStatus status, Pageable pageable);

    Page<Booking> findByCustomerEmailOrderByBookingDateDesc(String customerEmail, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.movieId = :movieId AND b.status = :status")
    Long countByMovieIdAndStatus(@Param("movieId") Long movieId, @Param("status") BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.theaterId = :theaterId AND b.status = :status")
    Long countByTheaterIdAndStatus(@Param("theaterId") Long theaterId, @Param("status") BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.showtimeId = :showtimeId AND b.status = :status")
    Long countByShowtimeIdAndStatus(@Param("showtimeId") Long showtimeId, @Param("status") BookingStatus status);

    @Query("SELECT SUM(b.finalAmount) FROM Booking b WHERE b.movieId = :movieId AND b.paymentStatus = :paymentStatus")
    Double sumRevenueByMovieIdAndPaymentStatus(@Param("movieId") Long movieId, @Param("paymentStatus") Booking.PaymentStatus paymentStatus);

    @Query("SELECT SUM(b.finalAmount) FROM Booking b WHERE b.theaterId = :theaterId AND b.paymentStatus = :paymentStatus")
    Double sumRevenueByTheaterIdAndPaymentStatus(@Param("theaterId") Long theaterId, @Param("paymentStatus") Booking.PaymentStatus paymentStatus);

    @Query("SELECT SUM(b.numberOfSeats) FROM Booking b WHERE b.movieId = :movieId AND b.status = :status")
    Long sumSeatsByMovieIdAndStatus(@Param("movieId") Long movieId, @Param("status") BookingStatus status);

    @Query("SELECT SUM(b.numberOfSeats) FROM Booking b WHERE b.theaterId = :theaterId AND b.status = :status")
    Long sumSeatsByTheaterIdAndStatus(@Param("theaterId") Long theaterId, @Param("status") BookingStatus status);
}