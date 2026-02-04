package com.example.court_reserve.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(name = "Court", description = "Entidade que representa uma quadra esportiva.")
@Entity
@Table(name = "court_reserve")
public class Court {
    @Schema(description = "Identificador único da quadra.", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Tipo de esporte da quadra.")
    @Enumerated(EnumType.STRING)
    private SportType sportType;

    @Schema(description = "Preço por hora da quadra.", example = "120.00")
    private Double pricePerHour;

    @Schema(description = "Indica se a quadra está disponível para reserva.", example = "true")
    private boolean isAvailable;

    public void validate() {
        if (this.pricePerHour == null || this.pricePerHour < 0) {
            throw new IllegalArgumentException("O valor da hora da quadra não pode ser negativo ou nulo.");
        }

        if (this.sportType == null) {
            throw new IllegalArgumentException("O tipo de esporte é obrigatório para cadastrar uma quadra.");
        }
    }
}
