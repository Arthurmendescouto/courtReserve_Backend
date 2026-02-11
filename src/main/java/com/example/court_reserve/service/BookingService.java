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
        // 1. Buscar Entidades
        Court court = courtRepository.findById(request.courtId())
                .orElseThrow(() -> new EntityNotFoundException("Quadra não encontrada."));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        // 2. Criar a Entidade usando o Modelo Rico (Validações e Preço acontecem aqui)
        Booking booking = Booking.create(user, court, request.startDateTime(), request.endDateTime());

        // 3. Verificar Conflito de Horário
        boolean hasConflict = bookingRepository.existsConflictExcludingId(
                court.getId(), booking.getStartDateTime(), booking.getEndDateTime(), -1L);

        if (hasConflict) {
            throw new IllegalArgumentException("Já existe reserva para este horário nesta quadra.");
        }

        return bookingRepository.save(booking);
    }

    @Transactional
    public void delete(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new EntityNotFoundException("Reserva não encontrada com o ID: " + id);
        }
        bookingRepository.deleteById(id);
    }

    @Transactional
    public Booking update(Long id, BookingRequest request) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada com o ID: " + id));

        // Define as novas datas (ou mantém as antigas)
        LocalDateTime newStart = request.startDateTime() != null ? request.startDateTime() : existingBooking.getStartDateTime();
        LocalDateTime newEnd = request.endDateTime() != null ? request.endDateTime() : existingBooking.getEndDateTime();

        // Recria um objeto temporário para validar as regras (datas, preço, etc)
        Booking tempValido = Booking.create(existingBooking.getUser(), existingBooking.getCourt(), newStart, newEnd);

        // Verifica conflito ignorando a própria reserva que está sendo editada
        boolean hasConflict = bookingRepository.existsConflictExcludingId(
                existingBooking.getCourt().getId(), tempValido.getStartDateTime(), tempValido.getEndDateTime(), id);

        if (hasConflict) {
            throw new IllegalArgumentException("O novo horário escolhido conflita com outra reserva existente.");
        }

        existingBooking.setStartDateTime(tempValido.getStartDateTime());
        existingBooking.setEndDateTime(tempValido.getEndDateTime());
        existingBooking.setTotalPrice(tempValido.getTotalPrice()); // Atualiza o preço caso o horário tenha mudado

        return bookingRepository.save(existingBooking);
    }
}