package com.moviebooking.ticket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticketNumber;

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private Long showtimeId;

    @Column(nullable = false)
    private Long movieId;

    @Column(nullable = false)
    private Long theaterId;

    @Column(nullable = false)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;

    @Column(nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.ACTIVE;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;

    @Column
    private String customerPhone;

    @Column(nullable = false)
    private LocalDateTime showDateTime;

    @Column(nullable = false)
    private LocalDateTime issueDate = LocalDateTime.now();

    @Column
    private LocalDateTime validUntil;

    @Column
    private String qrCode;

    @Column
    private String barcode;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    // Helper methods
    public boolean isExpired() {
        return validUntil != null && LocalDateTime.now().isAfter(validUntil);
    }

    public boolean isValidForEntry() {
        return status == TicketStatus.ACTIVE && !isExpired();
    }

    public void markAsUsed() {
        this.status = TicketStatus.USED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = TicketStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public enum TicketStatus {
        ACTIVE, USED, CANCELLED, EXPIRED, REFUNDED
    }

    public enum SeatType {
        REGULAR, PREMIUM, VIP, RECLINER, GOLD, PLATINUM
    }
}