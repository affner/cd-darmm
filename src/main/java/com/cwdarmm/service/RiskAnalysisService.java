// RiskAnalysisService.java
package com.cwdarmm.service;

import com.cwdarmm.model.dto.OptimalContractRow;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.service.MarketDbService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.math.RoundingMode.DOWN;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskAnalysisService {

    private final MarketDbService marketDbService;


    /**
     * Ejecuta la simulación de trades y calcula métricas básicas.
     */
    public List<RiskResultDTO> calculate(RiskInputDTO in) {
        List<RiskResultDTO> rows = new ArrayList<>();

        // a) INITIAL (solo si el usuario dijo que sí es el primer trade)
        if (in.isFirstTrade()) {
            rows.add(RiskResultDTO.builder()
                    .tradeNumber(0)
                    .wl("INITIAL")
                    .account(in.getAccount())
                    .marketData(in.getMarketData())
                    .accountSize(in.getAccountSize())
                    .riskKellyA(in.getTicksSl1().doubleValue())
                    .riskKellyB(in.getTicksSl2().doubleValue())
                    .build()
            );
        }

        // b) Cálculo de “Optimal Contracts” para la sesión seleccionada
        int tradeNum = 1;
        int slTicks = in.isHouse() ? in.getTicksSl1() : in.getTicksSl2();
        var row = marketDbService.find(
                in.getMarket().getName(),
                in.getAccount().getName(),
                in.getMarketData().getName());
        if (row != null) {
            double riskPerContract = row.getTickValue().get() * slTicks
                    + row.getCommission().get();
            double currentRisk = in.getAccountSize().doubleValue() * 0.01; // 1% del capital
            BigDecimal contracts = BigDecimal.valueOf(currentRisk / riskPerContract)
                    .setScale(0, DOWN);

            String wl = in.isWin() ? "WIN" : "LOSS";

            rows.add(RiskResultDTO.builder()
                    .tradeNumber(tradeNum)
                    .wl(wl)
                    .account(in.getAccount())
                    .marketData(in.getMarketData())
                    .accountSize(contracts)
                    .riskKellyA(in.isHouse() ? (double) slTicks : null)
                    .riskKellyB(in.isLunch() ? (double) slTicks : null)
                    .build()
            );
        }

        return rows;
    }

    /**
     * Genera la tabla de Optimal Contracts para la ventana emergente.
     */
    public List<OptimalContractRow> generateOptimalContracts(RiskInputDTO in) {
        List<OptimalContractRow> out = new ArrayList<>();

        // Por cada sesión seleccionada, calculamos su fila
        if (in.isHouse()) {
            out.add(makeRow(in, in.getTicksSl1()));
        }
        if (in.isLunch()) {
            out.add(makeRow(in, in.getTicksSl2()));
        }

        return out;
    }

    private OptimalContractRow makeRow(RiskInputDTO in, int slTicks) {
        var row = marketDbService.find(
                in.getMarket().getName(),
                in.getAccount().getName(),
                in.getMarketData().getName());
        if (row == null) {
            return new OptimalContractRow(slTicks,
                    "N/A",
                    null,
                    0);
        }

        double riskPerContract = row.getTickValue().get() * slTicks
                + row.getCommission().get();
        double currentRisk = in.getAccountSize().doubleValue() * 0.01; // 1% por trade
        BigDecimal possible = BigDecimal.valueOf(currentRisk / riskPerContract)
                .setScale(0, DOWN);

        int r = switch (in.getMarket().getName()) {
            case "NASDAQ" -> 2;
            default -> 1;
        };
        int target = (int) Math.round(slTicks * in.getRiskReward() + r);

        if (possible.compareTo(BigDecimal.ONE) < 0) {
            return new OptimalContractRow(
                    slTicks,
                    "The risk is too high for this stop-loss size", null, target);
        }

        return new OptimalContractRow(
                slTicks,
                row.getSymbol().get(),
                possible,
                target
        );
    }
}
