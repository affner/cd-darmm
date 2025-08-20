package com.cwdarmm.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RiskEngineTest {

    @Test
    void kellyBSequence() {
        RiskEngine engine = new RiskEngine(1.5);
        boolean[] outcomes = {true, false, true, true}; // W, L, W, W
        double[] expected = {1.5, 1.575, 1.5435, 1.620675};
        for (int i = 0; i < outcomes.length; i++) {
            double val = engine.calculateKellyB(outcomes[i]);
            assertEquals(expected[i], val, 1e-6);
        }
    }
}
