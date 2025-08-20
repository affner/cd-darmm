package com.cwdarmm.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Servicio sencillo que calcula el número de contratos óptimos
 * según el tamaño de cuenta, el porcentaje de riesgo actual y
 * la configuración del stop loss.
 */
public class OptimalContractService {

    /**
     * Calcula la cantidad óptima de contratos.
     *
     * @param accountSize   capital de la cuenta
     * @param currentKellyB porcentaje Kelly B actual
     * @param slSizeTicks   tamaño del stop loss en ticks
     * @param tickValue     valor monetario de cada tick
     * @return número entero de contratos
     */
    public int calculate(double accountSize,
                         double currentKellyB,
                         int slSizeTicks,
                         double tickValue) {
        double riskPct = currentKellyB / 100.0;
        BigDecimal numerator = BigDecimal.valueOf(accountSize).multiply(BigDecimal.valueOf(riskPct));
        BigDecimal denominator = BigDecimal.valueOf(slSizeTicks).multiply(BigDecimal.valueOf(tickValue));
        return numerator.divide(denominator, 0, RoundingMode.DOWN).intValue();
    }
}
