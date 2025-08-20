package com.cwdarmm.service;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RiskEngineTest {

    @Test
    void sequenceMatchesVbaLogic() {
        RiskEngine engine = new RiskEngine();
        double base = 1.5;
        double prev = 0;
        boolean[] outcomes = {true, false, true, true}; // W, L, W, W

        List<Double> values = new ArrayList<>();
        for (int i = 0; i < outcomes.length; i++) {
            boolean prevWin = (i == 0) ? true : outcomes[i - 1];
            prev = engine.calculateKellyB(prev, prevWin, base);
            values.add(prev);
        }

        double[] expected = {1.5, 1.575, 1.5435, 1.620675};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], values.get(i), 1e-6);
        }
    }
}
