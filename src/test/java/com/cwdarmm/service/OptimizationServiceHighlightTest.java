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

/**
 * Tests that replicate several Excel scenarios provided by the user.
 * They focus on the calculation of the risk percentage and the
 * selection of the highlighted ("subrayado") row.
 */
public class OptimizationServiceHighlightTest {

    private BdMarket buildEs() {
        return BdMarket.builder()
                .id(1L)
                .account(CatAccount.builder().id(1L).description("NEXGEN").build())
                .market(CatMarket.builder().id(1L).description("S&P 500").color1("c1").color2("c2").build())
                .marketData(CatMarketData.builder().id(1L).description("PROJECTX").build())
                .contract(CatContract.builder().id(1L).description("E-mini S&P").build())
                .symbol(CatSymbol.builder().id(1L).symbol("ES").build())
                .tickValue(12.5)
                .commission(2.08)
                .build();
    }

    private BdMarket buildMes(BdMarket es) {
        return BdMarket.builder()
                .id(2L)
                .account(es.getAccount())
                .market(es.getMarket())
                .marketData(es.getMarketData())
                .contract(CatContract.builder().id(2L).description("Micro E-mini S&P").build())
                .symbol(CatSymbol.builder().id(2L).symbol("MES").build())
                .tickValue(1.25)
                .commission(0.74)
                .build();
    }

    /**
     * Example 1 from the user: both symbols yield zero contracts and the first
     * row should be highlighted with a risk percentage of 1.620675.
     */
    @Test
    void highlightFirstRowWhenNoContracts() {
        BdMarket es = buildEs();
        BdMarket mes = buildMes(es);

        BdMarketRepository repo = Mockito.mock(BdMarketRepository.class);
        Mockito.when(repo.findOneByMktAccMdata(1L,1L,1L)).thenReturn(List.of(es, mes));

        OptimizationService svc = new OptimizationService(repo);

        RiskInputDTO req = RiskInputDTO.builder()
                .account(es.getAccount())
                .market(es.getMarket())
                .marketData(es.getMarketData())
                .accountSize(new BigDecimal("100"))
                .riskReward(2.0)
                .ticksSl1(1)
                .ticksSl2(1)
                .house(false)
                .lunch(true)
                .win(true)
                .firstTrade(false)
                .riskPctB(new BigDecimal("1.5435"))
                .build();

        List<ResultRowDTO> rows = svc.calculate(req);
        assertEquals(4, rows.size());

        assertEquals(1.620675, rows.get(0).getRiskPercentage().get(), 0.000001);
        assertEquals(1.720675, rows.get(1).getRiskPercentage().get(), 0.000001);

        long highlighted = rows.stream().filter(r -> r.getOptimalRow().get()).count();
        assertEquals(1, highlighted);
        assertTrue(rows.get(0).getOptimalRow().get());
    }

    /**
     * Example 3 from the user: account size 145, the row with risk 1.5435
     * (symbol MES) must be highlighted.
     */
    @Test
    void highlightMesWithLowerRisk() {
        BdMarket es = buildEs();
        BdMarket mes = buildMes(es);

        BdMarketRepository repo = Mockito.mock(BdMarketRepository.class);
        Mockito.when(repo.findOneByMktAccMdata(1L,1L,1L)).thenReturn(List.of(es, mes));

        OptimizationService svc = new OptimizationService(repo);

        RiskInputDTO req = RiskInputDTO.builder()
                .account(es.getAccount())
                .market(es.getMarket())
                .marketData(es.getMarketData())
                .accountSize(new BigDecimal("145"))
                .riskReward(2.0)
                .ticksSl1(1)
                .ticksSl2(1)
                .house(false)
                .lunch(true)
                .win(true)
                .firstTrade(false)
                .riskPctB(new BigDecimal("1.47"))
                .build();

        List<ResultRowDTO> rows = svc.calculate(req);
        assertEquals(4, rows.size());

        assertEquals(1.543500, rows.get(2).getRiskPercentage().get(), 0.000001);
        assertTrue(rows.get(2).getOptimalRow().get());
    }
}

