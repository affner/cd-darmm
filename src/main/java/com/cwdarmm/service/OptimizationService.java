package com.cwdarmm.service;

import com.cwdarmm.model.domain.BdMarket;
import com.cwdarmm.model.dto.ResultRowDTO;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.repository.BdMarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OptimizationService {

    private final BdMarketRepository bdMarketRepo;

    /* ===== Utilidades de redondeo idénticas al Excel ===== */
    private static double r2(double v) {
        return new BigDecimal(v).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    private static double r3(double v) {
        return new BigDecimal(v).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    private static double r6(double v) {
        return new BigDecimal(v).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private static int offsetFor(String marketDescription) {
        return "NASDAQ".equalsIgnoreCase(marketDescription) ? 2 : 1; // S&P500 → 1
    }

    /**
     * Calcula la tabla Results replicando las fórmulas del Excel.
     * - Usa SOLO ticksSl1 (como el libro).
     * - Target se calcula igual que en Optimal Contracts.
     * - Resta comisiones en el profit exactamente como en el libro.
     */
    public List<ResultRowDTO> calculate(RiskInputDTO in) {
        // Traer configuraciones de mercado (tickValue, commission, symbol)
        List<BdMarket> rows = bdMarketRepo.findOneByMktAccMdata(
                in.getMarket().getId(),
                in.getAccount().getId(),
                in.getMarketData().getId()
        );
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("No BD_MARKET rows for selection");
        }

        List<ResultRowDTO> results = new ArrayList<>();
        // Seguimos el mismo criterio que el Excel para resaltar la fila óptima:
        // se selecciona la que tenga mayor beneficio potencial sin redondear.
        // Si hubiera empate en el beneficio, se escoge la de mayor porcentaje de riesgo.
        ResultRowDTO bestRow = null;
        double bestProfitRaw = Double.NEGATIVE_INFINITY;
        double bestRiskPctRaw = Double.NEGATIVE_INFINITY;

        // Excel usa solo el primer SL:
        final int sl = in.getTicksSl1();

        // Offset por mercado (S&P=1, NASDAQ=2)
        final int offset = offsetFor(in.getMarket().getDescription());

        // Risk-to-Reward (si viniera inválido, usar 2 como en tus capturas)
        double rr = in.getRiskReward();
        if (Double.isNaN(rr) || rr <= 0) rr = 2.0;

        // Target consistente con la ventana de Optimal Contracts
        final int targetTicks = (int) Math.round(rr * sl + offset);

        // Aplicar compounding/drawdown al porcentaje de riesgo si no es el primer trade
        BigDecimal riskA = in.getRiskPctA();
        BigDecimal riskB = in.getRiskPctB();
//        if (!in.isFirstTrade()) {
//            BigDecimal multiplier = in.isWin() ? new BigDecimal("1.05") : new BigDecimal("0.98");
//            riskA = riskA == null ? null : riskA.multiply(multiplier);
//            riskB = riskB == null ? null : riskB.multiply(multiplier);
//        }

        // Lista de porcentajes base activos (Kelly A/B) + variación "x"
        List<BigDecimal> baseRiskPcts = new ArrayList<>();
        if (in.isHouse() && riskA != null) baseRiskPcts.add(riskA);
        if (in.isLunch() && riskB != null) baseRiskPcts.add(riskB);
        if (baseRiskPcts.isEmpty()) baseRiskPcts.add(BigDecimal.ZERO);

        for (BigDecimal basePct : baseRiskPcts) {
            // Los porcentajes de riesgo que llegan aquí ya fueron
            // ajustados por compounding/drawdown en RiskAnalysisService,
            // por lo que no debemos volver a multiplicarlos.
            double pctBase = basePct.doubleValue();
            double x = pctBase < 1.0 ? 0.03 : 0.10; // en tus capturas: 1.500% → 1.575% y 1.675%

            for (BdMarket bd : rows) {
                final double tickValue   = bd.getTickValue();
                final double commission  = bd.getCommission();
                final String symbol      = bd.getSymbol().getSymbol();

                // k=0 -> base, k=1 -> base + x
                for (int k = 0; k < 2; k++) {
                    final double riskPctApplied = pctBase + (x * k);   // 1.575 / 1.675, etc.
                    final double accountSize    = in.getAccountSize().doubleValue();
                    final double currentRiskUSD = accountSize * (riskPctApplied / 100.0);

                    // Riesgo por contrato = SL * tickValue + commission
                    final double riskPerContract = sl * tickValue + commission;

                    // Contratos óptimos = floor(currentRiskUSD / riskPerContract)
                    int optimal = 0;
                    if (riskPerContract > 0) {
                        optimal = (int) Math.floor(currentRiskUSD / riskPerContract);
                    }
                    // En el primer trade el Excel permite iniciar con 5 contratos
                    // aunque el cálculo previo arroje menos de 1.
                    if (in.isFirstTrade() && optimal < 1) {
                        optimal = 5;
                    }

                    // Capital usado y métricas derivadas
                    final double capitalUsed   = optimal * riskPerContract;
                    final double realRiskPct   = accountSize == 0 ? 0.0 : (capitalUsed * 100.0 / accountSize);
                    final double potentialLoss = capitalUsed;  // = optimal * riskPerContract
                    final double potentialProfit = (optimal * (tickValue * targetTicks)) - (commission * optimal);

                    // Armar fila de Results
                    ResultRowDTO row = new ResultRowDTO();
                    row.getAsset().set(in.getMarket().getDescription());
                    row.getBroker().set(in.getAccount().getDescription());
                    row.getSymbol().set(symbol);

                    // *** Aquí estaba el problema del "2": ahora forzamos el mismo cálculo que Optimal Contracts ***
                    row.getTarget().set(targetTicks);

                    row.getSlSize().set(sl);
                    row.getRiskPerContract().set(r2(riskPerContract));
                    row.getOptimalContract().set(optimal);
                    row.getCapitalUsed().set(r2(capitalUsed));
                    row.getRealRisk().set(r3(realRiskPct));                   // 0.995
                    row.getPotentialProfit().set(r2(potentialProfit));        // 3.01
                    row.getPotentialLoss().set(r2(potentialLoss));            // 1.99
                    row.getRiskPercentage().set(r6(riskPctApplied));          // 1.575000 / 1.675000

                    // Color por símbolo (micro vs grande)
                    String c1 = in.getMarket().getColor1();
                    String c2 = in.getMarket().getColor2();
                    String chosen = (symbol != null && symbol.startsWith("M")) ? c1 : c2;
                    row.getRowColor().set(chosen);

                    // Determinar si esta es la mejor fila según el beneficio sin redondear
                    if (potentialProfit > bestProfitRaw ||
                            (Double.compare(potentialProfit, bestProfitRaw) == 0 &&
                                    riskPctApplied > bestRiskPctRaw)) {
                        bestProfitRaw = potentialProfit;
                        bestRiskPctRaw = riskPctApplied;
                        bestRow = row;
                    }

                    results.add(row);
                }
            }
        }

        // Marcar la fila con mayor beneficio como óptima
        if (bestRow != null) {
            bestRow.getOptimalRow().set(true);
        }

        return results;
    }
}
