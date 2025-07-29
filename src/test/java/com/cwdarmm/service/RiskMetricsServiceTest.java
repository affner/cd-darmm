package com.cwdarmm.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Esta clase prueba tres métricas fundamentales que se usan en modelos de riesgo como CW-DARMM:
 *  Expectancy (esperanza matemática)
 *  Drawdown máximo (pérdida desde el pico)
 *  Riesgo de Ruina (probabilidad de quebrar)
 *
 * Estas métricas ayudan a decidir si una estrategia vale la pena,
 * si es peligrosa o si es estable estadísticamente.
 */
class RiskMetricsServiceTest {

    // Contexto matemático para precisión decimal (más exacto que double)
    private static final MathContext MC = MathContext.DECIMAL64;
    private final RiskMetricsService service = new RiskMetricsService();

    /**
     *  Test: calcular la esperanza matemática (expectancy)
     * trades: +1, -1 ⇒ ganancia de 1, pérdida de 1
     * Probabilidad de ganar = 0.5, recompensa = 1, pérdida = 1
     *
     * Fórmula: E = P(win)·avgWin − P(loss)·avgLoss
     * E = 0.5·1 − 0.5·1 = 0
     * Esto indica que la estrategia no gana ni pierde a largo plazo.
     */
    @Test
    void calculateExpectancyBasic() {
        List<BigDecimal> trades = List.of(
                BigDecimal.ONE,         // Ganancia
                BigDecimal.ONE.negate() // Pérdida
        );
        BigDecimal expectancy = service.calculateExpectancy(trades);
        assertEquals(0, expectancy.compareTo(BigDecimal.ZERO));
    }

    /**
     *  Test: calcular el drawdown máximo
     * Supongamos que la cuenta sube y baja así:
     *   100 → 95 → 105
     *   Desde el pico de 100 baja a 95 (drawdown = 5),
     *   luego sube a 105 (nuevo pico, pero el drawdown anterior ya ocurrió).
     *
     * Resultado esperado: 5 (la peor caída desde un pico).
     */
    @Test
    void calculateDrawdownBasic() {
        List<BigDecimal> curve = List.of(
                new BigDecimal("100"),
                new BigDecimal("95"),
                new BigDecimal("105")
        );
        BigDecimal dd = service.calculateDrawdown(curve);
        assertEquals(0, dd.compareTo(new BigDecimal("5")));
    }

    /**
     *  Test: calcular riesgo de ruina
     * Fórmula: RoR ≈ [(1−E)/(1+E)]^n
     * Alternativa con winRate y payoff:
     *    q = 1 - p
     *    factor = q / (p * payoff)
     *    RoR = factor ^ trades
     *
     * En este ejemplo:
     *   - Win rate = 0.5 (50%)
     *   - Payoff = 1.5 (ganas 1.5 por cada 1 que arriesgas)
     *   - Trades = 10
     *
     * factor = 0.5 / (0.5 * 1.5) = 2/3
     * RoR = (2/3)^10 ≈ 0.0173 (1.7%)
     * Muy bajo = estrategia bastante segura.
     */
    @Test
    void calculateRiskOfRuinBasic() {
        BigDecimal winRate = new BigDecimal("0.5");
        BigDecimal payoff = new BigDecimal("1.5");
        int trades = 10;

        BigDecimal ror = service.calculateRiskOfRuin(winRate, payoff, trades);

        // Calculamos el valor esperado manualmente con precisión
        BigDecimal expected = BigDecimal.valueOf(2)
                .divide(BigDecimal.valueOf(3), MC)
                .pow(trades, MC)
                .stripTrailingZeros();

        assertEquals(0, ror.compareTo(expected));
    }

    /**
     *  Test de robustez
     * Asegura que los métodos no fallen ni lancen excepciones con datos válidos.
     * No importa el valor del resultado aquí, solo se verifica que el sistema no "crashee".
     */
    @Test
    void methodsShouldNotThrow() {
        assertDoesNotThrow(() ->
                service.calculateExpectancy(List.of(
                        new BigDecimal("1.0"),
                        new BigDecimal("-1.0")
                ))
        );
        assertDoesNotThrow(() ->
                service.calculateDrawdown(List.of(
                        new BigDecimal("100.0"),
                        new BigDecimal("95.0"),
                        new BigDecimal("105.0")
                ))
        );
        assertDoesNotThrow(() ->
                service.calculateRiskOfRuin(
                        new BigDecimal("0.5"),
                        new BigDecimal("1.5"),
                        5
                )
        );
    }
}
