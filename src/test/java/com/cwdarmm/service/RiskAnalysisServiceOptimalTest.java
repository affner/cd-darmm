package com.cwdarmm.service;

import com.cwdarmm.model.domain.*;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.OptimalContractRow;
import com.cwdarmm.repository.BdMarketRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RiskAnalysisServiceOptimalTest {

    @Test
    void optimalContractsSimpleRange() {
        // Mock BdMarket row
        BdMarket bd = BdMarket.builder()
                .id(1L)
                .account(CatAccount.builder().id(1L).description("NEXGEN").build())
                .market(CatMarket.builder().id(2L).description("S&P 500").build())
                .marketData(CatMarketData.builder().id(3L).description("PROJECTX").build())
                .contract(CatContract.builder().id(3L).description("E-mini S&P 500").build())
                .symbol(CatSymbol.builder().id(4L).symbol("MES").build())
                .multiplier(5)
                .tickSize(0.25)
                .tickValue(1.25)
                .margin(1540.0)
                .commission(1.42)
                .build();

        BdMarketRepository repo = Mockito.mock(BdMarketRepository.class);
        Mockito.when(repo.findOneByMktAccMdata(2L,1L,3L))
                .thenReturn(List.of(bd));

        RiskAnalysisService service = new RiskAnalysisService(repo);

        RiskInputDTO req = RiskInputDTO.builder()
                .account(bd.getAccount())
                .market(bd.getMarket())
                .marketData(bd.getMarketData())
                .accountSize(new BigDecimal("200"))
                .riskReward(2)
                .ticksSl1(1)
                .ticksSl2(2)
                .house(false)
                .lunch(true)
                .win(true)
                .riskPctA(null)
                .riskPctB(new BigDecimal("1.5"))
                .build();

        List<OptimalContractRow> rows = service.generateOptimalContracts(req);
        assertEquals(1, rows.size());
        OptimalContractRow row = rows.get(0);
        assertEquals(1, row.getSlSize());
        assertEquals("MES", row.getFuturesTicker());
        assertEquals(new BigDecimal("1"), row.getOptimalContract());
        assertEquals(3, row.getTargetTicks());
    }
}
