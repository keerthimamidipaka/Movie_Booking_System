package com.moviebooking.showtime.exception;

public class ShowtimeConflictException extends RuntimeException {

    public ShowtimeConflictException(String message) {
        super(message);
    }

    public ShowtimeConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}