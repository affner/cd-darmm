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

    /**
     * The Risk Percentage passed to the service already contains the
     * compounding from the previous trade. The service must not
     * multiply it again; otherwise the displayed percentage would be
     * inflated (e.g. 1.754 instead of 1.675).
     */
    @Test
    void riskPercentageDoesNotDoubleCompound() {
        BdMarket bd = BdMarket.builder()
                .id(1L)
                .account(CatAccount.builder().id(1L).description("NEXGEN").build())
                .market(CatMarket.builder().id(1L).description("NASDAQ").build())
                .marketData(CatMarketData.builder().id(1L).description("PROJECTX").build())
                .contract(CatContract.builder().id(1L).description("E-mini NASDAQ").build())
                .symbol(CatSymbol.builder().id(1L).symbol("NQ").build())
                .tickValue(5.0)
                .commission(1.0)
                .build();

        BdMarketRepository repo = Mockito.mock(BdMarketRepository.class);
        Mockito.when(repo.findOneByMktAccMdata(1L,1L,1L))
                .thenReturn(List.of(bd));

        OptimizationService svc = new OptimizationService(repo);

        // riskPctA ya viene con el compounding aplicado (1.575%)
        RiskInputDTO req = RiskInputDTO.builder()
                .account(bd.getAccount())
                .market(bd.getMarket())
                .marketData(bd.getMarketData())
                .accountSize(new BigDecimal("10000"))
                .riskReward(2.0)
                .ticksSl1(10)
                .house(true)
                .firstTrade(false)
                .win(true) // no debe influir
                .riskPctA(new BigDecimal("1.575"))
                .build();

        List<ResultRowDTO> rows = svc.calculate(req);
        assertEquals(2, rows.size());

        ResultRowDTO base = rows.get(0); // base percentage
        ResultRowDTO withX = rows.get(1); // base + X

        assertEquals(1.575, base.getRiskPercentage().get(), 1e-3);
        assertEquals(1.675, withX.getRiskPercentage().get(), 1e-3);
    }
}

