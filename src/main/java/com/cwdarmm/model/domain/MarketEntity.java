package com.cwdarmm.model.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "markets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private TradingAccount account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_id", nullable = false)
    private OpenMarket market;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private PriceFeed marketData;

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
