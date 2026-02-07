package com.example.court_reserve.controller.request;

import com.example.court_reserve.entity.SportType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CourtRequest", description = "Objeto de requisição para criar ou atualizar uma quadra.")
public record CourtRequest(
        @Schema(description = "Nome da quadra.", example = "Arena Futebol Clube")
        String name,
        @Schema(description = "Tipo de esporte da quadra.")
        SportType sportType,
        @Schema(description = "Preço por hora da quadra.", example = "120.00")
        Double pricePerHour,
        @Schema(description = "Indica se a quadra está disponível para reserva.", example = "true")
        Boolean isAvailable) {
}
