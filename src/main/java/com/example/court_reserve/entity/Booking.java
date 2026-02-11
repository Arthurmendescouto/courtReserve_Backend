package com.example.court_reserve.entity;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(name = "Booking", description = "Entidade que representa um agendamento de quadra.")
@Entity
@Getter
@Setter
@NoArgsConstructor
// AllArgsConstructor e Builder privados garantem que o método .create() seja a única porta de entrada
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "booking")
public class Booking {

    @Schema(description = "Identificador único do agendamento.", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Data e hora de início do agendamento.", example = "2024-06-01T10:00:00")
    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Schema(description = "Data e hora de término do agendamento.", example = "2024-06-01T11:00:00")
    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Schema(description = "Quadra reservada para o agendamento.")
    @ManyToOne
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @Schema(description = "Usuário que realizou o agendamento.")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Schema(description = "Preço total da reserva.", example = "150.00")
    @Column(nullable = false)
    private Double totalPrice;


    public static Booking create(User user, Court court, LocalDateTime rawStart, LocalDateTime rawEnd) {
        if (user == null || court == null || rawStart == null || rawEnd == null) {
            throw new IllegalArgumentException("Todos os dados (usuário, quadra e datas) são obrigatórios.");
        }

        LocalDateTime start = rawStart.withSecond(0).withNano(0);
        LocalDateTime end = rawEnd.withSecond(0).withNano(0);

        if (start.isAfter(end) || start.isEqual(end)) {
            throw new IllegalArgumentException("A data de início deve ser anterior ao término.");
        }

        if (start.isBefore(LocalDateTime.now().withSecond(0).withNano(0))) {
            throw new IllegalArgumentException("Não é possível realizar agendamentos para o passado.");
        }

        double durationInHours = Duration.between(start, end).toMinutes() / 60.0;

        double finalPrice = court.getPricePerHour() * durationInHours;

        DayOfWeek day = start.getDayOfWeek();
        boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);

        if (court.getSportType() == SportType.TENNIS && isWeekend) {
            finalPrice *= 1.5;
        }

        return Booking.builder()
                .user(user)
                .court(court)
                .startDateTime(start)
                .endDateTime(end)
                .totalPrice(finalPrice)
                .build();
    }
}