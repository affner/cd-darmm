// RiskService.java
package com.cwdarmm.service;

import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RiskService {
    // si necesitas leer definiciones de mercado, inyecta MarketDefinitionRepository u otro
    // private final MarketDefinitionRepository repo;

    public List<String> listAccounts() {
        return List.of("ACC1","ACC2","ACC3");
    }
    public List<String> listMarkets() {
        return List.of("Dow Jones","S&P 500","NASDAQ");
    }
    public List<String> listMarketData() {
        return List.of("Data A","Data B","Data C");
    }

    /**
     * Ejecuta la lógica de cálculo (módulo1.bas) y devuelve una lista de resultados.
     */
    public List<RiskResultDTO> calculate(RiskInputDTO in) {
        // Ejemplo: simulamos N trades (aquí 1 inicial + 5 siguientes)
        return IntStream.rangeClosed(0, 5)
                .mapToObj(i -> {
                    RiskResultDTO r = new RiskResultDTO();
                    r.setTradeNumber(i);
                    r.setWl(i==0 ? "INITIAL" : (i%2==0?"WIN":"LOSS"));
                    r.setAccount(in.getAccount());
                    r.setMarketData(in.getMarketData());
                    r.setAccountSize(in.getAccountSize() * Math.pow(1 + in.getRiskReward()/100, i));
                    r.setRiskKellyA(in.getRiskReward());   // placeholder
                    r.setRiskKellyB(in.getRiskReward()/2); // placeholder
                    return r;
                })
                .collect(Collectors.toList());
    }
}
