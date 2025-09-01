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
 * Verifica que la fila resaltada en la pestaña "Results" coincida con el
 * porcentaje de riesgo base (sin la variación "x") para distintos saldos de
 * cuenta y secuencias de resultados (WIN/LOSS).
 */
public class ResultHighlightTest {

    private BdMarketRepository repoWithESAndMES() {
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
        return repo;
    }

    private ResultRowDTO highlightedRow(List<ResultRowDTO> rows) {
        return rows.stream().filter(r -> r.getOptimalRow().get()).findFirst().orElseThrow();
    }

    @Test
    void highlightUsesBaseRiskForDifferentAccountSizes() {
        OptimizationService svc = new OptimizationService(repoWithESAndMES());

        // Escenario 1: saldo $200, primer WIN → riesgo B = 1.575%
        RiskInputDTO req1 = RiskInputDTO.builder()
                .account(CatAccount.builder().id(1L).description("NEXGEN").build())
                .market(CatMarket.builder().id(1L).description("S&P 500").color1("c1").color2("c2").build())
                .marketData(CatMarketData.builder().id(1L).description("PROJECTX").build())
                .accountSize(new BigDecimal("200"))
                .riskReward(2.0)
                .ticksSl1(1)
                .lunch(true)
                .riskPctB(new BigDecimal("1.575"))
                .build();
        List<ResultRowDTO> rows1 = svc.calculate(req1);
        assertEquals(1.575, highlightedRow(rows1).getRiskPercentage().get());

        // Escenario 2: saldo $145 tras un LOSS → riesgo B = 1.5435%
        RiskInputDTO req2 = req1.toBuilder()
                .accountSize(new BigDecimal("145"))
                .riskPctB(new BigDecimal("1.5435"))
                .build();
        List<ResultRowDTO> rows2 = svc.calculate(req2);
        assertEquals(1.5435, highlightedRow(rows2).getRiskPercentage().get());

        // Escenario 3: saldo $100 tras WIN → riesgo B = 1.620675%
        RiskInputDTO req3 = req1.toBuilder()
                .accountSize(new BigDecimal("100"))
                .riskPctB(new BigDecimal("1.620675"))
                .build();
        List<ResultRowDTO> rows3 = svc.calculate(req3);
        assertEquals(1.620675, highlightedRow(rows3).getRiskPercentage().get());
    }
}
