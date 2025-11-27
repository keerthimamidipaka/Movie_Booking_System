package com.moviebooking.showtime.exception;

public class ShowtimeNotFoundException extends RuntimeException {

    public ShowtimeNotFoundException(String message) {
        super(message);
    }

    public ShowtimeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}