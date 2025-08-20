package com.cwdarmm.service;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class OptimalContractServiceTest {
    private final OptimalContractService service = new OptimalContractService();

    @Test
    void calculatesOptimalContracts() {
        int result = service.calculateOptimalContracts(new BigDecimal("20000"), 1.5, 10, 12.5);
        assertEquals(2, result);
    }
}
