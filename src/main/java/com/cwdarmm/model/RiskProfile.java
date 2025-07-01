package com.cwdarmm.model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RiskProfile {

    private double kellyA;    // %
    private double kellyB;    // %
    private double lambdaWin; // multiplicador tras ganancia
    private double lambdaLoss;// multiplicador tras pérdida

}
