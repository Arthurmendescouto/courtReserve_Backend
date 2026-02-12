package com.example.court_reserve.strategy;

import com.example.court_reserve.entity.Court;

import java.time.Duration;
import java.time.LocalDateTime;

public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public double calculate(Court court, LocalDateTime start, LocalDateTime end) {
        long durationInMinutes = Duration.between(start, end).toMinutes();
        double durationInHours = durationInMinutes / 60.0;

        if (durationInHours == 0) durationInHours = 1.0;

        return court.getPricePerHour() * durationInHours;
    }
}