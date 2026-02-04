package com.example.court_reserve.service;

import com.example.court_reserve.config.JWTUserData;
import com.example.court_reserve.controller.request.BookingRequest;
import com.example.court_reserve.entity.Booking;
import com.example.court_reserve.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository repository;
    private final BookingRepository bookingRepository;

    public Page<Booking> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<Booking> findById(Long id) {
        return repository.findById(id);
    }

    public Booking save(Booking booking) {

        return repository.save(booking);
    }

    public void delete(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new EmptyResultDataAccessException("Usuário não encontrado com o ID: " + id, 1);
        }
        bookingRepository.deleteById(id);
    }
    public Booking update(Long id, BookingRequest request) {


        Booking existingBooking = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada com o ID: " + id));

        if (request.startDateTime() != null) {
            existingBooking.setStartDateTime(request.startDateTime());
        }


        if (request.endDateTime() != null) {
            existingBooking.setEndDateTime(request.endDateTime());
        }

        if (existingBooking.getEndDateTime().isBefore(existingBooking.getStartDateTime())) {
            throw new IllegalArgumentException("O horário de término deve ser posterior ao horário de início.");
        }
        return repository.save(existingBooking);
    }
}