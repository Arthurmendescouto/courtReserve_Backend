package com.example.court_reserve.controller.response;

import java.time.LocalDateTime;

import com.example.court_reserve.entity.Role;
import com.example.court_reserve.entity.SportType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BookingResponse", description = "Resposta com os dados de um agendamento.")
public record BookingResponse(
        @Schema(description = "ID do agendamento.", example = "1")
        Long id,
        @Schema(description = "Data e hora de início do agendamento.", example = "2024-06-01T10:00:00")
        LocalDateTime startDateTime,
        @Schema(description = "Data e hora de término do agendamento.", example = "2024-06-01T11:00:00")
        LocalDateTime endDateTime,
        @Schema(description = "Preço total calculado para a reserva.", example = "150.00")
        Double totalPrice,
        @Schema(description = "Informações do usuário que fez o agendamento.")
        UserInfo user,
        @Schema(description = "Informações da quadra reservada.")
        CourtInfo court
) {
    @Schema(name = "UserInfo", description = "Informações do usuário na resposta de agendamento.")
    public record UserInfo(
            @Schema(description = "ID do usuário.", example = "1")
            Long id,
            @Schema(description = "Nome do usuário.", example = "João Silva")
            String name,
            @Schema(description = "E-mail do usuário.", example = "joao@email.com")
            String email,
            @Schema(description = "Perfil de acesso do usuário.", example = "CLIENT")
            Role role){}

    @Schema(name = "CourtInfo", description = "Informações da quadra na resposta de agendamento.")
    public record CourtInfo(
            @Schema(description = "ID da quadra.", example = "2")
            Long id,
            @Schema(description = "Nome da quadra.", example = "Arena 1")
            String name,
            @Schema(description = "Tipo de esporte da quadra.")
            SportType sportType,
            @Schema(description = "Preço por hora da quadra.", example = "120.00")
            Double pricePerHour){}
}
