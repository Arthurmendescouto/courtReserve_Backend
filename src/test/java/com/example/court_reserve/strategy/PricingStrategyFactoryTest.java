package com.example.court_reserve.strategy;

import com.example.court_reserve.entity.SportType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class PricingStrategyFactoryTest {

    @Test
    @DisplayName("Deve retornar TennisPricingStrategy para o esporte TENNIS")
    void deveRetornarEstrategiaDeTenis() {
        PricingStrategy strategy = PricingStrategyFactory.getStrategy(SportType.TENNIS);

        // Verifica se o objeto retornado é do tipo correto
        assertInstanceOf(TennisPricingStrategy.class, strategy);
    }

    @Test
    @DisplayName("Deve retornar StandardPricingStrategy para qualquer outro esporte")
    void deveRetornarEstrategiaPadrao() {
        PricingStrategy strategyFootball = PricingStrategyFactory.getStrategy(SportType.FOOTBALL);
        PricingStrategy strategyBasketball = PricingStrategyFactory.getStrategy(SportType.BASKETBALL);

        assertInstanceOf(StandardPricingStrategy.class, strategyFootball);
        assertInstanceOf(StandardPricingStrategy.class, strategyBasketball);
    }
}