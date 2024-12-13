package com.hotel.rest.controller;

import com.hotel.common.model.Reservation;
import com.hotel.common.model.Client;
import com.hotel.common.model.Chambre;
import com.hotel.common.repository.ReservationRepository;
import com.hotel.common.repository.ClientRepository;
import com.hotel.common.repository.ChambreRepository;
import com.hotel.rest.dto.ReservationRequest;
import com.hotel.rest.dto.ReservationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationRepository reservationRepository;
    private final ClientRepository clientRepository;
    private final ChambreRepository chambreRepository;

    public ReservationController(
            ReservationRepository reservationRepository,
            ClientRepository clientRepository,
            ChambreRepository chambreRepository) {
        this.reservationRepository = reservationRepository;
        this.clientRepository = clientRepository;
        this.chambreRepository = chambreRepository;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Chambre chambre = chambreRepository.findById(request.getChambreId())
                .orElseThrow(() -> new RuntimeException("Chambre not found"));

        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setChambre(chambre);
        reservation.setDateDebut(request.getDateDebut());
        reservation.setDateFin(request.getDateFin());
        reservation.setPreferences(request.getPreferences());

        Reservation saved = reservationRepository.save(reservation);
        return ResponseEntity.ok(convertToResponse(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequest request) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    Client client = clientRepository.findById(request.getClientId())
                            .orElseThrow(() -> new RuntimeException("Client not found"));
                    Chambre chambre = chambreRepository.findById(request.getChambreId())
                            .orElseThrow(() -> new RuntimeException("Chambre not found"));

                    reservation.setClient(client);
                    reservation.setChambre(chambre);
                    reservation.setDateDebut(request.getDateDebut());
                    reservation.setDateFin(request.getDateFin());
                    reservation.setPreferences(request.getPreferences());

                    return ResponseEntity.ok(convertToResponse(reservationRepository.save(reservation)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservationRepository.delete(reservation);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private ReservationResponse convertToResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setClientNom(reservation.getClient().getNom());
        response.setClientPrenom(reservation.getClient().getPrenom());
        response.setChambreType(reservation.getChambre().getType());
        response.setChambrePrix(reservation.getChambre().getPrix());
        response.setDateDebut(reservation.getDateDebut());
        response.setDateFin(reservation.getDateFin());
        response.setPreferences(reservation.getPreferences());
        return response;
    }
}