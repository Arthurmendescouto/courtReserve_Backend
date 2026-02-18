package com.example.court_reserve.service;

import com.example.court_reserve.controller.request.BookingRequest;
import com.example.court_reserve.entity.*;
import com.example.court_reserve.repository.BookingRepository;
import com.example.court_reserve.repository.CourtRepository;
import com.example.court_reserve.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CourtRepository courtRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Court court;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        // Configuração padrão que pode ser usada por múltiplos testes
        start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        end = start.plusHours(1);

        user = User.create("Usuario Teste", "teste@email.com", "123456", Role.CLIENT);
        user.setId(1L);

        court = Court.create("Quadra Teste", SportType.FOOTBALL, 100.0, true);
        court.setId(1L);

        // Usamos lenient() para indicar que este stub pode não ser usado em todos os testes.
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("Deve criar um agendamento com sucesso quando o horário estiver livre")
    void deveCriarAgendamentoComSucesso() {
        BookingRequest request = new BookingRequest(1L, 1L, start, end);

        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        when(bookingRepository.existsConflictExcludingId(1L, start, end, -1L)).thenReturn(false);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking resultado = bookingService.createBooking(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getCourt().getId());
        assertEquals(1L, resultado.getUser().getId());
        assertEquals(100.0, resultado.getTotalPrice()); // Validamos se o preço foi calculado

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("Deve lançar erro quando houver conflito de horário")
    void deveLancarErroQuandoHouverConflito() {
        BookingRequest request = new BookingRequest(1L, 1L, start, end);

        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        when(bookingRepository.existsConflictExcludingId(1L, start, end, -1L)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(request);
        });

        assertEquals("Já existe reserva para este horário nesta quadra.", exception.getMessage());

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Deve calcular acréscimo de 50% no preço para Tênis no fim de semana")
    void deveCalcularPrecoComAcrescimoNoFimDeSemanaParaTenis() {
        // Busca o próximo sábado a partir de hoje para garantir que seja no futuro
        LocalDateTime inicio = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY)).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fim = inicio.plusHours(1);
        BookingRequest request = new BookingRequest(1L, 1L, inicio, fim);

        // Quadra de Tênis com preço base 100.0
        Court courtTenis = Court.create("Quadra Tênis", SportType.TENNIS, 100.0, true);
        courtTenis.setId(1L);

        when(courtRepository.findById(1L)).thenReturn(Optional.of(courtTenis));
        when(bookingRepository.existsConflictExcludingId(1L, inicio, fim, -1L)).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        Booking resultado = bookingService.createBooking(request);

        assertEquals(150.0, resultado.getTotalPrice()); // 100.0 + 50% = 150.0
    }

    @Test
    @DisplayName("Deve calcular preço normal para Tênis em dia de semana")
    void deveCalcularPrecoNormalParaTenisEmDiaDeSemana() {
        LocalDateTime inicio = LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fim = inicio.plusHours(1);
        BookingRequest request = new BookingRequest(1L, 1L, inicio, fim);

        Court courtTenis = Court.create("Quadra Tênis", SportType.TENNIS, 100.0, true);
        courtTenis.setId(1L);

        when(courtRepository.findById(1L)).thenReturn(Optional.of(courtTenis));
        when(bookingRepository.existsConflictExcludingId(1L, inicio, fim, -1L)).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        Booking resultado = bookingService.createBooking(request);

        assertEquals(100.0, resultado.getTotalPrice()); // Preço base, sem acréscimo
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar agendar para uma quadra inexistente")
    void deveLancarErroParaQuadraInexistente() {
        BookingRequest request = new BookingRequest(1L, 99L, start, end); // ID 99 não existe

        when(courtRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            bookingService.createBooking(request);
        });

        assertEquals("Quadra não encontrada.", exception.getMessage());
    }
}
