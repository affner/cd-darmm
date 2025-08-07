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
 * Tests for {@link OptimizationService#calculate(RiskInputDTO)} ensuring
 * the Java implementation mirrors the formulas from risk_market.frm.
 */
public class OptimizationServiceCalculateTest {

    /**
     * When the available risk is insufficient in the first trade, the
     * Excel macro allows trading 5 contracts. The target ticks must
     * respect the configured risk-reward ratio.
     */
    @Test
    void firstTradeUsesFiveContractsAndRespectsTarget() {
        BdMarket bd = BdMarket.builder()
                .id(1L)
                .account(CatAccount.builder().id(1L).description("NEXGEN").build())
                .market(CatMarket.builder().id(1L).description("NASDAQ").build())
                .marketData(CatMarketData.builder().id(1L).description("PROJECTX").build())
                .contract(CatContract.builder().id(1L).description("E-mini NASDAQ").build())
                .symbol(CatSymbol.builder().id(1L).symbol("NQ").build())
                .tickValue(12.5)
                .commission(2.0)
                .build();

        BdMarketRepository repo = Mockito.mock(BdMarketRepository.class);
        Mockito.when(repo.findOneByMktAccMdata(1L,1L,1L))
                .thenReturn(List.of(bd));

        OptimizationService svc = new OptimizationService(repo);

        RiskInputDTO req = RiskInputDTO.builder()
                .account(bd.getAccount())
                .market(bd.getMarket())
                .marketData(bd.getMarketData())
                .accountSize(new BigDecimal("200"))
                .riskReward(1.0)
                .ticksSl1(1)
                .ticksSl2(1)
                .house(true)
                .firstTrade(true)
                .riskPctA(new BigDecimal("2.5"))
                .build();

        List<ResultRowDTO> rows = svc.calculate(req);
        assertEquals(2, rows.size()); // two rows: for k=0 and k=1

        ResultRowDTO row = rows.get(0);
        assertEquals(1, row.getSlSize().get());
        assertEquals(3, row.getTarget().get()); // 1*1 + offset(2)
        assertEquals(5, row.getOptimalContract().get());
    }

    @Test
    void reproducesExcelScenario() {
        // Two contracts for the S&P 500: regular ES and micro MES
        CatAccount account = CatAccount.builder().id(1L).description("NEXGEN").build();
        CatMarket market = CatMarket.builder().id(1L).description("S&P 500").build();
        CatMarketData md = CatMarketData.builder().id(1L).description("PROJECTX").build();

        BdMarket es = BdMarket.builder()
                .id(1L)
                .account(account)
                .market(market)
                .marketData(md)
                .contract(CatContract.builder().id(1L).description("E-mini S&P 500").build())
                .symbol(CatSymbol.builder().id(1L).symbol("ES").build())
                .tickValue(12.5)
                .commission(2.08)
                .build();

        BdMarket mes = BdMarket.builder()
                .id(2L)
                .account(account)
                .market(market)
                .marketData(md)
                .contract(CatContract.builder().id(2L).description("Micro E-mini S&P 500").build())
                .symbol(CatSymbol.builder().id(2L).symbol("MES").build())
                .tickValue(1.25)
                .commission(0.74)
                .build();

        BdMarketRepository repo = Mockito.mock(BdMarketRepository.class);
        Mockito.when(repo.findOneByMktAccMdata(1L,1L,1L))
                .thenReturn(List.of(es, mes));

        OptimizationService svc = new OptimizationService(repo);

        RiskInputDTO req = RiskInputDTO.builder()
                .account(account)
                .market(market)
                .marketData(md)
                .accountSize(new BigDecimal("200"))
                .riskReward(2.0)
                .ticksSl1(1)
                .ticksSl2(1)
                .house(true)
                .firstTrade(false)
                .riskPctA(new BigDecimal("1.575"))
                .build();

        List<ResultRowDTO> rows = svc.calculate(req);
        assertEquals(4, rows.size());

        // ES with risk 1.575
        ResultRowDTO esRow = rows.stream()
                .filter(r -> r.getSymbol().get().equals("ES") && r.getRiskPercentage().get() == 1.575)
                .findFirst().orElseThrow();
        assertEquals(3, esRow.getTarget().get());
        assertEquals(1, esRow.getSlSize().get());
        assertEquals(14.58, esRow.getRiskPerContract().get(), 1e-2);
        assertEquals(0, esRow.getOptimalContract().get());

        // MES with risk 1.575
        ResultRowDTO mesRow = rows.stream()
                .filter(r -> r.getSymbol().get().equals("MES") && r.getRiskPercentage().get() == 1.575)
                .findFirst().orElseThrow();
        assertEquals(3, mesRow.getTarget().get());
        assertEquals(1, mesRow.getSlSize().get());
        assertEquals(1.99, mesRow.getRiskPerContract().get(), 1e-2);
        assertEquals(1, mesRow.getOptimalContract().get());
        assertEquals(1.99, mesRow.getCapitalUsed().get(), 1e-2);
        assertEquals(0.995, mesRow.getRealRisk().get(), 1e-3);
        assertEquals(3.01, mesRow.getPotentialProfit().get(), 1e-2);
        assertEquals(1.99, mesRow.getPotentialLoss().get(), 1e-2);
    }
}

