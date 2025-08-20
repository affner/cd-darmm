package com.cwdarmm.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Motor sencillo para aplicar la lógica de compounding/drawdown
 * sobre el porcentaje de riesgo Kelly B. Mantiene el valor
 * actual en memoria y lo actualiza tras cada trade.
 */
@Service
public class RiskEngine {
    private final double baseRiskB;
    private double current;

    public RiskEngine() {
        this(0);
    }

    public RiskEngine(double baseRiskB) {
        this.baseRiskB = baseRiskB;
        this.current = 0;
    }

    /**
     * Calcula el porcentaje Kelly B a utilizar en el trade actual y
     * actualiza el estado interno para el siguiente trade.
     *
     * @param isWin resultado del trade previo (true si fue WIN)
     * @return porcentaje Kelly B para el trade actual
     */
    public double calculateKellyB(boolean isWin) {
        double next = current == 0 ? baseRiskB : current;
        double result = next;
        next = next * (isWin ? 1.05 : 0.98);
        current = round(next);
        return round(result);
    }

    private double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(6, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
