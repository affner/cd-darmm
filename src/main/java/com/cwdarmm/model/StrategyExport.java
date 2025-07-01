package com.cwdarmm.model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class StrategyExport {

    private String name;
    private Market market;
    private int contracts;
    private int stopTicks;
    private int targetTicks;


}
