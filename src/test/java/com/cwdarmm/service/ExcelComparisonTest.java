package com.cwdarmm.service;

import com.cwdarmm.model.domain.*;
import com.cwdarmm.model.dto.ResultRowDTO;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.repository.BdMarketRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExcelComparisonTest {
    @Test
    void scenarioFromUser() {
        BdMarket es = BdMarket.builder()
                .id(1L)
                .account(CatAccount.builder().id(1L).description("NEXGEN").build())
                .market(CatMarket.builder().id(1L).description("S&P 500").color1("c1").color2("c2").build())
                .marketData(CatMarketData.builder().id(1L).description("PROJECTX").build())
                .contract(CatContract.builder().id(1L).description("E-mini S&P").build())
                .symbol(CatSymbol.builder().id(1L).symbol("ES").build())
                .tickValue(12.5)
                .commission(2.08)
                .build();
        BdMarket mes = BdMarket.builder()
                .id(2L)
                .account(es.getAccount())
                .market(es.getMarket())
                .marketData(es.getMarketData())
                .contract(CatContract.builder().id(2L).description("Micro E-mini S&P").build())
                .symbol(CatSymbol.builder().id(2L).symbol("MES").build())
                .tickValue(1.25)
                .commission(0.74)
                .build();

        BdMarketRepository repo = Mockito.mock(BdMarketRepository.class);
        Mockito.when(repo.findOneByMktAccMdata(1L,1L,1L)).thenReturn(List.of(es, mes));

        OptimizationService svc = new OptimizationService(repo);

        RiskInputDTO req = RiskInputDTO.builder()
                .account(es.getAccount())
                .market(es.getMarket())
                .marketData(es.getMarketData())
                .accountSize(new BigDecimal("200"))
                .riskReward(2.0)
                .ticksSl1(1)
                .ticksSl2(1)
                .house(false)
                .lunch(true)
                .win(true)
                .firstTrade(false)
                .riskPctB(new BigDecimal("1.5"))
                .build();

        List<ResultRowDTO> rows = svc.calculate(req);
        assertEquals(4, rows.size());

        // first row correspond to ES riskPct 1.575
        ResultRowDTO row0 = rows.get(0);
        assertEquals("S&P 500", row0.getAsset().get());
        assertEquals("NEXGEN", row0.getBroker().get());
        assertEquals("ES", row0.getSymbol().get());
        assertEquals(3, row0.getTarget().get());
        assertEquals(1, row0.getSlSize().get());
        assertEquals(0, row0.getOptimalContract().get());
        assertEquals(1.575, row0.getRiskPercentage().get());

        ResultRowDTO row2 = rows.get(2); // MES riskPct 1.575
        assertEquals("MES", row2.getSymbol().get());
        assertEquals(1, row2.getOptimalContract().get());
        assertEquals(1.99, row2.getRiskPerContract().get(), 0.01);
        assertEquals(1.575, row2.getRiskPercentage().get());

        // Solo la fila con mayor Profit y menor porcentaje de riesgo debe resaltarse
        long highlighted = rows.stream().filter(r -> r.getOptimalRow().get()).count();
        assertEquals(1, highlighted);
        ResultRowDTO highlightedRow = rows.stream()
                .filter(r -> r.getOptimalRow().get())
                .findFirst().orElseThrow();
        assertEquals("MES", highlightedRow.getSymbol().get());
        assertEquals(1.575, highlightedRow.getRiskPercentage().get());
    }
}
