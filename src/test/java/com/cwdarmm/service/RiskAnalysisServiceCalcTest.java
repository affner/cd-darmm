package com.cwdarmm.service;

import com.cwdarmm.model.domain.CatAccount;
import com.cwdarmm.model.domain.CatMarketData;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.repository.BdMarketRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RiskAnalysisServiceCalcTest {

    private RiskAnalysisService service = new RiskAnalysisService(Mockito.mock(BdMarketRepository.class));

    private RiskInputDTO baseInput() {
        return RiskInputDTO.builder()
                .account(CatAccount.builder().id(1L).description("ACC").build())
                .marketData(CatMarketData.builder().id(2L).description("DATA").build())
                .accountSize(new BigDecimal("1000"))
                .build();
    }

    @Test
    void firstTradeReturnsInitialRow() {
        RiskInputDTO in = baseInput().toBuilder()
                .firstTrade(true)
                .riskPctA(new BigDecimal("2.0"))
                .riskPctB(new BigDecimal("1.0"))
                .build();

        List<RiskResultDTO> rows = service.calculate(in);
        assertEquals(1, rows.size());
        RiskResultDTO r = rows.get(0);
        assertEquals(0, r.getTradeNumber());
        assertEquals("INITIAL", r.getWl());
        assertEquals(new BigDecimal("1000"), r.getAccountSize());
        assertEquals(2.0, r.getRiskKellyA());
        assertEquals(1.0, r.getRiskKellyB());
    }

    @Test
    void winAdjustsRiskPercentages() {
        RiskInputDTO in = baseInput().toBuilder()
                .firstTrade(false)
                .house(true)
                .lunch(true)
                .win(true)
                .riskPctA(new BigDecimal("2.0"))
                .riskPctB(new BigDecimal("1.5"))
                .build();

        List<RiskResultDTO> rows = service.calculate(in);
        assertEquals(1, rows.size());
        RiskResultDTO r = rows.get(0);
        assertEquals(1, r.getTradeNumber());
        assertEquals("WIN", r.getWl());
        assertEquals(2.0 * 1.05, r.getRiskKellyA(), 1e-9);
        assertEquals(1.5 * 1.05, r.getRiskKellyB(), 1e-9);
    }

    @Test
    void lossAdjustsRiskPercentagesDown() {
        RiskInputDTO in = baseInput().toBuilder()
                .firstTrade(false)
                .house(true)
                .lunch(true)
                .win(false)
                .riskPctA(new BigDecimal("3.0"))
                .riskPctB(new BigDecimal("2.0"))
                .build();

        List<RiskResultDTO> rows = service.calculate(in);
        assertEquals(1, rows.size());
        RiskResultDTO r = rows.get(0);
        assertEquals(1, r.getTradeNumber());
        assertEquals("LOSS", r.getWl());
        assertEquals(3.0 * 0.98, r.getRiskKellyA(), 1e-9);
        assertEquals(2.0 * 0.98, r.getRiskKellyB(), 1e-9);
    }
}
