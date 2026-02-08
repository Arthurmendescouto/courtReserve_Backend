package com.example.court_reserve.service;

import com.example.court_reserve.controller.request.BookingRequest;
import com.example.court_reserve.entity.Booking;
import com.example.court_reserve.entity.Court;
import com.example.court_reserve.entity.User;
import com.example.court_reserve.repository.BookingRepository;
import com.example.court_reserve.repository.CourtRepository;
import com.example.court_reserve.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;

    public Page<Booking> findAll(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking createBooking(BookingRequest request) {
        // 1. Validações e Limpeza de Data (Zera segundos e milissegundos para comparação literal)
        if (request.startDateTime() == null || request.endDateTime() == null) {
            throw new IllegalArgumentException("Datas são obrigatórias.");
        }

        LocalDateTime start = request.startDateTime().withSecond(0).withNano(0);
        LocalDateTime end = request.endDateTime().withSecond(0).withNano(0);

        if (start.isAfter(end) || start.isEqual(end)) {
            throw new IllegalArgumentException("Data de início deve ser anterior ao fim.");
        }

        // Comparamos com o 'agora' também zerado para evitar erros de milésimos no envio do JSON
        if (start.isBefore(LocalDateTime.now().withSecond(0).withNano(0))) {
            throw new IllegalArgumentException("Não é possível agendar no passado.");
        }

        // 2. Buscar Entidades
        Court court = courtRepository.findById(request.courtId())
                .orElseThrow(() -> new EntityNotFoundException("Quadra não encontrada."));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        // 3. Verificar Conflito de Horário (Usando as datas limpas e excluindo ID -1)
        boolean hasConflict = bookingRepository.existsConflictExcludingId(
                court.getId(), start, end, -1L);

        if (hasConflict) {
            throw new IllegalArgumentException("Já existe reserva para este horário nesta quadra.");
        }

        // 4. Salvar (Salvando os valores zerados para garantir consistência no banco)
        Booking booking = Booking.builder()
                .court(court)
                .user(user)
                .startDateTime(start)
                .endDateTime(end)
                .build();

        return bookingRepository.save(booking);
    }

    @Transactional
    public void delete(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new EmptyResultDataAccessException("Reserva não encontrada com o ID: " + id, 1);
        }
        bookingRepository.deleteById(id);
    }

    @Transactional
    public Booking update(Long id, BookingRequest request) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada com o ID: " + id));

        // Limpa as datas vindo do request ou usa as já existentes no banco (também limpas)
        LocalDateTime start = request.startDateTime() != null
                ? request.startDateTime().withSecond(0).withNano(0)
                : existingBooking.getStartDateTime().withSecond(0).withNano(0);

        LocalDateTime end = request.endDateTime() != null
                ? request.endDateTime().withSecond(0).withNano(0)
                : existingBooking.getEndDateTime().withSecond(0).withNano(0);

        if (end.isBefore(start) || end.isEqual(start)) {
            throw new IllegalArgumentException("O horário de término deve ser posterior ao horário de início.");
        }

        // Verifica conflito ignorando a própria reserva que está sendo editada
        boolean hasConflict = bookingRepository.existsConflictExcludingId(
                existingBooking.getCourt().getId(), start, end, id);

        if (hasConflict) {
            throw new IllegalArgumentException("O novo horário escolhido conflita com outra reserva existente.");
        }

        existingBooking.setStartDateTime(start);
        existingBooking.setEndDateTime(end);

        return bookingRepository.save(existingBooking);
    }
}