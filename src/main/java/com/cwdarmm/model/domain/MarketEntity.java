package com.cwdarmm.model.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "markets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String account;
    private String market;
    @Column(name = "market_data")
    private String marketData;
    private Double accountSize;
    private Double riskA;
    private Double riskB;
    private Double riskFinalHouse;
    private Double riskFinalLunch;
}
