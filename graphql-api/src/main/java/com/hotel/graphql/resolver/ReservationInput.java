package com.hotel.graphql.resolver;

import lombok.Data;

@Data
public class ReservationInput {
    private Long clientId;
    private Long chambreId;
    private String dateDebut;
    private String dateFin;
    private String preferences;
}