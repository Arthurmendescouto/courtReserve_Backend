package com.example.court_reserve.controller.request;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "BookingRequest", description = "Objeto de requisição para criar ou atualizar um agendamento.")
public record BookingRequest(
        @NotNull
        @Schema(description = "ID do usuário que está realizando o agendamento.", example = "2") // Ex: ID do Cliente
        Long userId,
        @NotNull
        @Schema(description = "ID da quadra a ser reservada.", example = "2")
        Long courtId,
        @NotNull
        @Schema(description = "Data e hora de início do agendamento. Deve ser anterior à data de fim.", example = "2026-02-14T10:00:00")
        LocalDateTime startDateTime,
        @NotNull
        @Schema(description = "Data e hora de término do agendamento. Deve ser posterior à data de início.", example = "2026-02-14T11:00:00")
        LocalDateTime endDateTime) {
}
