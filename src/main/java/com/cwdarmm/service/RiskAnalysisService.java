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

        // b) Fila de resultado del trade actual
        int tradeNum = 1;
        String wl = in.isWin() ? "WIN" : "LOSS";

        rows.add(RiskResultDTO.builder()
                .tradeNumber(tradeNum)
                .wl(wl)
                .account(in.getAccount())
                .marketData(in.getMarketData())
                .accountSize(in.getAccountSize())
                .riskKellyA(in.isHouse() && in.getRiskPctA()!=null ? in.getRiskPctA().doubleValue() : null)
                .riskKellyB(in.isLunch() && in.getRiskPctB()!=null ? in.getRiskPctB().doubleValue() : null)
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
                    "No existe configuración BD_MARKET para " +
                            in.getMarket().getDescription() + " / " +
                            in.getAccount().getDescription() + " / " +
                            in.getMarketData().getDescription()
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

        // 3) Para cada sesión HOUSE / LUNCH, construimos la fila con makeRow(...)
        return Stream.of(
                        in.isHouse() ? makeRow(in, chosen, in.getTicksSl1(), in.getRiskPctA(), contract, symbol) : null,
                        in.isLunch() ? makeRow(in, chosen, in.getTicksSl2(), in.getRiskPctB(), contract, symbol) : null
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private OptimalContractRow makeRow(RiskInputDTO in,
                                       BdMarket bd,
                                       int   slTicks,
                                       BigDecimal riskPct,
                                       CatContract contract,
                                       CatSymbol   symbol) {
        // --- Mapeo directo de las celdas de Excel al código Java ---

        // 1) currentRisk = AccountSize * RiskPct / 100
        //    en Excel era: =C13 * C5/100    (p.ej 5000 * 2.5% = 125)
        BigDecimal appliedPct = riskPct == null ? DEFAULT_RISK_PCT : riskPct;
        BigDecimal currentRisk = in.getAccountSize()
                .multiply(appliedPct)
                .divide(BigDecimal.valueOf(100), 8, BigDecimal.ROUND_HALF_UP);

        // 2) riskPerContract = tickValue * SL_ticks + commission
        //    en Excel: =G14 * C15 + C16    (tickValue * SL_size + comisión)
        BigDecimal tickValue  = BigDecimal.valueOf(bd.getTickValue());
        BigDecimal commission = BigDecimal.valueOf(bd.getCommission());
        BigDecimal riskPerContract = tickValue
                .multiply(BigDecimal.valueOf(slTicks))
                .add(commission);

        // 3) optimalContracts = FLOOR(currentRisk / riskPerContract)
        //    en Excel: =TRUNC( currentRisk / riskPerContract , 0 )
        BigDecimal optimalContracts = BigDecimal.ZERO;
        if (riskPerContract.compareTo(BigDecimal.ZERO) > 0) {
            optimalContracts = currentRisk
                    .divide(riskPerContract, 0, BigDecimal.ROUND_DOWN);
        }

        // 4) targetTicks = SL_ticks * RiskReward + r (offset por mercado)
        int offset = marketOffset(in.getMarket());
        int targetTicks = (int) Math.round(in.getRiskReward() * slTicks + offset);

        // 5) futuresTicker = symbol.getSymbol()
        //    en Excel ponías “ES” o “MES” directamente en la columna Symbol
        String futuresTicker = symbol.getSymbol();

        // 6) Si no alcanza para 1 contrato, mostramos mensaje de “too high risk”
        if (optimalContracts.compareTo(BigDecimal.ONE) < 0) {
            return new OptimalContractRow(
                    slTicks,
                    "The risk is too high",
                    null,
                    targetTicks
            );
        }

        // 7) Devolvemos la fila idéntica a la del XLSM:
        //    (SL_ticks, Symbol, #Contracts, TargetTicks)
        return new OptimalContractRow(
                slTicks,
                futuresTicker,
                optimalContracts,
                targetTicks
        );
    }

    /** Offset usado para el cálculo del target según el mercado. */
    private int marketOffset(CatMarket market) {
        if (market == null) return 1;
        String m = market.getDescription();
        return "NASDAQ".equalsIgnoreCase(m) ? 2 : 1;
    }
}


