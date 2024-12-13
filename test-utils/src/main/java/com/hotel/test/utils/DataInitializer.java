package com.hotel.test.utils;

import com.hotel.common.model.*;
import com.hotel.common.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final ClientRepository clientRepository;
    private final ChambreRepository chambreRepository;
    private final ReservationRepository reservationRepository;

    public DataInitializer(
            ClientRepository clientRepository,
            ChambreRepository chambreRepository,
            ReservationRepository reservationRepository) {
        this.clientRepository = clientRepository;
        this.chambreRepository = chambreRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void run(String... args) {
        if (clientRepository.count() == 0) {
            List<Client> clients = new ArrayList<>();
            List<Chambre> chambres = new ArrayList<>();

            // Create test clients
            for (int i = 0; i < 10; i++) {
                clients.add(DataGenerator.generateClient());
            }
            clientRepository.saveAll(clients);

            // Create test rooms
            for (int i = 0; i < 5; i++) {
                chambres.add(DataGenerator.generateChambre());
            }
            chambreRepository.saveAll(chambres);

            // Create test reservations
            for (int i = 0; i < 5; i++) {
                Reservation reservation = DataGenerator.generateReservation(
                        clients.get(i),
                        chambres.get(i % chambres.size()),
                        DataGenerator.PayloadSize.SMALL
                );
                reservationRepository.save(reservation);
            }
        }
    }
}