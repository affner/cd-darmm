package com.cwdarmm.model.domain;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OptimalContract {

    private int stopTicks;
    private int contracts;
    private double riskUsd;

}
