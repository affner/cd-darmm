package com.cwdarmm.model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Market {
    private String name;       // “S&P 500”
    private String symbol;     // ES
    private double tickSize;   // 0.25
    private double tickValue;  // 12.5
    private int   defaultStop; // ticks sugeridos
    private String colorHex;   // para la UI

}
