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
    private final CourtRepository courtRepository;

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
            return courtRepository.findAvailableCourts(cleanStart, cleanEnd, sportType, namePattern, pageable);
        }

        if (name != null && !name.isBlank()) {
            return courtRepository.findByNameContainingIgnoreCase(name, pageable);
        }
        if (sportType != null) {
            return courtRepository.findBySportType(sportType, pageable);
        }
        return courtRepository.findAll(pageable);
    }

    public Optional<Court> findById(Long id) {
        return courtRepository.findById(id);
    }

    @Transactional
    public Court save(Court court) {
        return courtRepository.save(court);
    }

    @Transactional
    public void delete(Long id) {
        if (!courtRepository.existsById(id)) {
            throw new EntityNotFoundException("Quadra não encontrada com o ID: " + id);
        }
        courtRepository.deleteById(id);
    }

    @Transactional
    public Court updateCourt(Long id, CourtRequest request) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quadra não encontrada para o id: " + id));

        // Usa o método de domínio para atualizar e validar
        court.update(request.name(), request.sportType(), request.pricePerHour(), request.isAvailable());

        return courtRepository.save(court);
    }

    public long count() {
        return courtRepository.count();
    }
}