package com.cwdarmm.model.domain;

/**
 * Configuración detallada de mercado (tabla <code>bd_markets</code>).
 *
 * <p>Relaciona cuenta, mercado, feed de datos y parámetros
 * específicos como tamaño de tick, margen y comisión.</p>
 */

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bd_markets")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BdMarket {
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cat_contract_id")
    private CatContract contract;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cat_symbol_id")
    private CatSymbol symbol;

    @Column(name = "multiplier")
    private Double multiplier;

    @Column(name = "tick_size")
    private Double tickSize;

    @Column(name = "tick_value")
    private Double tickValue;

    @Column(name = "margin")
    private Double margin;

    @Column(name = "commission")
    private Double commission;
}
