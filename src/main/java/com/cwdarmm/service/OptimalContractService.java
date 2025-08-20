package com.cwdarmm.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Calcula el número óptimo de contratos según el tamaño de la cuenta,
 * el porcentaje de riesgo actual y el valor por tick del mercado.
 */
@Service
public class OptimalContractService {

    /**
     * @param account       tamaño de la cuenta en dólares
     * @param currentKellyB porcentaje Kelly B actual (por ejemplo 1.5)
     * @param slSizeTicks   tamaño del stop-loss en ticks
     * @param tickValue     valor monetario de un tick
     * @return número entero de contratos óptimos
     */
    public int calculateOptimalContracts(BigDecimal account,
                                         double currentKellyB,
                                         int slSizeTicks,
                                         double tickValue) {
        if (account == null) return 0;
        double riskPct = currentKellyB / 100.0;
        double denom = slSizeTicks * tickValue;
        if (denom <= 0) return 0;
        double numerator = account.doubleValue() * riskPct;
        return (int) Math.floor(numerator / denom);
    }
}
