package com.moviebooking.booking.entity;


import com.moviebooking.booking.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bookingReference;

    @Column(nullable = false)
    private Long movieId;

    @Column(nullable = false)
    private Long theaterId;

    @Column(nullable = false)
    private Long showtimeId;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;

    @Column
    private String customerPhone;

    @Column(nullable = false)
    private Integer numberOfSeats;

    @ElementCollection
    @CollectionTable(name = "booking_seats", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "seat_number")
    private List<String> seatNumbers;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private Double taxAmount;

    @Column(nullable = false)
    private Double finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column
    private String paymentId;

    @Column
    private String paymentGateway;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private LocalDateTime showDateTime;

    @Column(nullable = false)
    private LocalDateTime bookingDate = LocalDateTime.now();

    @Column
    private LocalDateTime paymentDate;

    @Column
    private LocalDateTime cancellationDate;

    @Column
    private String cancellationReason;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    // Helper methods
    public boolean isPaid() {
        return paymentStatus == PaymentStatus.COMPLETED;
    }

    public boolean isCancellable() {
        return status == BookingStatus.CONFIRMED && 
               showDateTime.isAfter(LocalDateTime.now().plusHours(2)); // 2 hour cancellation policy
    }

    public void confirmBooking() {
        this.status = BookingStatus.CONFIRMED;
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.paymentDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void cancelBooking(String reason) {
        this.status = BookingStatus.CANCELLED;
        this.cancellationDate = LocalDateTime.now();
        this.cancellationReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public void refundBooking() {
        this.paymentStatus = PaymentStatus.REFUNDED;
        this.updatedAt = LocalDateTime.now();
    }

    

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, REFUNDED, PARTIAL_REFUND
    }

    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, NET_BANKING, UPI, WALLET, CASH
    }
}
