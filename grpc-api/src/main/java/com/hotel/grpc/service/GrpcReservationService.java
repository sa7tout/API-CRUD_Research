package com.hotel.grpc.service;

import com.hotel.common.model.Reservation;
import com.hotel.common.repository.ReservationRepository;
import com.hotel.common.repository.ClientRepository;
import com.hotel.common.repository.ChambreRepository;
import com.hotel.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@GrpcService
public class GrpcReservationService extends ReservationServiceGrpc.ReservationServiceImplBase {
    private final ReservationRepository reservationRepository;
    private final ClientRepository clientRepository;
    private final ChambreRepository chambreRepository;

    public GrpcReservationService(
            ReservationRepository reservationRepository,
            ClientRepository clientRepository,
            ChambreRepository chambreRepository) {
        this.reservationRepository = reservationRepository;
        this.clientRepository = clientRepository;
        this.chambreRepository = chambreRepository;
    }

    @Override
    @Transactional
    public void createReservation(CreateReservationRequest request,
                                  StreamObserver<ReservationResponse> responseObserver) {
        try {
            var client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            var chambre = chambreRepository.findById(request.getChambreId())
                    .orElseThrow(() -> new RuntimeException("Chambre not found"));

            var reservation = new Reservation();
            reservation.setClient(client);
            reservation.setChambre(chambre);
            reservation.setDateDebut(LocalDate.parse(request.getDateDebut()));
            reservation.setDateFin(LocalDate.parse(request.getDateFin()));
            reservation.setPreferences(request.getPreferences());

            var saved = reservationRepository.save(reservation);
            responseObserver.onNext(convertToGrpcReservation(saved));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getReservation(GetReservationRequest request,
                               StreamObserver<ReservationResponse> responseObserver) {
        try {
            var reservation = reservationRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("Reservation not found"));
            responseObserver.onNext(convertToGrpcReservation(reservation));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void updateReservation(UpdateReservationRequest request,
                                  StreamObserver<ReservationResponse> responseObserver) {
        try {
            var reservation = reservationRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("Reservation not found"));

            var client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            var chambre = chambreRepository.findById(request.getChambreId())
                    .orElseThrow(() -> new RuntimeException("Chambre not found"));

            reservation.setClient(client);
            reservation.setChambre(chambre);
            reservation.setDateDebut(LocalDate.parse(request.getDateDebut()));
            reservation.setDateFin(LocalDate.parse(request.getDateFin()));
            reservation.setPreferences(request.getPreferences());

            var updated = reservationRepository.save(reservation);
            responseObserver.onNext(convertToGrpcReservation(updated));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void deleteReservation(DeleteReservationRequest request,
                                  StreamObserver<DeleteReservationResponse> responseObserver) {
        try {
            reservationRepository.deleteById(request.getId());
            responseObserver.onNext(DeleteReservationResponse.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    private ReservationResponse convertToGrpcReservation(Reservation reservation) {
        return ReservationResponse.newBuilder()
                .setId(reservation.getId())
                .setClient(Client.newBuilder()
                        .setId(reservation.getClient().getId())
                        .setNom(reservation.getClient().getNom())
                        .setPrenom(reservation.getClient().getPrenom())
                        .setEmail(reservation.getClient().getEmail())
                        .setTelephone(reservation.getClient().getTelephone())
                        .build())
                .setChambre(Chambre.newBuilder()
                        .setId(reservation.getChambre().getId())
                        .setType(reservation.getChambre().getType())
                        .setPrix(reservation.getChambre().getPrix())
                        .setDisponible(reservation.getChambre().getDisponible())
                        .build())
                .setDateDebut(reservation.getDateDebut().toString())
                .setDateFin(reservation.getDateFin().toString())
                .setPreferences(reservation.getPreferences())
                .build();
    }
}