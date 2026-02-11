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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(access = AccessLevel.PRIVATE) // Só a própria classe pode usar o Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA exige, mas protegemos de uso externo
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    @Schema(description = "Nome da quadra.", example = "Arena Futebol Clube")
    private String name;

    @Schema(description = "Tipo de esporte da quadra.")
    @Enumerated(EnumType.STRING)
    private SportType sportType;

    @Schema(description = "Preço por hora da quadra.", example = "120.00")
    private Double pricePerHour;

    @Schema(description = "Indica se a quadra está disponível para reserva.", example = "true")
    private boolean isAvailable;

    public static Court create(String name, SportType sportType, Double pricePerHour, boolean isAvailable) {
        Court court = Court.builder()
                .name(name)
                .sportType(sportType)
                .pricePerHour(pricePerHour)
                .isAvailable(isAvailable)
                .build();
        
        court.validate();
        return court;
    }

    public void update(String name, SportType sportType, Double pricePerHour, Boolean isAvailable) {
        if (name != null) this.name = name;
        if (sportType != null) this.sportType = sportType;
        if (pricePerHour != null) this.pricePerHour = pricePerHour;
        if (isAvailable != null) this.isAvailable = isAvailable;
        validate();
    }

    private void validate() {
        if (this.pricePerHour == null || this.pricePerHour < 0) {
            throw new IllegalArgumentException("O valor da hora da quadra não pode ser negativo ou nulo.");
        }

        if (this.sportType == null) {
            throw new IllegalArgumentException("O tipo de esporte é obrigatório para cadastrar uma quadra.");
        }

        if (this.name == null || this.name.isBlank()) {
            throw new IllegalArgumentException("O nome da quadra é obrigatório.");
        }
    }
}
