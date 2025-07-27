package com.cwdarmm.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

/**
 * Servicio con cálculos estadísticos básicos para el manejo de riesgo.
 *
 * <p>Implementa funciones como expectativa y drawdown, conceptos
 * explicados en el artículo "CW-DARMM: A Probabilistic,
 * Confidence-Weighted Framework for Adaptive Capital Growth". Son
 * utilidades independientes de la capa de presentación y se
 * inspiran en las macros del libro XLSM.</p>
 */
@Service
public class RiskMetricsService {

    private static final MathContext MC = MathContext.DECIMAL64;

    /**
     * Calcula la expectativa del sistema.
     * Fórmula: E = (avgWin * pWin) - (avgLoss * (1 - pWin))
     * trades: lista de PnL de cada trade (positivos y negativos)
     */
    public BigDecimal calculateExpectancy(List<BigDecimal> trades) {
        if (trades == null || trades.isEmpty()) {
            return BigDecimal.ZERO.stripTrailingZeros();
        }

        int total = trades.size();
        long wins = trades.stream().filter(v -> v.compareTo(BigDecimal.ZERO) > 0).count();
        long losses = total - wins;

        BigDecimal bdTotal = BigDecimal.valueOf(total);
        BigDecimal pWin = BigDecimal.valueOf(wins).divide(bdTotal, MC);

        // avgWin
        BigDecimal sumWin = trades.stream()
                .filter(v -> v.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgWin = wins > 0
                ? sumWin.divide(BigDecimal.valueOf(wins), MC)
                : BigDecimal.ZERO;

        // avgLoss (usamos valores absolutos)
        BigDecimal sumLoss = trades.stream()
                .filter(v -> v.compareTo(BigDecimal.ZERO) < 0)
                .map(BigDecimal::abs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgLoss = losses > 0
                ? sumLoss.divide(BigDecimal.valueOf(losses), MC)
                : BigDecimal.ZERO;

        BigDecimal termWin  = avgWin.multiply(pWin, MC);
        BigDecimal termLoss = avgLoss.multiply(
                BigDecimal.ONE.subtract(pWin, MC), MC);

        return termWin.subtract(termLoss, MC)
                .stripTrailingZeros();
    }

    /**
     * Calcula el drawdown máximo de la curva de capital.
     * drawdown = máximo pico menos valle subsecuente
     */
    public BigDecimal calculateDrawdown(List<BigDecimal> equityCurve) {
        if (equityCurve == null || equityCurve.isEmpty()) {
            return BigDecimal.ZERO.stripTrailingZeros();
        }

        BigDecimal peak = equityCurve.get(0);
        BigDecimal maxDrawdown = BigDecimal.ZERO;

        for (BigDecimal value : equityCurve) {
            if (value.compareTo(peak) > 0) {
                peak = value;
            } else {
                BigDecimal dd = peak.subtract(value, MC);
                if (dd.compareTo(maxDrawdown) > 0) {
                    maxDrawdown = dd;
                }
            }
        }

        return maxDrawdown.stripTrailingZeros();
    }

    /**
     * Calcula el riesgo de ruina tras cierto número de trades.
     * Aproximación: (q / (p * payoff)) ^ trades
     * donde q = 1 - p, p = winRate, payoff = ratio de ganancia por pérdida
     */
    public BigDecimal calculateRiskOfRuin(BigDecimal winRate,
                                          BigDecimal payoff,
                                          int trades) {
        if (winRate.compareTo(BigDecimal.ZERO) <= 0
                || winRate.compareTo(BigDecimal.ONE)  >= 0
                || payoff.compareTo(BigDecimal.ZERO) <= 0
                || trades <= 0) {
            return BigDecimal.ONE;
        }

        BigDecimal q      = BigDecimal.ONE.subtract(winRate, MC);
        BigDecimal denom  = winRate.multiply(payoff, MC);
        BigDecimal factor = q.divide(denom, MC);

        // pow(int) existe en BigDecimal
        BigDecimal result = factor.pow(trades, MC);
        return result.stripTrailingZeros();
    }
}
