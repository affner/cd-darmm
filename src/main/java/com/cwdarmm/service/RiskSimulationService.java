// src/main/java/com/cwdarmm/service/RiskSimulationService.java
package com.cwdarmm.service;

import com.cwdarmm.model.domain.MarketDefinition;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.repository.MarketDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskSimulationService {

    /**
     * Simula una serie de trades comenzando desde INITIAL (trade 0) y genera lista de resultados.
     */
    public List<RiskResultDTO> simulateTrades(RiskInputDTO req) {
        // Workaround: simulación básica sin acceder a BD
        List<RiskResultDTO> results = new ArrayList<>();
        results.add(buildInitialResult(req));

        // Simular tres trades de ejemplo
        double base = req.getAccountSize();
        for (int i = 1; i <= 3; i++) {
            double newSize = base * (1 + (i % 2 == 1 ? 0.02 : -0.01));
            results.add(RiskResultDTO.builder()
                    .tradeNumber(i)
                    .wl(i % 2 == 1 ? "WIN" : "LOSS")
                    .account(req.getAccount())
                    .marketData(req.getMarketData())
                    .accountSize(newSize)
                    .riskKellyA(req.getRiskReward())
                    .riskKellyB(req.getRiskReward())
                    .build());
        }
        return results;
    }

    private RiskResultDTO buildInitialResult(RiskInputDTO req) {
        return RiskResultDTO.builder()
                .tradeNumber(0)
                .wl("INITIAL")
                .account(req.getAccount())
                .marketData(req.getMarketData())
                .accountSize(req.getAccountSize())
                .riskKellyA(req.getRiskReward())
                .riskKellyB(req.getRiskReward())
                .build();
    }
}
