package com.moviebooking.ticket.service;

import com.moviebooking.ticket.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketService {

    Ticket createTicket(Ticket ticket);

    List<Ticket> createMultipleTickets(List<Ticket> tickets);

    Ticket updateTicket(Long id, Ticket ticket);

    Optional<Ticket> getTicketById(Long id);

    Optional<Ticket> getTicketByTicketNumber(String ticketNumber);

    List<Ticket> getTicketsByBooking(Long bookingId);

    List<Ticket> getTicketsByShowtime(Long showtimeId);

    List<Ticket> getTicketsByMovie(Long movieId);

    List<Ticket> getTicketsByTheater(Long theaterId);

    List<Ticket> getTicketsByCustomerEmail(String customerEmail);

    List<Ticket> getTicketsByCustomerPhone(String customerPhone);

    List<Ticket> getTicketsByStatus(Ticket.TicketStatus status);

    List<Ticket> getTicketsByCustomerEmailAndStatus(String email, Ticket.TicketStatus status);

    List<Ticket> getTicketsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<Ticket> getTicketsByMovieAndDateRange(Long movieId, LocalDateTime startDate, LocalDateTime endDate);

    List<Ticket> getTicketsByTheaterAndDateRange(Long theaterId, LocalDateTime startDate, LocalDateTime endDate);

    List<Ticket> getTicketsBySeatType(Ticket.SeatType seatType);

    Page<Ticket> getTicketsByStatusPaginated(Ticket.TicketStatus status, Pageable pageable);

    boolean isSeatAvailable(Long showtimeId, String seatNumber);

    void markTicketAsUsed(Long ticketId);

    void markTicketAsUsed(String ticketNumber);

    void cancelTicket(Long ticketId);

    void cancelTicket(String ticketNumber);

    void refundTicket(Long ticketId);

    void expireOldTickets();

    String generateTicketNumber();

    String generateQRCode(String ticketNumber);

    String generateBarcode(String ticketNumber);

    boolean validateTicket(String ticketNumber);

    Long getTicketCountByMovie(Long movieId);

    Long getTicketCountByTheater(Long theaterId);

    Long getTicketCountByShowtime(Long showtimeId);

    Double getRevenueByMovie(Long movieId);

    Double getRevenueByTheater(Long theaterId);
}