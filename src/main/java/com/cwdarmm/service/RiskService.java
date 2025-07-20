// RiskService.java
package com.cwdarmm.service;

import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        List<Double> pnlSeries = results.stream()
                .filter(r -> r.getTradeNumber() > 0)
                .map(r -> r.getAccountSize() - results.get(r.getTradeNumber() - 1).getAccountSize())
                .toList();

        double expectancy = riskMetricsService.calculateExpectancy(pnlSeries);
        double drawdown  = riskMetricsService.calculateDrawdown(
                results.stream().map(RiskResultDTO::getAccountSize).toList());
        double ruinRisk  = riskMetricsService.calculateRiskOfRuin(
                /* pWin */ 0.5, /* payoff */ 1.0, pnlSeries.size());
        System.out.printf("Metrics -> E=%.2f, DD=%.2f, RoR=%.4f%n", expectancy, drawdown, ruinRisk);

        return results;
    }
}
