package com.moviebooking.showtime.entity;
import com.moviebooking.showtime.enums.ShowStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "showtimes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long movieId;

    @Column(nullable = false)
    private Long theaterId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Integer availableSeats;

    @Column(nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShowStatus status = ShowStatus.ACTIVE;

    @Column(nullable = false)
    private String screenNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShowType showType;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    // Helper method to check if seats are available
    public boolean hasAvailableSeats(int requestedSeats) {
        return availableSeats >= requestedSeats;
    }

    // Helper method to reserve seats
    public void reserveSeats(int seatsToReserve) {
        if (hasAvailableSeats(seatsToReserve)) {
            this.availableSeats -= seatsToReserve;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Not enough available seats");
        }
    }

    // Helper method to release seats
    public void releaseSeats(int seatsToRelease) {
        this.availableSeats += seatsToRelease;
        if (this.availableSeats > this.totalSeats) {
            this.availableSeats = this.totalSeats;
        }
        this.updatedAt = LocalDateTime.now();
    }

    

    public enum ShowType {
        REGULAR_2D, IMAX_2D, REGULAR_3D, IMAX_3D, DOLBY_ATMOS
    }
}