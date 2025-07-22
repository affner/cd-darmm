// RiskService.java
package com.cwdarmm.service;

import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RiskService {
    private final RiskMetricsService riskMetricsService;
    private final RiskSimulationService simulationService;

    /**
     * Ejecuta la simulación de trades y calcula métricas básicas.
     */
    public List<RiskResultDTO> calculate(RiskInputDTO in) {
        List<RiskResultDTO> results = simulationService.simulateTrades(in);

        // Construir serie de PnL para métricas
// 1) Construir serie de PnL como BigDecimal
        List<BigDecimal> pnlSeries = results.stream()
                .filter(r -> r.getTradeNumber() > 0)
                .map(r -> r.getAccountSize()
                        .subtract(results.get(r.getTradeNumber() - 1).getAccountSize()))
                .toList();

        // 2) Llamar a los métodos ahora con BigDecimal
        BigDecimal expectancy = riskMetricsService.calculateExpectancy(pnlSeries);
        BigDecimal drawdown = riskMetricsService.calculateDrawdown(
                results.stream().map(RiskResultDTO::getAccountSize).toList());
        BigDecimal ruinRisk = riskMetricsService.calculateRiskOfRuin(
                BigDecimal.valueOf(0.5), BigDecimal.valueOf(1.0), pnlSeries.size());

        // 3) Log con toPlainString()
        System.out.printf("Metrics -> E=%s, DD=%s, RoR=%s%n",
                expectancy.toPlainString(),
                drawdown.toPlainString(),
                ruinRisk.toPlainString());

        System.out.printf("Metrics -> E=%.2f, DD=%.2f, RoR=%.4f%n", expectancy, drawdown, ruinRisk);

        return results;
    }
}
