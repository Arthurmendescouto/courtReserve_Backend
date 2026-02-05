package com.example.court_reserve.service;

import com.example.court_reserve.controller.request.CourtRequest;
import com.example.court_reserve.entity.Booking;
import com.example.court_reserve.entity.Court;
import com.example.court_reserve.entity.SportType;
import com.example.court_reserve.entity.User;
import com.example.court_reserve.repository.BookingRepository;
import com.example.court_reserve.repository.CourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourtService {
    private final CourtRepository repository;


    public Page<Court> findAll(Pageable pageable, SportType sportType, LocalDateTime start, LocalDateTime end){
        if (start != null && end != null) {
            return repository.findAvailableCourts(start, end, sportType, pageable);
        }
        if (sportType != null) {
            return repository.findBySportType(sportType, pageable);
        }
        return repository.findAll(pageable);
    }
    public Optional<Court> findById(Long id){
        return repository.findById(id);
    }

    public Court save(Court court){
        court.validate();
        return repository.save(court);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EmptyResultDataAccessException("Usuário não encontrado com o ID: " + id, 1);
        }
        repository.deleteById(id);
    }

    public Court updateCourt(Long id, CourtRequest request) {



        Court court = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Court não encontrado para o id: " + id));

        if (request.sportType() != null) {
            court.setSportType(request.sportType());
        }
        if (request.pricePerHour() != null) {
            court.setPricePerHour(request.pricePerHour());
        }
        if (request.isAvailable() != null) {
            court.setAvailable(request.isAvailable());
        }
        court.validate();
        return repository.save(court);
    }

    public long count() {
        return repository.count();
    }
}
