package com.hotel.common.model;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@Entity
@Table(name = "chambres")
public class Chambre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String type;

    @NotNull
    @Positive
    private Double prix;

    private Boolean disponible = true;
}