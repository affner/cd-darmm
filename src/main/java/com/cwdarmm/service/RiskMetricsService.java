package com.cwdarmm.service;

import org.springframework.stereotype.Service;

import java.util.DoubleSummaryStatistics;
import java.util.List;

/**
 * Servicio con cálculos estadísticos básicos para el manejo de riesgo.
 */
@Service
public class RiskMetricsService {

    /**
     * Calcula la expectativa del sistema.
     * Fórmula: E = (avgWin * pWin) - (avgLoss * (1 - pWin))
     * trades: lista de PnL de cada trade (positivos y negativos)
     */
    public double calculateExpectancy(List<Double> trades) {
        if (trades == null || trades.isEmpty()) return 0.0;
        long total = trades.size();
        long wins = trades.stream().filter(v -> v > 0).count();
        long losses = total - wins;

        double pWin = (double) wins / total;

        DoubleSummaryStatistics stats = trades.stream()
                .filter(v -> v > 0)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();
        double avgWin = stats.getCount() > 0 ? stats.getAverage() : 0.0;

        DoubleSummaryStatistics lossStats = trades.stream()
                .filter(v -> v < 0)
                .mapToDouble(Math::abs)
                .summaryStatistics();
        double avgLoss = lossStats.getCount() > 0 ? lossStats.getAverage() : 0.0;

        return (avgWin * pWin) - (avgLoss * (1 - pWin));
    }

    /**
     * Calcula el drawdown máximo de la curva de capital.
     * drawdown = máximo pico menos valle subsecuente
     */
    public double calculateDrawdown(List<Double> equityCurve) {
        if (equityCurve == null || equityCurve.isEmpty()) return 0.0;
        double peak = Double.NEGATIVE_INFINITY;
        double maxDrawdown = 0.0;

        for (double value : equityCurve) {
            if (value > peak) {
                peak = value;
            } else {
                double dd = peak - value;
                if (dd > maxDrawdown) {
                    maxDrawdown = dd;
                }
            }
        }
        return maxDrawdown;
    }

    /**
     * Calcula el riesgo de ruina tras cierto número de trades.
     * Aproximación: (q / (p * payoff)) ^ trades
     * donde q = 1 - p, p = winRate, payoff = ratio de ganancia por pérdida
     */
    public double calculateRiskOfRuin(double winRate, double payoff, int trades) {
        if (winRate <= 0 || winRate >= 1 || payoff <= 0 || trades <= 0) return 1.0;
        double q = 1.0 - winRate;
        double factor = q / (winRate * payoff);
        return Math.pow(factor, trades);
    }


}
