// ReservationResolver.java
package com.hotel.graphql.resolver;

import com.hotel.common.model.Reservation;
import com.hotel.common.model.Client;
import com.hotel.common.model.Chambre;
import com.hotel.common.repository.ReservationRepository;
import com.hotel.common.repository.ClientRepository;
import com.hotel.common.repository.ChambreRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.time.LocalDate;
import java.util.List;

@Controller
public class ReservationResolver {
    private final ReservationRepository reservationRepository;
    private final ClientRepository clientRepository;
    private final ChambreRepository chambreRepository;

    public ReservationResolver(
            ReservationRepository reservationRepository,
            ClientRepository clientRepository,
            ChambreRepository chambreRepository) {
        this.reservationRepository = reservationRepository;
        this.clientRepository = clientRepository;
        this.chambreRepository = chambreRepository;
    }

    @QueryMapping
    public Reservation reservation(@Argument Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    @QueryMapping
    public List<Reservation> reservations() {
        return reservationRepository.findAll();
    }

    @QueryMapping
    public List<Chambre> chambresDisponibles() {
        return chambreRepository.findByDisponible(true);
    }

    @MutationMapping
    public Reservation createReservation(@Argument ReservationInput input) {
        Client client = clientRepository.findById(input.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Chambre chambre = chambreRepository.findById(input.getChambreId())
                .orElseThrow(() -> new RuntimeException("Chambre not found"));

        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setChambre(chambre);
        reservation.setDateDebut(LocalDate.parse(input.getDateDebut()));
        reservation.setDateFin(LocalDate.parse(input.getDateFin()));
        reservation.setPreferences(input.getPreferences());

        return reservationRepository.save(reservation);
    }

    @MutationMapping
    public Reservation updateReservation(@Argument Long id, @Argument ReservationInput input) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        Client client = clientRepository.findById(input.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Chambre chambre = chambreRepository.findById(input.getChambreId())
                .orElseThrow(() -> new RuntimeException("Chambre not found"));

        reservation.setClient(client);
        reservation.setChambre(chambre);
        reservation.setDateDebut(LocalDate.parse(input.getDateDebut()));
        reservation.setDateFin(LocalDate.parse(input.getDateFin()));
        reservation.setPreferences(input.getPreferences());

        return reservationRepository.save(reservation);
    }

    @MutationMapping
    public Boolean deleteReservation(@Argument Long id) {
        reservationRepository.deleteById(id);
        return true;
    }
}