package com.cwdarmm.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OptimalContractServiceTest {

    @Test
    void computesOptimalContracts() {
        OptimalContractService service = new OptimalContractService();
        int contracts = service.calculate(10000.0, 2.5, 4, 12.5);
        assertEquals(5, contracts);
    }
}
