package com.cwdarmm.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RiskMetricsServiceTest {

    private static final MathContext MC = MathContext.DECIMAL64;
    private final RiskMetricsService service = new RiskMetricsService();

    @Test
    void calculateExpectancyBasic() {
        // trades: +1, -1  ⇒ pWin = 0.5, avgWin=1, avgLoss=1
        // E = (1*0.5) - (1*(1-0.5)) = 0
        List<BigDecimal> trades = List.of(
                BigDecimal.ONE,
                BigDecimal.ONE.negate()
        );
        BigDecimal expectancy = service.calculateExpectancy(trades);
        assertEquals(0, expectancy.compareTo(BigDecimal.ZERO));
    }

    @Test
    void calculateDrawdownBasic() {
        // equity curve: 100 → 95 (dd=5) → 105 (new peak) ⇒ maxDrawdown = 5
        List<BigDecimal> curve = List.of(
                new BigDecimal("100"),
                new BigDecimal("95"),
                new BigDecimal("105")
        );
        BigDecimal dd = service.calculateDrawdown(curve);
        assertEquals(0, dd.compareTo(new BigDecimal("5")));
    }

    @Test
    void calculateRiskOfRuinBasic() {
        // winRate=0.5, payoff=1.5 ⇒ q=0.5, factor=0.5/(0.5*1.5)=2/3
        // trades=10 ⇒ (2/3)^10 ≈ 0.01734152991583261
        BigDecimal winRate = new BigDecimal("0.5");
        BigDecimal payoff = new BigDecimal("1.5");
        int trades = 10;

        BigDecimal ror = service.calculateRiskOfRuin(winRate, payoff, trades);
        // construimos el valor esperado con el mismo MC
        BigDecimal expected = BigDecimal.valueOf(2)
                .divide(BigDecimal.valueOf(3), MC)
                .pow(trades, MC)
                .stripTrailingZeros();

        assertEquals(0, ror.compareTo(expected));
    }

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
