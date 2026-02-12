package com.example.court_reserve.strategy;

import com.example.court_reserve.entity.SportType;

public class PricingStrategyFactory {

    public static PricingStrategy getStrategy(SportType sportType) {
        if (sportType == null) return new StandardPricingStrategy();

        return switch (sportType) {
            case TENNIS -> new TennisPricingStrategy();
            // Se criarmos regras para FUTEBOL no futuro, basta adicionar aqui.
            default -> new StandardPricingStrategy();
        };
    }
}