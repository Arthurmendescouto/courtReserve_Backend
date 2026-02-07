package com.example.court_reserve.repository;

import com.example.court_reserve.entity.Court;
import com.example.court_reserve.entity.SportType;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourtRepository extends JpaRepository<Court,Long> {

    Page<Court> findBySportType(SportType sportType, Pageable pageable);

    // Filtro simples por nome (Case Insensitive)
    Page<Court> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT c FROM Court c WHERE " +
            "(:sportType IS NULL OR c.sportType = :sportType) AND " +
            "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "c.isAvailable = true AND " +
            "c.id NOT IN " +
            "(SELECT b.court.id FROM Booking b WHERE " +
            "(b.startDateTime < :end AND b.endDateTime > :start))")
    Page<Court> findAvailableCourts(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end,
                                    @Param("sportType") SportType sportType,
                                    @Param("name") String name,
                                    Pageable pageable);

}
