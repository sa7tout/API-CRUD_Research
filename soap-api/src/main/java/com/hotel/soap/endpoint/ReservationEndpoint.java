package com.hotel.soap.endpoint;

import com.hotel.common.model.Reservation;
import com.hotel.common.model.Client;
import com.hotel.common.model.Chambre;
import com.hotel.common.repository.ReservationRepository;
import com.hotel.common.repository.ClientRepository;
import com.hotel.common.repository.ChambreRepository;
import com.hotel.soap.api.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class ReservationEndpoint {
    private static final String NAMESPACE_URI = "http://hotel.com/soap/api";
    private final ReservationRepository reservationRepository;
    private final ClientRepository clientRepository;
    private final ChambreRepository chambreRepository;

    public ReservationEndpoint(ReservationRepository reservationRepository,
                               ClientRepository clientRepository,
                               ChambreRepository chambreRepository) {
        this.reservationRepository = reservationRepository;
        this.clientRepository = clientRepository;
        this.chambreRepository = chambreRepository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getReservationRequest")
    @ResponsePayload
    public GetReservationResponse getReservation(@RequestPayload GetReservationRequest request) {
        Reservation reservation = reservationRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        GetReservationResponse response = new GetReservationResponse();
        response.setReservation(convertToSoapReservation(reservation));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createReservationRequest")
    @ResponsePayload
    public CreateReservationResponse createReservation(@RequestPayload CreateReservationRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Chambre chambre = chambreRepository.findById(request.getChambreId())
                .orElseThrow(() -> new RuntimeException("Chambre not found"));

        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setChambre(chambre);
        reservation.setDateDebut(request.getDateDebut().toGregorianCalendar().toZonedDateTime().toLocalDate());
        reservation.setDateFin(request.getDateFin().toGregorianCalendar().toZonedDateTime().toLocalDate());
        reservation.setPreferences(request.getPreferences());

        Reservation saved = reservationRepository.save(reservation);

        CreateReservationResponse response = new CreateReservationResponse();
        response.setReservation(convertToSoapReservation(saved));
        return response;
    }

    private com.hotel.soap.api.Reservation convertToSoapReservation(Reservation reservation) {
        com.hotel.soap.api.Reservation soapReservation = new com.hotel.soap.api.Reservation();
        soapReservation.setId(reservation.getId());

        com.hotel.soap.api.Client soapClient = new com.hotel.soap.api.Client();
        soapClient.setId(reservation.getClient().getId());
        soapClient.setNom(reservation.getClient().getNom());
        soapClient.setPrenom(reservation.getClient().getPrenom());
        soapClient.setEmail(reservation.getClient().getEmail());
        soapClient.setTelephone(reservation.getClient().getTelephone());
        soapReservation.setClient(soapClient);

        com.hotel.soap.api.Chambre soapChambre = new com.hotel.soap.api.Chambre();
        soapChambre.setId(reservation.getChambre().getId());
        soapChambre.setType(reservation.getChambre().getType());
        soapChambre.setPrix(reservation.getChambre().getPrix());
        soapChambre.setDisponible(reservation.getChambre().getDisponible());
        soapReservation.setChambre(soapChambre);

        // Convert dates to XMLGregorianCalendar
        // Add implementation here

        soapReservation.setPreferences(reservation.getPreferences());
        return soapReservation;
    }
}