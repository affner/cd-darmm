package com.cwdarmm.model.domain;

/**
 * Catálogo de mercados disponibles (e.g. S&P 500, NASDAQ...).
 *
 * <p>Entidad mapeada a la tabla <code>cat_markets</code>.</p>
 */

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cat_markets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatMarket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false, unique = true)
    private String description;
}
