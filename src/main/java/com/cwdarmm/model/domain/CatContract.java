package com.cwdarmm.model.domain;

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
