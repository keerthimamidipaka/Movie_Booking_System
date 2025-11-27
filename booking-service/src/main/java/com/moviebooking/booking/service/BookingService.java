package com.moviebooking.booking.service;

import com.moviebooking.booking.enums.BookingStatus;
import com.moviebooking.booking.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingService {

    Booking createBooking(Booking booking);

    Booking updateBooking(Long id, Booking booking);

    Optional<Booking> getBookingById(Long id);

    Optional<Booking> getBookingByReference(String bookingReference);

    List<Booking> getBookingsByCustomerEmail(String customerEmail);

    List<Booking> getBookingsByCustomerPhone(String customerPhone);

    List<Booking> getBookingsByMovie(Long movieId);

    List<Booking> getBookingsByTheater(Long theaterId);

    List<Booking> getBookingsByShowtime(Long showtimeId);

    List<Booking> getBookingsByStatus(BookingStatus status);

    List<Booking> getBookingsByPaymentStatus(Booking.PaymentStatus paymentStatus);

    List<Booking> getBookingsByCustomerAndStatus(String email, BookingStatus status);

    List<Booking> getBookingsByCustomerAndPaymentStatus(String email, Booking.PaymentStatus paymentStatus);

    List<Booking> getBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<Booking> getBookingsByShowDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<Booking> getBookingsByMovieAndStatus(Long movieId, BookingStatus status);

    List<Booking> getBookingsByTheaterAndStatus(Long theaterId, BookingStatus status);

    List<Booking> getBookingsByShowtimeAndStatus(Long showtimeId, BookingStatus status);

    List<Booking> getBookingsByPaymentMethod(Booking.PaymentMethod paymentMethod);

    Page<Booking> getBookingsByStatusPaginated(BookingStatus status, Pageable pageable);

    Page<Booking> getBookingsByCustomerPaginated(String customerEmail, Pageable pageable);

    boolean confirmBooking(Long bookingId, String paymentId);

    boolean confirmBooking(String bookingReference, String paymentId);

    boolean cancelBooking(Long bookingId, String reason);

    boolean cancelBooking(String bookingReference, String reason);

    boolean refundBooking(Long bookingId);

    void expireOldBookings();

    String generateBookingReference();

    Double calculateTotalAmount(List<String> seatNumbers, Double basePrice);

    Double calculateTaxAmount(Double totalAmount);

    Double calculateFinalAmount(Double totalAmount, Double taxAmount);

    boolean validateBooking(Booking booking);

    Long getBookingCountByMovie(Long movieId);

    Long getBookingCountByTheater(Long theaterId);

    Long getBookingCountByShowtime(Long showtimeId);

    Double getRevenueByMovie(Long movieId);

    Double getRevenueByTheater(Long theaterId);

    Long getTotalSeatsByMovie(Long movieId);

    Long getTotalSeatsByTheater(Long theaterId);
}