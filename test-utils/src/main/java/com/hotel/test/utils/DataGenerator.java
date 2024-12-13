package com.hotel.test.utils;

import com.hotel.common.model.*;
import java.time.LocalDate;
import java.util.Random;

public class DataGenerator {
    private static final Random random = new Random();

    public static class PayloadSize {
        public static final int SMALL = 1024;    // 1KB
        public static final int MEDIUM = 10240;  // 10KB
        public static final int LARGE = 102400;  // 100KB
    }

    public static Client generateClient() {
        Client client = new Client();
        client.setNom("Test Client " + random.nextInt(1000));
        client.setPrenom("Prenom " + random.nextInt(1000));
        client.setEmail("test" + random.nextInt(1000) + "@test.com");
        client.setTelephone("+1234567890");
        return client;
    }

    public static Chambre generateChambre() {
        Chambre chambre = new Chambre();
        chambre.setType(random.nextBoolean() ? "Simple" : "Double");
        chambre.setPrix(100.0 + random.nextDouble() * 200);
        chambre.setDisponible(true);
        return chambre;
    }

    public static String generateLargeText(int targetSize) {
        StringBuilder sb = new StringBuilder();
        String baseText = "Additional reservation details and metadata: ";
        while (sb.length() < targetSize) {
            sb.append(baseText).append(random.nextInt(10000)).append(" ");
        }
        return sb.substring(0, targetSize);
    }

    public static Reservation generateReservation(Client client, Chambre chambre, int payloadSize) {
        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setChambre(chambre);
        reservation.setDateDebut(LocalDate.now());
        reservation.setDateFin(LocalDate.now().plusDays(random.nextInt(10) + 1));

        String preferences;
        switch (payloadSize) {
            case PayloadSize.SMALL:
                preferences = "Basic preferences";
                break;
            case PayloadSize.MEDIUM:
                preferences = generateLargeText(PayloadSize.MEDIUM / 2);
                break;
            case PayloadSize.LARGE:
                preferences = generateLargeText(PayloadSize.LARGE / 2);
                break;
            default:
                preferences = "Default preferences";
        }
        reservation.setPreferences(preferences);

        return reservation;
    }
}