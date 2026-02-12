package com.example.court_reserve.strategy;

import com.example.court_reserve.entity.Court;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class TennisPricingStrategy implements PricingStrategy {

    private final StandardPricingStrategy standardStrategy = new StandardPricingStrategy();

    @Override
    public double calculate(Court court, LocalDateTime start, LocalDateTime end) {
        double basePrice = standardStrategy.calculate(court, start, end);

        DayOfWeek day = start.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return basePrice * 1.5;
        }

        return basePrice;
    }
}