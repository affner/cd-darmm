package com.cwdarmm.service;

import com.cwdarmm.model.domain.BdMarket;
import com.cwdarmm.model.domain.CatAccount;
import com.cwdarmm.model.domain.CatMarket;
import com.cwdarmm.model.domain.CatMarketData;
import com.cwdarmm.model.domain.CatContract;
import com.cwdarmm.model.domain.CatSymbol;
import com.cwdarmm.model.dto.ResultRowDTO;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.repository.BdMarketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OptimizationServiceTest {

    private BdMarketRepository repo;
    private OptimizationService service;

    private BdMarket es;
    private BdMarket mes;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(BdMarketRepository.class);
        service = new OptimizationService(repo);

        CatSymbol symES = CatSymbol.builder().symbol("ES").build();
        CatSymbol symMES = CatSymbol.builder().symbol("MES").build();
        CatContract contract = CatContract.builder().id(1L).build();
        CatAccount account = CatAccount.builder().id(1L).description("NEXGEN").initialSize(100.0).build();
        CatMarket market = CatMarket.builder().id(2L).description("S&P 500").build();
        CatMarketData marketData = CatMarketData.builder().id(3L).description("PROJECTX").build();

        es = BdMarket.builder()
                .account(account)
                .market(market)
                .marketData(marketData)
                .contract(contract)
                .symbol(symES)
                .tickValue(12.5)
                .commission(2.08)
                .build();

        mes = BdMarket.builder()
                .account(account)
                .market(market)
                .marketData(marketData)
                .contract(contract)
                .symbol(symMES)
                .tickValue(1.25)
                .commission(0.74)
                .build();

        when(repo.findOneByMktAccMdata(anyLong(), anyLong(), anyLong()))
                .thenReturn(List.of(es, mes));
    }

    private RiskInputDTO baseInput(double accountSize, double risk, double rr) {
        return RiskInputDTO.builder()
                .market(es.getMarket())
                .account(es.getAccount())
                .marketData(es.getMarketData())
                .accountSize(BigDecimal.valueOf(accountSize))
                .riskReward(rr)
                .ticksSl1(1)
                .house(true)
                .riskPctA(BigDecimal.valueOf(risk))
                .build();
    }

    @Test
    void dataset1() {
        RiskInputDTO in = baseInput(100, 1.620675, 1);
        List<ResultRowDTO> rows = service.calculate(in);
        assertEquals(4, rows.size());
        assertEquals(1.620675, rows.get(0).getRiskPercentage().get());
        assertTrue(rows.get(0).getOptimalRow().get());
    }

    @Test
    void dataset2() {
        RiskInputDTO in = baseInput(200, 1.575, 2);
        List<ResultRowDTO> rows = service.calculate(in);
        assertEquals(4, rows.size());
        assertEquals(1.575, rows.get(2).getRiskPercentage().get());
        assertTrue(rows.get(2).getOptimalRow().get());
    }

    @Test
    void dataset3() {
        RiskInputDTO in = baseInput(145, 1.5435, 1);
        List<ResultRowDTO> rows = service.calculate(in);
        assertEquals(4, rows.size());
        assertEquals(1.5435, rows.get(2).getRiskPercentage().get());
        assertTrue(rows.get(2).getOptimalRow().get());
    }
}
