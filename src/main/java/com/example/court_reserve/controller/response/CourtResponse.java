package com.example.court_reserve.controller.response;

import com.example.court_reserve.entity.SportType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "CourtResponse", description = "Resposta com os dados de uma quadra.")
@Builder
public record CourtResponse(
        @Schema(description = "ID da quadra.", example = "1")
        Long id,
        @Schema(description = "Nome da quadra.", example = "Arena Futebol Clube")
        String name,
        @Schema(description = "Tipo de esporte da quadra.")
        SportType sportType,
        @Schema(description = "Preço por hora da quadra.", example = "120.00")
        Double pricePerHour,
        @Schema(description = "Indica se a quadra está disponível para reserva.", example = "true")
        Boolean isAvailable) {
}
