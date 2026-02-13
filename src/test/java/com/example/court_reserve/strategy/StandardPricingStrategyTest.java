package com.example.court_reserve.strategy;

import com.example.court_reserve.entity.Court;
import com.example.court_reserve.entity.SportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StandardPricingStrategyTest {

    private StandardPricingStrategy strategy;
    private Court court;

    @BeforeEach
    void setUp() {
        strategy = new StandardPricingStrategy();
        // Cria uma quadra com preço base de R$ 100/hora para facilitar os cálculos
        court = Court.create("Quadra Padrão", SportType.FOOTBALL, 100.0, true);
    }

    @Test
    @DisplayName("Deve calcular o preço para 1 hora exatamente")
    void deveCalcularPrecoParaUmaHora() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        double price = strategy.calculate(court, start, end);
        assertEquals(100.0, price);
    }

    @Test
    @DisplayName("Deve calcular o preço proporcional para 1 hora e 30 minutos")
    void deveCalcularPrecoParaUmaHoraEMeia() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(90); // 1.5 horas

        double price = strategy.calculate(court, start, end);
        assertEquals(150.0, price);
    }
}