package com.example.court_reserve.repository;

import com.example.court_reserve.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.court.id = :courtId " +
            "AND b.id <> :excludeId " + // Ignora a própria reserva na checagem
            "AND (:start < b.endDateTime AND :end > b.startDateTime)")
    boolean existsConflictExcludingId(@Param("courtId") Long courtId,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      @Param("excludeId") Long excludeId);
}
