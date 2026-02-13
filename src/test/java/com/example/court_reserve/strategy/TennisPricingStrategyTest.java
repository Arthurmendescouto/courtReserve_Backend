package com.example.court_reserve.strategy;

import com.example.court_reserve.entity.Court;
import com.example.court_reserve.entity.SportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TennisPricingStrategyTest {

    private TennisPricingStrategy strategy;
    private Court tennisCourt;

    @BeforeEach
    void setUp() {
        strategy = new TennisPricingStrategy();
        // Quadra de Tênis com preço base R$ 100/hora
        tennisCourt = Court.create("Quadra de Tênis", SportType.TENNIS, 100.0, true);
    }

    @Test
    @DisplayName("Deve cobrar preço normal para Tênis em dia de semana")
    void deveCobrarPrecoNormalEmDiaDeSemana() {
        // Garante que o teste rode em uma segunda-feira futura
        LocalDateTime start = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(10);
        LocalDateTime end = start.plusHours(1);

        double price = strategy.calculate(tennisCourt, start, end);

        // Preço base, sem acréscimo
        assertEquals(100.0, price);
    }

    @Test
    @DisplayName("Deve aplicar acréscimo de 50% para Tênis no Sábado")
    void deveAplicarAcrescimoNoSabado() {
        // Garante que o teste rode em um sábado futuro
        LocalDateTime start = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY)).withHour(10);
        LocalDateTime end = start.plusHours(1);

        double price = strategy.calculate(tennisCourt, start, end);

        // Preço com 50% de acréscimo
        assertEquals(150.0, price);
    }
}