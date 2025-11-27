package com.moviebooking.showtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class ShowtimeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShowtimeServiceApplication.class, args);
    }
}