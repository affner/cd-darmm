package com.cwdarmm.service;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio con cálculos estadísticos básicos para el manejo de riesgo.
 */
@Service
public class RiskMetricsService {

    /**
     * Calcula la expectativa del sistema.
     * TODO: expectativa = promedio de ganancias × probabilidad – pérdidas × (1 − probabilidad)
     */
    public double calculateExpectancy(List<Double> trades) {
        return 0.0; // pendiente de implementar
    }

    /**
     * Calcula el drawdown máximo de la curva de capital.
     * TODO: drawdown = caída máxima desde un pico a un valle de la curva
     */
    public double calculateDrawdown(List<Double> history) {
        return 0.0; // pendiente de implementar
    }

    /**
     * Calcula el riesgo de ruina.
     * TODO: fórmula basada en probabilidad de ganar, payoff y número de operaciones
     */
    public double calculateRiskOfRuin(double winRate, double payoff, int trades) {
        return 0.0; // pendiente de implementar
    }
}
