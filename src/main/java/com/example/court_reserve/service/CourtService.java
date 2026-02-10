package com.example.court_reserve.service;

import com.example.court_reserve.controller.request.CourtRequest;
import com.example.court_reserve.entity.Court;
import com.example.court_reserve.entity.SportType;
import com.example.court_reserve.repository.CourtRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourtService {
    private final CourtRepository repository;

    public Page<Court> findAll(Pageable pageable, SportType sportType, LocalDateTime start, LocalDateTime end, String name) {
        if (start != null && end != null) {
            // LIMPEZA: Garante que a busca de quadras ignore segundos/milisegundos
            // Isso evita que o banco ignore conflitos por causa de frações de segundo.
            LocalDateTime cleanStart = start.truncatedTo(ChronoUnit.MINUTES);
            LocalDateTime cleanEnd = end.truncatedTo(ChronoUnit.MINUTES);

            String namePattern = null;
            if (name != null && !name.isBlank()) {
                namePattern = "%" + name.toLowerCase() + "%";
            }
            return repository.findAvailableCourts(cleanStart, cleanEnd, sportType, namePattern, pageable);
        }

        if (name != null && !name.isBlank()) {
            return repository.findByNameContainingIgnoreCase(name, pageable);
        }
        if (sportType != null) {
            return repository.findBySportType(sportType, pageable);
        }
        return repository.findAll(pageable);
    }

    public Optional<Court> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Court save(Court court) {
        court.validate();
        return repository.save(court);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Quadra não encontrada com o ID: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public Court updateCourt(Long id, CourtRequest request) {
        Court court = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quadra não encontrada para o id: " + id));

        if (request.name() != null) court.setName(request.name());
        if (request.sportType() != null) court.setSportType(request.sportType());
        if (request.pricePerHour() != null) court.setPricePerHour(request.pricePerHour());
        if (request.isAvailable() != null) court.setAvailable(request.isAvailable());

        court.validate();
        return repository.save(court);
    }

    public long count() {
        return repository.count();
    }
}