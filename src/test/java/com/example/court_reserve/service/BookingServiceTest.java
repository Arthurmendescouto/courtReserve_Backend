package com.example.court_reserve.service;

import com.example.court_reserve.controller.request.BookingRequest;
import com.example.court_reserve.entity.Booking;
import com.example.court_reserve.entity.Court;
import com.example.court_reserve.entity.User;
import com.example.court_reserve.repository.BookingRepository;
import com.example.court_reserve.repository.CourtRepository;
import com.example.court_reserve.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    // 1. Mocks: Os "dublês" das suas dependências
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CourtRepository courtRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    @DisplayName("Deve criar um agendamento com sucesso quando o horário estiver livre")
    void deveCriarAgendamentoComSucesso() {
        // --- 1. ARRANGE (Preparação) ---

        // Dados de entrada (Request)
        LocalDateTime inicio = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fim = inicio.plusHours(1);
        BookingRequest request = new BookingRequest(1L,1L,inicio,fim);

        // Objetos falsos que o banco devolveria
        Court courtFalsa = new Court();
        courtFalsa.setId(1L);
        User userFalso = new User();
        userFalso.setId(1L);

        // Treinando os dublês (Mocks)
        when(courtRepository.findById(1L)).thenReturn(Optional.of(courtFalsa));
        when(userRepository.findById(1L)).thenReturn(Optional.of(userFalso));

        // Aqui dizemos: "Não há conflito" (thenReturn false)
        // O -1L indica que é uma reserva nova
        when(bookingRepository.existsConflictExcludingId(1L, inicio, fim, -1L)).thenReturn(false);

        // Simulando o salvamento
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- 2. ACT (Ação) ---
        Booking resultado = bookingService.createBooking(request);

        // --- 3. ASSERT (Verificação) ---
        assertNotNull(resultado);
        assertEquals(1L, resultado.getCourt().getId());
        assertEquals(1L, resultado.getUser().getId());

        // Verifica se o save foi chamado exatamente 1 vez
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }
}
