package com.cwdarmm.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utilidad para calcular el porcentaje de riesgo Kelly B tras cada trade.
 *
 * <p>La lógica replica el comportamiento del formulario VBA:
 * partiendo del valor del trade anterior (prev) se aplica un
 * multiplicador del 5% cuando el trade previo fue ganador
 * o del -2% cuando fue perdedor. Si es el primer trade
 * (prev == 0), se utiliza el riesgo base almacenado en la
 * cuenta.</p>
 */
public class RiskEngine {

    /**
     * Calcula el nuevo porcentaje Kelly B.
     *
     * @param prevKellyB   valor Kelly B del trade anterior; 0 si es el primero
     * @param prevWin      resultado del trade anterior (true = WIN, false = LOSS)
     * @param baseRiskB    porcentaje inicial de Kelly B definido en la cuenta
     * @return nuevo porcentaje Kelly B redondeado a 6 decimales
     */
    public double calculateKellyB(double prevKellyB, boolean prevWin, double baseRiskB) {
        double next;
        if (prevKellyB == 0) {
            // primer trade: se usa el valor base sin compounding
            next = baseRiskB;
        } else {
            next = prevKellyB * (prevWin ? 1.05 : 0.98);
        }
        return BigDecimal.valueOf(next).setScale(6, RoundingMode.HALF_UP).doubleValue();
    }
}
