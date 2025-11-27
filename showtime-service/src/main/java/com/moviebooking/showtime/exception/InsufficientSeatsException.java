package com.moviebooking.showtime.exception;

public class InsufficientSeatsException extends RuntimeException {

    public InsufficientSeatsException(String message) {
        super(message);
    }

    public InsufficientSeatsException(String message, Throwable cause) {
        super(message, cause);
    }
}