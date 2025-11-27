package com.moviebooking.ticket.exception;

public class SeatAlreadyBookedException extends RuntimeException {

    public SeatAlreadyBookedException(String message) {
        super(message);
    }

    public SeatAlreadyBookedException(String message, Throwable cause) {
        super(message, cause);
    }
}