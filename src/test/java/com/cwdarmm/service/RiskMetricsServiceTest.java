package com.cwdarmm.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class RiskMetricsServiceTest {

    private final RiskMetricsService service = new RiskMetricsService();

    @Test
    void methodsShouldNotThrow() {
        Assertions.assertDoesNotThrow(() -> service.calculateExpectancy(List.of(1.0, -1.0)));
        Assertions.assertDoesNotThrow(() -> service.calculateDrawdown(List.of(100.0, 95.0, 105.0)));
        Assertions.assertDoesNotThrow(() -> service.calculateRiskOfRuin(0.5, 1.5, 10));
    }
}
