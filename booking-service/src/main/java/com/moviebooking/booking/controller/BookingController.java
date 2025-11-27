package com.moviebooking.booking.controller;

import com.moviebooking.booking.enums.BookingStatus;
import com.moviebooking.booking.entity.Booking;
import com.moviebooking.booking.service.BookingService;
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
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody Booking booking) {
        log.info("Creating booking for customer: {}", booking.getCustomerEmail());
        Booking createdBooking = bookingService.createBooking(booking);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @Valid @RequestBody Booking booking) {
        log.info("Updating booking with id: {}", id);
        Booking updatedBooking = bookingService.updateBooking(id, booking);
        return ResponseEntity.ok(updatedBooking);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        log.info("Fetching booking with id: {}", id);
        Optional<Booking> booking = bookingService.getBookingById(id);
        return booking.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reference/{bookingReference}")
    public ResponseEntity<Booking> getBookingByReference(@PathVariable @NotBlank String bookingReference) {
        log.info("Fetching booking with reference: {}", bookingReference);
        Optional<Booking> booking = bookingService.getBookingByReference(bookingReference);
        return booking.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/email/{email}")
    public ResponseEntity<List<Booking>> getBookingsByCustomerEmail(@PathVariable @NotBlank String email) {
        log.info("Fetching bookings for customer: {}", email);
        List<Booking> bookings = bookingService.getBookingsByCustomerEmail(email);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/customer/phone/{phone}")
    public ResponseEntity<List<Booking>> getBookingsByCustomerPhone(@PathVariable @NotBlank String phone) {
        log.info("Fetching bookings for phone: {}", phone);
        List<Booking> bookings = bookingService.getBookingsByCustomerPhone(phone);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Booking>> getBookingsByMovie(@PathVariable @NotNull Long movieId) {
        log.info("Fetching bookings for movie: {}", movieId);
        List<Booking> bookings = bookingService.getBookingsByMovie(movieId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/theater/{theaterId}")
    public ResponseEntity<List<Booking>> getBookingsByTheater(@PathVariable @NotNull Long theaterId) {
        log.info("Fetching bookings for theater: {}", theaterId);
        List<Booking> bookings = bookingService.getBookingsByTheater(theaterId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/showtime/{showtimeId}")
    public ResponseEntity<List<Booking>> getBookingsByShowtime(@PathVariable @NotNull Long showtimeId) {
        log.info("Fetching bookings for showtime: {}", showtimeId);
        List<Booking> bookings = bookingService.getBookingsByShowtime(showtimeId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Booking>> getBookingsByStatus(@PathVariable BookingStatus status) {
        log.info("Fetching bookings with status: {}", status);
        List<Booking> bookings = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<List<Booking>> getBookingsByPaymentStatus(@PathVariable Booking.PaymentStatus paymentStatus) {
        log.info("Fetching bookings with payment status: {}", paymentStatus);
        List<Booking> bookings = bookingService.getBookingsByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/customer/{email}/status/{status}")
    public ResponseEntity<List<Booking>> getBookingsByCustomerAndStatus(
            @PathVariable @NotBlank String email,
            @PathVariable BookingStatus status) {
        log.info("Fetching bookings for customer: {} with status: {}", email, status);
        List<Booking> bookings = bookingService.getBookingsByCustomerAndStatus(email, status);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Booking>> getBookingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching bookings between {} and {}", startDate, endDate);
        List<Booking> bookings = bookingService.getBookingsByDateRange(startDate, endDate);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/show-date-range")
    public ResponseEntity<List<Booking>> getBookingsByShowDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching bookings for shows between {} and {}", startDate, endDate);
        List<Booking> bookings = bookingService.getBookingsByShowDateRange(startDate, endDate);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/status/{status}/paginated")
    public ResponseEntity<Page<Booking>> getBookingsByStatusPaginated(
            @PathVariable BookingStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Fetching paginated bookings with status: {} - page: {}, size: {}", status, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Booking> bookings = bookingService.getBookingsByStatusPaginated(status, pageable);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/customer/{email}/paginated")
    public ResponseEntity<Page<Booking>> getBookingsByCustomerPaginated(
            @PathVariable @NotBlank String email,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Fetching paginated bookings for customer: {} - page: {}, size: {}", email, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Booking> bookings = bookingService.getBookingsByCustomerPaginated(email, pageable);
        return ResponseEntity.ok(bookings);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<String> confirmBooking(
            @PathVariable Long id,
            @RequestParam @NotBlank String paymentId) {
        log.info("Confirming booking: {} with payment: {}", id, paymentId);
        boolean success = bookingService.confirmBooking(id, paymentId);
        if (success) {
            return ResponseEntity.ok("Booking confirmed successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to confirm booking");
        }
    }

    @PatchMapping("/reference/{bookingReference}/confirm")
    public ResponseEntity<String> confirmBookingByReference(
            @PathVariable @NotBlank String bookingReference,
            @RequestParam @NotBlank String paymentId) {
        log.info("Confirming booking: {} with payment: {}", bookingReference, paymentId);
        boolean success = bookingService.confirmBooking(bookingReference, paymentId);
        if (success) {
            return ResponseEntity.ok("Booking confirmed successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to confirm booking");
        }
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<String> cancelBooking(
            @PathVariable Long id,
            @RequestParam @NotBlank String reason) {
        log.info("Cancelling booking: {} for reason: {}", id, reason);
        boolean success = bookingService.cancelBooking(id, reason);
        if (success) {
            return ResponseEntity.ok("Booking cancelled successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to cancel booking");
        }
    }

    @PatchMapping("/reference/{bookingReference}/cancel")
    public ResponseEntity<String> cancelBookingByReference(
            @PathVariable @NotBlank String bookingReference,
            @RequestParam @NotBlank String reason) {
        log.info("Cancelling booking: {} for reason: {}", bookingReference, reason);
        boolean success = bookingService.cancelBooking(bookingReference, reason);
        if (success) {
            return ResponseEntity.ok("Booking cancelled successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to cancel booking");
        }
    }

    @PatchMapping("/{id}/refund")
    public ResponseEntity<String> refundBooking(@PathVariable Long id) {
        log.info("Refunding booking: {}", id);
        boolean success = bookingService.refundBooking(id);
        if (success) {
            return ResponseEntity.ok("Booking refunded successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to refund booking");
        }
    }

    @GetMapping("/movie/{movieId}/count")
    public ResponseEntity<Long> getBookingCountByMovie(@PathVariable @NotNull Long movieId) {
        log.info("Getting booking count for movie: {}", movieId);
        Long count = bookingService.getBookingCountByMovie(movieId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/theater/{theaterId}/count")
    public ResponseEntity<Long> getBookingCountByTheater(@PathVariable @NotNull Long theaterId) {
        log.info("Getting booking count for theater: {}", theaterId);
        Long count = bookingService.getBookingCountByTheater(theaterId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/showtime/{showtimeId}/count")
    public ResponseEntity<Long> getBookingCountByShowtime(@PathVariable @NotNull Long showtimeId) {
        log.info("Getting booking count for showtime: {}", showtimeId);
        Long count = bookingService.getBookingCountByShowtime(showtimeId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/movie/{movieId}/revenue")
    public ResponseEntity<Double> getRevenueByMovie(@PathVariable @NotNull Long movieId) {
        log.info("Getting revenue for movie: {}", movieId);
        Double revenue = bookingService.getRevenueByMovie(movieId);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/theater/{theaterId}/revenue")
    public ResponseEntity<Double> getRevenueByTheater(@PathVariable @NotNull Long theaterId) {
        log.info("Getting revenue for theater: {}", theaterId);
        Double revenue = bookingService.getRevenueByTheater(theaterId);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/movie/{movieId}/seats")
    public ResponseEntity<Long> getTotalSeatsByMovie(@PathVariable @NotNull Long movieId) {
        log.info("Getting total seats booked for movie: {}", movieId);
        Long seats = bookingService.getTotalSeatsByMovie(movieId);
        return ResponseEntity.ok(seats);
    }

    @GetMapping("/theater/{theaterId}/seats")
    public ResponseEntity<Long> getTotalSeatsByTheater(@PathVariable @NotNull Long theaterId) {
        log.info("Getting total seats booked for theater: {}", theaterId);
        Long seats = bookingService.getTotalSeatsByTheater(theaterId);
        return ResponseEntity.ok(seats);
    }
}