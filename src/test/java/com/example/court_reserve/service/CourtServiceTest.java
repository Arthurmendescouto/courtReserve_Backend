package com.example.court_reserve.service;

import com.example.court_reserve.controller.request.CourtRequest;
import com.example.court_reserve.entity.Court;
import com.example.court_reserve.entity.SportType;
import com.example.court_reserve.repository.CourtRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourtServiceTest {

    @Mock
    private CourtRepository courtRepository;

    @InjectMocks
    private CourtService courtService;

    @Test
    @DisplayName("Deve criar quadra com sucesso")
    void deveCriarQuadra() {
        Court courtSalva = Court.create("Quadra 1", SportType.FOOTBALL, 100.0, true);
        courtSalva.setId(1L);

        when(courtRepository.save(any(Court.class))).thenReturn(courtSalva);

        Court courtParaSalvar = Court.create("Quadra 1", SportType.FOOTBALL, 100.0, true);

        Court resultado = courtService.save(courtParaSalvar);

        assertNotNull(resultado);
        verify(courtRepository, times(1)).save(any(Court.class));
    }

    @Test
    @DisplayName("Deve atualizar quadra com sucesso")
    void deveAtualizarQuadra() {
        Long id = 1L;
        CourtRequest request = new CourtRequest("Nome Novo", SportType.TENNIS, 150.0, true);
        Court courtExistente = Court.create("Nome Antigo", SportType.FOOTBALL, 100.0, true);
        courtExistente.setId(id);

        when(courtRepository.findById(id)).thenReturn(Optional.of(courtExistente));
        when(courtRepository.save(any(Court.class))).thenAnswer(i -> i.getArguments()[0]);

        Court atualizada = courtService.updateCourt(id, request);

        assertEquals("Nome Novo", atualizada.getName());
        assertEquals(SportType.TENNIS, atualizada.getSportType());
        verify(courtRepository).save(courtExistente);
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar quadra inexistente")
    void deveFalharAoAtualizarQuadraInexistente() {
        Long id = 99L;
        CourtRequest request = new CourtRequest("Nome", SportType.FOOTBALL, 100.0, true);

        when(courtRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            courtService.updateCourt(id, request);
        });

        verify(courtRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar quadra com sucesso")
    void deveDeletarQuadra() {
        Long id = 1L;
        when(courtRepository.existsById(id)).thenReturn(true);

        courtService.delete(id);

        verify(courtRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar deletar quadra inexistente")
    void deveFalharAoDeletarQuadraInexistente() {
        Long id = 99L;
        when(courtRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> courtService.delete(id));
        verify(courtRepository, never()).deleteById(any());
    }
}