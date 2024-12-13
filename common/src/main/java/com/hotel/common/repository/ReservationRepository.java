package com.hotel.common.repository;

import com.hotel.common.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByClientId(Long clientId);
    List<Reservation> findByChambreIdAndDateDebutBetweenOrDateFinBetween(
            Long chambreId, LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2);
}