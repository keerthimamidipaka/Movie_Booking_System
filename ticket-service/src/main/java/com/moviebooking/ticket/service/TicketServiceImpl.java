package com.moviebooking.ticket.service;

import com.moviebooking.ticket.entity.Ticket;
import com.moviebooking.ticket.exception.TicketNotFoundException;
import com.moviebooking.ticket.exception.SeatAlreadyBookedException;
import com.moviebooking.ticket.exception.TicketValidationException;
import com.moviebooking.ticket.repository.TicketRepository;
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
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Override
    public Ticket createTicket(Ticket ticket) {
        log.info("Creating ticket for showtime: {}, seat: {}", ticket.getShowtimeId(), ticket.getSeatNumber());

        // Check if seat is already booked
        if (!isSeatAvailable(ticket.getShowtimeId(), ticket.getSeatNumber())) {
            throw new SeatAlreadyBookedException("Seat " + ticket.getSeatNumber() + " is already booked for this showtime");
        }

        // Generate ticket number and codes
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setQrCode(generateQRCode(ticket.getTicketNumber()));
        ticket.setBarcode(generateBarcode(ticket.getTicketNumber()));

        // Set validity period (usually until show start time)
        ticket.setValidUntil(ticket.getShowDateTime());

        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setStatus(Ticket.TicketStatus.ACTIVE);

        return ticketRepository.save(ticket);
    }

    @Override
    public List<Ticket> createMultipleTickets(List<Ticket> tickets) {
        log.info("Creating {} tickets", tickets.size());

        // Validate all seats are available before creating any tickets
        for (Ticket ticket : tickets) {
            if (!isSeatAvailable(ticket.getShowtimeId(), ticket.getSeatNumber())) {
                throw new SeatAlreadyBookedException("Seat " + ticket.getSeatNumber() + " is already booked");
            }
        }

        // Create all tickets
        for (Ticket ticket : tickets) {
            ticket.setTicketNumber(generateTicketNumber());
            ticket.setQrCode(generateQRCode(ticket.getTicketNumber()));
            ticket.setBarcode(generateBarcode(ticket.getTicketNumber()));
            ticket.setValidUntil(ticket.getShowDateTime());
            ticket.setCreatedAt(LocalDateTime.now());
            ticket.setStatus(Ticket.TicketStatus.ACTIVE);
        }

        return ticketRepository.saveAll(tickets);
    }

    @Override
    public Ticket updateTicket(Long id, Ticket ticket) {
        log.info("Updating ticket with id: {}", id);
        Ticket existingTicket = ticketRepository.findById(id)
            .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));

        // Only allow updates to certain fields
        existingTicket.setCustomerName(ticket.getCustomerName());
        existingTicket.setCustomerEmail(ticket.getCustomerEmail());
        existingTicket.setCustomerPhone(ticket.getCustomerPhone());
        existingTicket.setUpdatedAt(LocalDateTime.now());

        return ticketRepository.save(existingTicket);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ticket> getTicketById(Long id) {
        log.info("Fetching ticket with id: {}", id);
        return ticketRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ticket> getTicketByTicketNumber(String ticketNumber) {
        log.info("Fetching ticket with number: {}", ticketNumber);
        return ticketRepository.findByTicketNumber(ticketNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByBooking(Long bookingId) {
        log.info("Fetching tickets for booking: {}", bookingId);
        return ticketRepository.findByBookingId(bookingId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByShowtime(Long showtimeId) {
        log.info("Fetching tickets for showtime: {}", showtimeId);
        return ticketRepository.findByShowtimeId(showtimeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByMovie(Long movieId) {
        log.info("Fetching tickets for movie: {}", movieId);
        return ticketRepository.findByMovieId(movieId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByTheater(Long theaterId) {
        log.info("Fetching tickets for theater: {}", theaterId);
        return ticketRepository.findByTheaterId(theaterId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByCustomerEmail(String customerEmail) {
        log.info("Fetching tickets for customer email: {}", customerEmail);
        return ticketRepository.findByCustomerEmail(customerEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByCustomerPhone(String customerPhone) {
        log.info("Fetching tickets for customer phone: {}", customerPhone);
        return ticketRepository.findByCustomerPhone(customerPhone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByStatus(Ticket.TicketStatus status) {
        log.info("Fetching tickets with status: {}", status);
        return ticketRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByCustomerEmailAndStatus(String email, Ticket.TicketStatus status) {
        log.info("Fetching tickets for customer: {} with status: {}", email, status);
        return ticketRepository.findByCustomerEmailAndStatus(email, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching tickets between {} and {}", startDate, endDate);
        return ticketRepository.findByShowDateTimeBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByMovieAndDateRange(Long movieId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching tickets for movie: {} between {} and {}", movieId, startDate, endDate);
        return ticketRepository.findByMovieIdAndShowDateTimeBetween(movieId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByTheaterAndDateRange(Long theaterId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching tickets for theater: {} between {} and {}", theaterId, startDate, endDate);
        return ticketRepository.findByTheaterIdAndShowDateTimeBetween(theaterId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsBySeatType(Ticket.SeatType seatType) {
        log.info("Fetching tickets for seat type: {}", seatType);
        return ticketRepository.findBySeatTypeAndStatus(seatType, Ticket.TicketStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Ticket> getTicketsByStatusPaginated(Ticket.TicketStatus status, Pageable pageable) {
        log.info("Fetching paginated tickets with status: {}", status);
        return ticketRepository.findByStatusOrderByShowDateTimeDesc(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSeatAvailable(Long showtimeId, String seatNumber) {
        log.info("Checking seat availability for showtime: {}, seat: {}", showtimeId, seatNumber);
        Optional<Ticket> existingTicket = ticketRepository.findByShowtimeIdAndSeatNumber(showtimeId, seatNumber);
        return existingTicket.isEmpty() || 
               existingTicket.get().getStatus() == Ticket.TicketStatus.CANCELLED ||
               existingTicket.get().getStatus() == Ticket.TicketStatus.REFUNDED;
    }

    @Override
    public void markTicketAsUsed(Long ticketId) {
        log.info("Marking ticket as used: {}", ticketId);
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + ticketId));

        if (!ticket.isValidForEntry()) {
            throw new TicketValidationException("Ticket is not valid for entry");
        }

        ticket.markAsUsed();
        ticketRepository.save(ticket);
    }

    @Override
    public void markTicketAsUsed(String ticketNumber) {
        log.info("Marking ticket as used: {}", ticketNumber);
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
            .orElseThrow(() -> new TicketNotFoundException("Ticket not found with number: " + ticketNumber));

        if (!ticket.isValidForEntry()) {
            throw new TicketValidationException("Ticket is not valid for entry");
        }

        ticket.markAsUsed();
        ticketRepository.save(ticket);
    }

    @Override
    public void cancelTicket(Long ticketId) {
        log.info("Cancelling ticket: {}", ticketId);
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + ticketId));

        ticket.cancel();
        ticketRepository.save(ticket);
    }

    @Override
    public void cancelTicket(String ticketNumber) {
        log.info("Cancelling ticket: {}", ticketNumber);
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
            .orElseThrow(() -> new TicketNotFoundException("Ticket not found with number: " + ticketNumber));

        ticket.cancel();
        ticketRepository.save(ticket);
    }

    @Override
    public void refundTicket(Long ticketId) {
        log.info("Refunding ticket: {}", ticketId);
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + ticketId));

        ticket.setStatus(Ticket.TicketStatus.REFUNDED);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
    }

    @Override
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void expireOldTickets() {
        log.info("Running scheduled task to expire old tickets");
        LocalDateTime currentTime = LocalDateTime.now();
        List<Ticket> expiredTickets = ticketRepository.findExpiredTickets(currentTime, Ticket.TicketStatus.ACTIVE);

        for (Ticket ticket : expiredTickets) {
            ticket.setStatus(Ticket.TicketStatus.EXPIRED);
            ticket.setUpdatedAt(currentTime);
        }

        if (!expiredTickets.isEmpty()) {
            ticketRepository.saveAll(expiredTickets);
            log.info("Expired {} tickets", expiredTickets.size());
        }
    }

    @Override
    public String generateTicketNumber() {
        String prefix = "TKT";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + timestamp + "-" + uuid;
    }

    @Override
    public String generateQRCode(String ticketNumber) {
        // In real implementation, generate actual QR code
        return "QR-" + ticketNumber;
    }

    @Override
    public String generateBarcode(String ticketNumber) {
        // In real implementation, generate actual barcode
        return "BC-" + ticketNumber.replace("-", "");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateTicket(String ticketNumber) {
        log.info("Validating ticket: {}", ticketNumber);
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketNumber(ticketNumber);

        if (ticketOpt.isEmpty()) {
            return false;
        }

        Ticket ticket = ticketOpt.get();
        return ticket.isValidForEntry();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTicketCountByMovie(Long movieId) {
        log.info("Getting ticket count for movie: {}", movieId);
        return ticketRepository.countByMovieIdAndStatus(movieId, Ticket.TicketStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTicketCountByTheater(Long theaterId) {
        log.info("Getting ticket count for theater: {}", theaterId);
        return ticketRepository.countByTheaterIdAndStatus(theaterId, Ticket.TicketStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTicketCountByShowtime(Long showtimeId) {
        log.info("Getting ticket count for showtime: {}", showtimeId);
        return ticketRepository.countByShowtimeIdAndStatus(showtimeId, Ticket.TicketStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getRevenueByMovie(Long movieId) {
        log.info("Getting revenue for movie: {}", movieId);
        Double revenue = ticketRepository.sumPriceByMovieIdAndStatus(movieId, Ticket.TicketStatus.ACTIVE);
        return revenue != null ? revenue : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Double getRevenueByTheater(Long theaterId) {
        log.info("Getting revenue for theater: {}", theaterId);
        Double revenue = ticketRepository.sumPriceByTheaterIdAndStatus(theaterId, Ticket.TicketStatus.ACTIVE);
        return revenue != null ? revenue : 0.0;
    }
}