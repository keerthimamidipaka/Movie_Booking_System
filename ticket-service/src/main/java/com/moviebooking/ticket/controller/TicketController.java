package com.moviebooking.ticket.controller;

import com.moviebooking.ticket.entity.Ticket;
import com.moviebooking.ticket.service.TicketService;
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
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody Ticket ticket) {
        log.info("Creating ticket for showtime: {}", ticket.getShowtimeId());
        Ticket createdTicket = ticketService.createTicket(ticket);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Ticket>> createMultipleTickets(@Valid @RequestBody List<Ticket> tickets) {
        log.info("Creating {} tickets", tickets.size());
        List<Ticket> createdTickets = ticketService.createMultipleTickets(tickets);
        return new ResponseEntity<>(createdTickets, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @Valid @RequestBody Ticket ticket) {
        log.info("Updating ticket with id: {}", id);
        Ticket updatedTicket = ticketService.updateTicket(id, ticket);
        return ResponseEntity.ok(updatedTicket);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        log.info("Fetching ticket with id: {}", id);
        Optional<Ticket> ticket = ticketService.getTicketById(id);
        return ticket.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{ticketNumber}")
    public ResponseEntity<Ticket> getTicketByTicketNumber(@PathVariable @NotBlank String ticketNumber) {
        log.info("Fetching ticket with number: {}", ticketNumber);
        Optional<Ticket> ticket = ticketService.getTicketByTicketNumber(ticketNumber);
        return ticket.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<Ticket>> getTicketsByBooking(@PathVariable @NotNull Long bookingId) {
        log.info("Fetching tickets for booking: {}", bookingId);
        List<Ticket> tickets = ticketService.getTicketsByBooking(bookingId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/showtime/{showtimeId}")
    public ResponseEntity<List<Ticket>> getTicketsByShowtime(@PathVariable @NotNull Long showtimeId) {
        log.info("Fetching tickets for showtime: {}", showtimeId);
        List<Ticket> tickets = ticketService.getTicketsByShowtime(showtimeId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Ticket>> getTicketsByMovie(@PathVariable @NotNull Long movieId) {
        log.info("Fetching tickets for movie: {}", movieId);
        List<Ticket> tickets = ticketService.getTicketsByMovie(movieId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/theater/{theaterId}")
    public ResponseEntity<List<Ticket>> getTicketsByTheater(@PathVariable @NotNull Long theaterId) {
        log.info("Fetching tickets for theater: {}", theaterId);
        List<Ticket> tickets = ticketService.getTicketsByTheater(theaterId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/customer/email/{email}")
    public ResponseEntity<List<Ticket>> getTicketsByCustomerEmail(@PathVariable @NotBlank String email) {
        log.info("Fetching tickets for customer email: {}", email);
        List<Ticket> tickets = ticketService.getTicketsByCustomerEmail(email);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/customer/phone/{phone}")
    public ResponseEntity<List<Ticket>> getTicketsByCustomerPhone(@PathVariable @NotBlank String phone) {
        log.info("Fetching tickets for customer phone: {}", phone);
        List<Ticket> tickets = ticketService.getTicketsByCustomerPhone(phone);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Ticket>> getTicketsByStatus(@PathVariable Ticket.TicketStatus status) {
        log.info("Fetching tickets with status: {}", status);
        List<Ticket> tickets = ticketService.getTicketsByStatus(status);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/status/{status}/paginated")
    public ResponseEntity<Page<Ticket>> getTicketsByStatusPaginated(
            @PathVariable Ticket.TicketStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Fetching paginated tickets with status: {} - page: {}, size: {}", status, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Ticket> tickets = ticketService.getTicketsByStatusPaginated(status, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/customer/{email}/status/{status}")
    public ResponseEntity<List<Ticket>> getTicketsByCustomerEmailAndStatus(
            @PathVariable @NotBlank String email,
            @PathVariable Ticket.TicketStatus status) {
        log.info("Fetching tickets for customer: {} with status: {}", email, status);
        List<Ticket> tickets = ticketService.getTicketsByCustomerEmailAndStatus(email, status);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Ticket>> getTicketsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching tickets between {} and {}", startDate, endDate);
        List<Ticket> tickets = ticketService.getTicketsByDateRange(startDate, endDate);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/movie/{movieId}/date-range")
    public ResponseEntity<List<Ticket>> getTicketsByMovieAndDateRange(
            @PathVariable @NotNull Long movieId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching tickets for movie: {} between {} and {}", movieId, startDate, endDate);
        List<Ticket> tickets = ticketService.getTicketsByMovieAndDateRange(movieId, startDate, endDate);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/theater/{theaterId}/date-range")
    public ResponseEntity<List<Ticket>> getTicketsByTheaterAndDateRange(
            @PathVariable @NotNull Long theaterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching tickets for theater: {} between {} and {}", theaterId, startDate, endDate);
        List<Ticket> tickets = ticketService.getTicketsByTheaterAndDateRange(theaterId, startDate, endDate);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/seat-type/{seatType}")
    public ResponseEntity<List<Ticket>> getTicketsBySeatType(@PathVariable Ticket.SeatType seatType) {
        log.info("Fetching tickets for seat type: {}", seatType);
        List<Ticket> tickets = ticketService.getTicketsBySeatType(seatType);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/seat-availability")
    public ResponseEntity<Boolean> isSeatAvailable(
            @RequestParam @NotNull Long showtimeId,
            @RequestParam @NotBlank String seatNumber) {
        log.info("Checking seat availability for showtime: {}, seat: {}", showtimeId, seatNumber);
        boolean available = ticketService.isSeatAvailable(showtimeId, seatNumber);
        return ResponseEntity.ok(available);
    }

    @PatchMapping("/{id}/use")
    public ResponseEntity<String> markTicketAsUsed(@PathVariable Long id) {
        log.info("Marking ticket as used: {}", id);
        ticketService.markTicketAsUsed(id);
        return ResponseEntity.ok("Ticket marked as used successfully");
    }

    @PatchMapping("/number/{ticketNumber}/use")
    public ResponseEntity<String> markTicketAsUsedByNumber(@PathVariable @NotBlank String ticketNumber) {
        log.info("Marking ticket as used: {}", ticketNumber);
        ticketService.markTicketAsUsed(ticketNumber);
        return ResponseEntity.ok("Ticket marked as used successfully");
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<String> cancelTicket(@PathVariable Long id) {
        log.info("Cancelling ticket: {}", id);
        ticketService.cancelTicket(id);
        return ResponseEntity.ok("Ticket cancelled successfully");
    }

    @PatchMapping("/number/{ticketNumber}/cancel")
    public ResponseEntity<String> cancelTicketByNumber(@PathVariable @NotBlank String ticketNumber) {
        log.info("Cancelling ticket: {}", ticketNumber);
        ticketService.cancelTicket(ticketNumber);
        return ResponseEntity.ok("Ticket cancelled successfully");
    }

    @PatchMapping("/{id}/refund")
    public ResponseEntity<String> refundTicket(@PathVariable Long id) {
        log.info("Refunding ticket: {}", id);
        ticketService.refundTicket(id);
        return ResponseEntity.ok("Ticket refunded successfully");
    }

    @GetMapping("/validate/{ticketNumber}")
    public ResponseEntity<Boolean> validateTicket(@PathVariable @NotBlank String ticketNumber) {
        log.info("Validating ticket: {}", ticketNumber);
        boolean valid = ticketService.validateTicket(ticketNumber);
        return ResponseEntity.ok(valid);
    }

    @GetMapping("/movie/{movieId}/count")
    public ResponseEntity<Long> getTicketCountByMovie(@PathVariable @NotNull Long movieId) {
        log.info("Getting ticket count for movie: {}", movieId);
        Long count = ticketService.getTicketCountByMovie(movieId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/theater/{theaterId}/count")
    public ResponseEntity<Long> getTicketCountByTheater(@PathVariable @NotNull Long theaterId) {
        log.info("Getting ticket count for theater: {}", theaterId);
        Long count = ticketService.getTicketCountByTheater(theaterId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/showtime/{showtimeId}/count")
    public ResponseEntity<Long> getTicketCountByShowtime(@PathVariable @NotNull Long showtimeId) {
        log.info("Getting ticket count for showtime: {}", showtimeId);
        Long count = ticketService.getTicketCountByShowtime(showtimeId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/movie/{movieId}/revenue")
    public ResponseEntity<Double> getRevenueByMovie(@PathVariable @NotNull Long movieId) {
        log.info("Getting revenue for movie: {}", movieId);
        Double revenue = ticketService.getRevenueByMovie(movieId);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/theater/{theaterId}/revenue")
    public ResponseEntity<Double> getRevenueByTheater(@PathVariable @NotNull Long theaterId) {
        log.info("Getting revenue for theater: {}", theaterId);
        Double revenue = ticketService.getRevenueByTheater(theaterId);
        return ResponseEntity.ok(revenue);
    }
}