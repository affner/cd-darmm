package com.cwdarmm.model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TradingAccount {

    private String name;             // “NEXGEN”
    private double initialSize;      // 5 000
    private double currentSize;      // mutable

    public void updateSize(double delta) { currentSize += delta; }


}
