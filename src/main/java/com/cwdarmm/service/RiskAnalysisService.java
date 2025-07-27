// RiskAnalysisService.java
package com.cwdarmm.service;

import com.cwdarmm.model.domain.BdMarket;
import com.cwdarmm.model.domain.CatContract;
import com.cwdarmm.model.domain.CatSymbol;
import com.cwdarmm.model.domain.CatMarket;
import com.cwdarmm.model.dto.MarketDbRowDTO;
import com.cwdarmm.model.dto.OptimalContractRow;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.repository.BdMarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RiskAnalysisService {

    /** Valor por defecto si no se especifica el porcentaje de riesgo. */
    private static final BigDecimal DEFAULT_RISK_PCT = new BigDecimal("2.5");


    private final BdMarketRepository bdMarketRepo;

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
                    .riskKellyA(in.getRiskPctA()==null?null:in.getRiskPctA().doubleValue())
                    .riskKellyB(in.getRiskPctB()==null?null:in.getRiskPctB().doubleValue())
                    .build()
            );
        }

        BigDecimal multiplier = in.isWin() ? new BigDecimal("1.05") : new BigDecimal("0.98");
        BigDecimal newRiskA = in.isHouse() && in.getRiskPctA()!=null ? in.getRiskPctA().multiply(multiplier) : null;
        BigDecimal newRiskB = in.isLunch() && in.getRiskPctB()!=null ? in.getRiskPctB().multiply(multiplier) : null;

        String wl = in.isWin() ? "WIN" : "LOSS";

        rows.add(RiskResultDTO.builder()
                .tradeNumber(1)
                .wl(wl)
                .account(in.getAccount())
                .marketData(in.getMarketData())
                .accountSize(in.getAccountSize())
                .riskKellyA(newRiskA==null?null:newRiskA.doubleValue())
                .riskKellyB(newRiskB==null?null:newRiskB.doubleValue())
                .build()
        );

        return rows;
    }

    /**
     * Genera la tabla de Optimal Contracts para la ventana emergente,
     * imitando las fórmulas del XLSM original.
     */
    public List<OptimalContractRow> generateOptimalContracts(RiskInputDTO in) {
        // 1) Traemos todas las filas de bd_markets para esta tupla (market, account, marketData)
        List<BdMarket> rows = bdMarketRepo.findOneByMktAccMdata(
                in.getMarket().getId(),
                in.getAccount().getId(),
                in.getMarketData().getId()
        );
        if (rows.isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe configuración BD_MARKET para " + " market:"+
                            in.getMarket().getId()+ " /  Account:" +
                            in.getAccount().getId() + " /  marketData:" +
                            in.getMarketData().getId()
            );
        }

        // 2) Selección del contrato “principal”
        //    En el XLSM no pedías al usuario elegir “ES” vs “MES”,
        //    así que usamos la fila cuyo multiplier == 1 (si existiera),
        //    o, de lo contrario, la primera de la lista (equivalente al .get(0) previo).
        BdMarket chosen = rows.stream()
                .filter(b -> b.getMultiplier() == 1)
                .findFirst()
                .orElse(rows.get(0));

        // Extraemos aquí los objetos contract y symbol para pasarlos a makeRow:
        CatContract contract = chosen.getContract();
        CatSymbol symbol   = chosen.getSymbol();

        BigDecimal multiplier = in.isWin() ? new BigDecimal("1.05") : new BigDecimal("0.98");
        BigDecimal riskA = in.getRiskPctA()==null?null:in.getRiskPctA().multiply(multiplier);
        BigDecimal riskB = in.getRiskPctB()==null?null:in.getRiskPctB().multiply(multiplier);

        int offset = offsetForMarket(in.getMarket());

        return Stream.of(
                        in.isHouse() ? makeRowRange(in, chosen, riskA, contract, symbol, offset) : null,
                        in.isLunch() ? makeRowRange(in, chosen, riskB, contract, symbol, offset) : null
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private int offsetForMarket(CatMarket market) {
        String name = market.getDescription();
        return "NASDAQ".equalsIgnoreCase(name) ? 2 : 1;
    }

    private OptimalContractRow makeRowRange(RiskInputDTO in,
                                            BdMarket bd,
                                            BigDecimal riskPct,
                                            CatContract contract,
                                            CatSymbol   symbol,
                                            int offset) {
        // --- Mapeo directo de las celdas de Excel al código Java ---
        // 1) currentRisk = AccountSize * RiskPct / 100
        BigDecimal appliedPct = riskPct == null ? DEFAULT_RISK_PCT : riskPct;
        BigDecimal currentRisk = in.getAccountSize()
                .multiply(appliedPct)
                .divide(BigDecimal.valueOf(100), 8, BigDecimal.ROUND_HALF_UP);

        BigDecimal tickValue  = BigDecimal.valueOf(bd.getTickValue());
        BigDecimal commission = BigDecimal.valueOf(bd.getCommission());

        // Rango de SL ticks: usamos los valores ingresados en el formulario
        int start = in.getTicksSl1();
        int end   = in.getTicksSl2() >= start ? in.getTicksSl2() : start;

        BigDecimal bestProfit = null;
        BigDecimal bestContracts = null;
        int bestSl = start;

        for (int sl = start; sl <= end; sl++) {
            BigDecimal riskPerContract = tickValue
                    .multiply(BigDecimal.valueOf(sl))
                    .add(commission);

            if (riskPerContract.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal optContracts = currentRisk
                    .divide(riskPerContract, 0, BigDecimal.ROUND_DOWN);

            int targetTicks = (int) Math.round(in.getRiskReward() * sl + offset);
            BigDecimal potentialProfit = optContracts
                    .multiply(tickValue.multiply(BigDecimal.valueOf(targetTicks)))
                    .subtract(commission.multiply(optContracts));

            if (bestProfit == null || potentialProfit.compareTo(bestProfit) > 0) {
                bestProfit = potentialProfit;
                bestContracts = optContracts;
                bestSl = sl;
            }
        }

        int targetTicks = (int) Math.round(in.getRiskReward() * bestSl + offset);
        String futuresTicker = symbol.getSymbol();

        if (bestContracts == null || bestContracts.compareTo(BigDecimal.ONE) < 0) {
            return new OptimalContractRow(start,
                    "The risk is too high",
                    null,
                    (int)Math.round(in.getRiskReward() * start + offset));
        }

        return new OptimalContractRow(bestSl, futuresTicker, bestContracts, targetTicks);
    }
}


