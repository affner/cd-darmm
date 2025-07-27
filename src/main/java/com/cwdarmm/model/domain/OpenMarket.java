package com.cwdarmm.model.domain;

/**
 * Sesión de mercado abierta por el usuario.
 *
 * <p>Almacena la combinación de cuenta, mercado y feed
 * seleccionados, además de los porcentajes de riesgo
 * configurados para la sesión.</p>
 */

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "open_markets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenMarket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_account_id", nullable = false)
    private CatAccount account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_market_id", nullable = false)
    private CatMarket market;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_market_data_id", nullable = false)
    private CatMarketData marketData;

    @Column(name = "account_size", precision = 19, scale = 8)
    private BigDecimal accountSize;

    @Column(name = "riska")
    private Double riskA;

    @Column(name = "riskb")
    private Double riskB;

    @Column(name = "risk_final_house")
    private Double riskFinalHouse;

    @Column(name = "risk_final_lunch")
    private Double riskFinalLunch;
}
