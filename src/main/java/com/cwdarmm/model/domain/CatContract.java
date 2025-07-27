package com.cwdarmm.model.domain;

/**
 * Catálogo de contratos (futuros) configurables.
 * Representa la descripción del instrumento (E-mini, Micro, etc.).
 */

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cat_contracts")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CatContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** p.ej. "E-mini Nasdaq", "Micro E-mini S&P", "Gold Futures", etc. */
    @Column(name = "description", nullable = false, unique = true)
    private String description;
}
