package com.cwdarmm.model.domain;

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

    @Column(name = "multiplier",    nullable = false)
    private Integer multiplier;

    @Column(name = "tick_size",     nullable = false)
    private Double tickSize;

    @Column(name = "tick_value",    nullable = false)
    private Double tickValue;

    @Column(name = "margin",        nullable = false)
    private Double margin;

    @Column(name = "commission",    nullable = false)
    private Double commission;
}
