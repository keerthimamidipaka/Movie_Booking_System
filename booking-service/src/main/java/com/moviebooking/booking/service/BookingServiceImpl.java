package com.moviebooking.booking.service;

import com.moviebooking.booking.enums.BookingStatus;
import com.moviebooking.booking.entity.Booking;
import com.moviebooking.booking.exception.BookingNotFoundException;
import com.moviebooking.booking.exception.BookingValidationException;
import com.moviebooking.booking.exception.BookingCancellationException;
import com.moviebooking.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private static final Double TAX_RATE = 0.18; // 18% GST
    private static final Double CONVENIENCE_FEE = 50.0;

    @Override
    public Booking createBooking(Booking booking) {
        log.info("Creating booking for customer: {}, movie: {}", booking.getCustomerEmail(), booking.getMovieId());

        // Validate booking
        if (!validateBooking(booking)) {
            throw new BookingValidationException("Invalid booking details");
        }

        // Generate booking reference
        booking.setBookingReference(generateBookingReference());

        // Calculate amounts
        Double totalAmount = calculateTotalAmount(booking.getSeatNumbers(), booking.getTotalAmount() / booking.getNumberOfSeats());
        Double taxAmount = calculateTaxAmount(totalAmount);
        Double finalAmount = calculateFinalAmount(totalAmount, taxAmount);

        booking.setTotalAmount(totalAmount);
        booking.setTaxAmount(taxAmount);
        booking.setFinalAmount(finalAmount);

        booking.setBookingDate(LocalDateTime.now());
        booking.setCreatedAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(Booking.PaymentStatus.PENDING);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBooking(Long id, Booking booking) {
        log.info("Updating booking with id: {}", id);
        Booking existingBooking = bookingRepository.findById(id)
            .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

        // Only allow updates to certain fields
        existingBooking.setCustomerName(booking.getCustomerName());
        existingBooking.setCustomerEmail(booking.getCustomerEmail());
        existingBooking.setCustomerPhone(booking.getCustomerPhone());
        existingBooking.setUpdatedAt(LocalDateTime.now());

        return bookingRepository.save(existingBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Booking> getBookingById(Long id) {
        log.info("Fetching booking with id: {}", id);
        return bookingRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Booking> getBookingByReference(String bookingReference) {
        log.info("Fetching booking with reference: {}", bookingReference);
        return bookingRepository.findByBookingReference(bookingReference);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByCustomerEmail(String customerEmail) {
        log.info("Fetching bookings for customer: {}", customerEmail);
        return bookingRepository.findByCustomerEmail(customerEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByCustomerPhone(String customerPhone) {
        log.info("Fetching bookings for phone: {}", customerPhone);
        return bookingRepository.findByCustomerPhone(customerPhone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByMovie(Long movieId) {
        log.info("Fetching bookings for movie: {}", movieId);
        return bookingRepository.findByMovieId(movieId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByTheater(Long theaterId) {
        log.info("Fetching bookings for theater: {}", theaterId);
        return bookingRepository.findByTheaterId(theaterId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByShowtime(Long showtimeId) {
        log.info("Fetching bookings for showtime: {}", showtimeId);
        return bookingRepository.findByShowtimeId(showtimeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        log.info("Fetching bookings with status: {}", status);
        return bookingRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByPaymentStatus(Booking.PaymentStatus paymentStatus) {
        log.info("Fetching bookings with payment status: {}", paymentStatus);
        return bookingRepository.findByPaymentStatus(paymentStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByCustomerAndStatus(String email, BookingStatus status) {
        log.info("Fetching bookings for customer: {} with status: {}", email, status);
        return bookingRepository.findByCustomerEmailAndStatus(email, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByCustomerAndPaymentStatus(String email, Booking.PaymentStatus paymentStatus) {
        log.info("Fetching bookings for customer: {} with payment status: {}", email, paymentStatus);
        return bookingRepository.findByCustomerEmailAndPaymentStatus(email, paymentStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching bookings between {} and {}", startDate, endDate);
        return bookingRepository.findByBookingDateBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByShowDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching bookings for shows between {} and {}", startDate, endDate);
        return bookingRepository.findByShowDateTimeBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByMovieAndStatus(Long movieId, BookingStatus status) {
        log.info("Fetching bookings for movie: {} with status: {}", movieId, status);
        return bookingRepository.findByMovieIdAndStatus(movieId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByTheaterAndStatus(Long theaterId,BookingStatus status) {
        log.info("Fetching bookings for theater: {} with status: {}", theaterId, status);
        return bookingRepository.findByTheaterIdAndStatus(theaterId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByShowtimeAndStatus(Long showtimeId, BookingStatus status) {
        log.info("Fetching bookings for showtime: {} with status: {}", showtimeId, status);
        return bookingRepository.findByShowtimeIdAndStatus(showtimeId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByPaymentMethod(Booking.PaymentMethod paymentMethod) {
        log.info("Fetching bookings with payment method: {}", paymentMethod);
        return bookingRepository.findByPaymentMethodAndPaymentStatus(paymentMethod, Booking.PaymentStatus.COMPLETED);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Booking> getBookingsByStatusPaginated(BookingStatus status, Pageable pageable) {
        log.info("Fetching paginated bookings with status: {}", status);
        return bookingRepository.findByStatusOrderByBookingDateDesc(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Booking> getBookingsByCustomerPaginated(String customerEmail, Pageable pageable) {
        log.info("Fetching paginated bookings for customer: {}", customerEmail);
        return bookingRepository.findByCustomerEmailOrderByBookingDateDesc(customerEmail, pageable);
    }

    @Override
    public boolean confirmBooking(Long bookingId, String paymentId) {
        log.info("Confirming booking: {} with payment: {}", bookingId, paymentId);
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        booking.setPaymentId(paymentId);
        booking.confirmBooking();
        bookingRepository.save(booking);
        return true;
    }

    @Override
    public boolean confirmBooking(String bookingReference, String paymentId) {
        log.info("Confirming booking: {} with payment: {}", bookingReference, paymentId);
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
            .orElseThrow(() -> new BookingNotFoundException("Booking not found with reference: " + bookingReference));

        booking.setPaymentId(paymentId);
        booking.confirmBooking();
        bookingRepository.save(booking);
        return true;
    }

    @Override
    public boolean cancelBooking(Long bookingId, String reason) {
        log.info("Cancelling booking: {} for reason: {}", bookingId, reason);
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.isCancellable()) {
            throw new BookingCancellationException("Booking cannot be cancelled. Check cancellation policy.");
        }

        booking.cancelBooking(reason);
        bookingRepository.save(booking);
        return true;
    }

    @Override
    public boolean cancelBooking(String bookingReference, String reason) {
        log.info("Cancelling booking: {} for reason: {}", bookingReference, reason);
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
            .orElseThrow(() -> new BookingNotFoundException("Booking not found with reference: " + bookingReference));

        if (!booking.isCancellable()) {
            throw new BookingCancellationException("Booking cannot be cancelled. Check cancellation policy.");
        }

        booking.cancelBooking(reason);
        bookingRepository.save(booking);
        return true;
    }

    @Override
    public boolean refundBooking(Long bookingId) {
        log.info("Refunding booking: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        booking.refundBooking();
        bookingRepository.save(booking);
        return true;
    }

    @Override
    @Scheduled(fixedRate = 1800000) // Run every 30 minutes
    public void expireOldBookings() {
        log.info("Running scheduled task to expire old bookings");
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(15); // 15 minute payment window
        List<Booking> expiredBookings = bookingRepository.findExpiredBookings(BookingStatus.PENDING, expireTime);

        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.EXPIRED);
            booking.setUpdatedAt(LocalDateTime.now());
        }

        if (!expiredBookings.isEmpty()) {
            bookingRepository.saveAll(expiredBookings);
            log.info("Expired {} bookings", expiredBookings.size());
        }
    }

    @Override
    public String generateBookingReference() {
        String prefix = "BKG";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + "-" + timestamp + "-" + uuid;
    }

    @Override
    public Double calculateTotalAmount(List<String> seatNumbers, Double basePrice) {
        Double totalAmount = seatNumbers.size() * basePrice;
        return totalAmount + CONVENIENCE_FEE;
    }

    @Override
    public Double calculateTaxAmount(Double totalAmount) {
        return totalAmount * TAX_RATE;
    }

    @Override
    public Double calculateFinalAmount(Double totalAmount, Double taxAmount) {
        return totalAmount + taxAmount;
    }

    @Override
    public boolean validateBooking(Booking booking) {
        // Basic validation
        if (booking.getCustomerEmail() == null || booking.getCustomerEmail().trim().isEmpty()) {
            return false;
        }
        if (booking.getMovieId() == null || booking.getTheaterId() == null || booking.getShowtimeId() == null) {
            return false;
        }
        if (booking.getSeatNumbers() == null || booking.getSeatNumbers().isEmpty()) {
            return false;
        }
        if (booking.getNumberOfSeats() != booking.getSeatNumbers().size()) {
            return false;
        }
        if (booking.getShowDateTime() == null || booking.getShowDateTime().isBefore(LocalDateTime.now())) {
            return false;
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getBookingCountByMovie(Long movieId) {
        log.info("Getting booking count for movie: {}", movieId);
        return bookingRepository.countByMovieIdAndStatus(movieId, BookingStatus.CONFIRMED);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getBookingCountByTheater(Long theaterId) {
        log.info("Getting booking count for theater: {}", theaterId);
        return bookingRepository.countByTheaterIdAndStatus(theaterId, BookingStatus.CONFIRMED);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getBookingCountByShowtime(Long showtimeId) {
        log.info("Getting booking count for showtime: {}", showtimeId);
        return bookingRepository.countByShowtimeIdAndStatus(showtimeId, BookingStatus.CONFIRMED);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getRevenueByMovie(Long movieId) {
        log.info("Getting revenue for movie: {}", movieId);
        Double revenue = bookingRepository.sumRevenueByMovieIdAndPaymentStatus(movieId, Booking.PaymentStatus.COMPLETED);
        return revenue != null ? revenue : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Double getRevenueByTheater(Long theaterId) {
        log.info("Getting revenue for theater: {}", theaterId);
        Double revenue = bookingRepository.sumRevenueByTheaterIdAndPaymentStatus(theaterId, Booking.PaymentStatus.COMPLETED);
        return revenue != null ? revenue : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalSeatsByMovie(Long movieId) {
        log.info("Getting total seats booked for movie: {}", movieId);
        Long seats = bookingRepository.sumSeatsByMovieIdAndStatus(movieId, BookingStatus.CONFIRMED);
        return seats != null ? seats : 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalSeatsByTheater(Long theaterId) {
        log.info("Getting total seats booked for theater: {}", theaterId);
        Long seats = bookingRepository.sumSeatsByTheaterIdAndStatus(theaterId, BookingStatus.CONFIRMED);
        return seats != null ? seats : 0L;
    }
}