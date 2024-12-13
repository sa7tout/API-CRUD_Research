package com.hotel.common.model;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
@Entity
@Table(name = "utilisateurs")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String nomUtilisateur;

    @NotBlank
    private String motDePasse;

    @NotBlank
    private String role;
}