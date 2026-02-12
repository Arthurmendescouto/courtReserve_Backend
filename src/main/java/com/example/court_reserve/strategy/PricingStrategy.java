package com.example.court_reserve.strategy;

import com.example.court_reserve.entity.Court;

import java.time.LocalDateTime;

public interface PricingStrategy {
    // O contrato: quem implementar isso deve saber calcular o preço
    double calculate(Court court, LocalDateTime start, LocalDateTime end);
}