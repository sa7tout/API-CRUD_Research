package com.hotel.rest.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservationRequest {
    private Long clientId;
    private Long chambreId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String preferences;
}