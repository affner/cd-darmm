package com.cwdarmm.model.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bd_market")  // nombre de la tabla de BD_MARKET
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketCatalog {
    @Id
    @Column(name = "symbol", length = 10)
    private String symbol;

    @Column(name = "multiplier")
    private double multiplier;

    @Column(name = "tick_size")
    private double tickSize;

    @Column(name = "tick_value")
    private double tickValue;

    @Column(name = "margin")
    private double margin;

    @Column(name = "commission")
    private double commission;
}
