package com.hotel.rest.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservationResponse {
    private Long id;
    private String clientNom;
    private String clientPrenom;
    private String chambreType;
    private Double chambrePrix;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String preferences;
}