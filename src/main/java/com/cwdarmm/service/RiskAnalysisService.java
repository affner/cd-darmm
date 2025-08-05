package com.cwdarmm.service;

import com.cwdarmm.model.domain.BdMarket;
import com.cwdarmm.model.domain.CatMarket;
import com.cwdarmm.model.dto.OptimalContractRow;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.repository.BdMarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio que reproduce la lógica de cálculo de riesgo y
 * contratos óptimos descrita en el documento
 * "CW-DARMM: A Probabilistic, Confidence-Weighted Framework for
 * Adaptive Capital Growth".
 *
 * <p>La implementación toma como referencia el módulo VBA
 * <code>risk_market.frm</code> del libro Excel, donde se calculan
 * los tamaños de contrato y se actualizan los porcentajes de
 * riesgo según el resultado del trade. Todas las validaciones y
 * fórmulas se han trasladado a Java para integrarse con la
 * aplicación de escritorio.</p>
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RiskAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(RiskAnalysisService.class);

    /**
     * Valor por defecto si no se especifica el porcentaje de riesgo.
     */
    private static final BigDecimal DEFAULT_RISK_PCT = new BigDecimal("2.5");


    private final BdMarketRepository bdMarketRepo;

    /**
     * Ejecuta la simulación de trades y calcula métricas básicas.
     */
    public List<RiskResultDTO> calculate(RiskInputDTO in) {
        // 1) Preparamos la lista donde se guardarán los resultados
        //    (ver secciones iniciales de docs/GOOD ARTICLE.pdf para una
        //     explicación del flujo de riesgo)
        List<RiskResultDTO> rows = new ArrayList<>();

        // 2) Si es el primer trade, sólo devolvemos INITIAL y salimos
        if (in.isFirstTrade()) {
            rows.add(RiskResultDTO.builder()
                    .tradeNumber(0)
                    .wl("INITIAL")
                    .account(in.getAccount())
                    .marketData(in.getMarketData())
                    .accountSize(in.getAccountSize())
                    .riskKellyA(in.getRiskPctA() == null ? null : in.getRiskPctA().doubleValue())
                    .riskKellyB(in.getRiskPctB() == null ? null : in.getRiskPctB().doubleValue())
                    .build());
            return rows;  // <<< aquí devolvemos sólo INITIAL
        }

        // 3) Según el resultado del trade ajustamos los porcentajes de riesgo
        BigDecimal multiplier = in.isWin() ? new BigDecimal("1.05") : new BigDecimal("0.98");
        BigDecimal newRiskA = in.isHouse() && in.getRiskPctA() != null ? in.getRiskPctA().multiply(multiplier) : null;
        BigDecimal newRiskB = in.isLunch() && in.getRiskPctB() != null ? in.getRiskPctB().multiply(multiplier) : null;

        // 4) Registramos el trade actual, WIN o LOSS
        String wl = in.isWin() ? "WIN" : "LOSS";
        rows.add(RiskResultDTO.builder()
                .tradeNumber(1)
                .wl(wl)
                .account(in.getAccount())
                .marketData(in.getMarketData())
                .accountSize(in.getAccountSize())
                .riskKellyA(newRiskA == null ? null : newRiskA.doubleValue())
                .riskKellyB(newRiskB == null ? null : newRiskB.doubleValue())
                .build());// <<< sólo la fila WIN/LOSS

        // 5) Devolvemos la lista para su visualización o exportación
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
                    "No existe configuración BD_MARKET para " + " market:" +
                            in.getMarket().getId() + " /  Account:" +
                            in.getAccount().getId() + " /  marketData:" +
                            in.getMarketData().getId()
            );
        }

        log.debug("BD_MARKET rows found: {}", rows.size());

        // 2) Ajustamos el riesgo con un multiplicador (compounding/drawdown)
        //    salvo en el primer trade, donde se usa el valor "en crudo" tal
        //    como se indicó en el formulario.
        BigDecimal riskA = in.getRiskPctA();
        BigDecimal riskB = in.getRiskPctB();
        if (!in.isFirstTrade()) {
            BigDecimal multiplier = in.isWin() ? new BigDecimal("1.05") : new BigDecimal("0.98");
            riskA = riskA == null ? null : riskA.multiply(multiplier);
            riskB = riskB == null ? null : riskB.multiply(multiplier);
        }

        // 3) Determinamos el offset para el cálculo de targets según el mercado
        int offset = offsetForMarket(in.getMarket());

        // 4) Generamos una fila para cada "bote" (House/Lunch)
        return Stream.of(
                        in.isHouse() ? bestRowForRisk(rows, in, riskA, offset) : null,
                        in.isLunch() ? bestRowForRisk(rows, in, riskB, offset) : null
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private int offsetForMarket(CatMarket market) {
        String name = market.getDescription();
        return "NASDAQ".equalsIgnoreCase(name) ? 2 : 1;
    }

    private OptimalContractRow bestRowForRisk(List<BdMarket> markets,
                                              RiskInputDTO in,
                                              BigDecimal riskPct,
                                              int offset) {
        // --- Mapeo directo de las celdas de Excel al código Java ---
        // 1) Calcular el riesgo disponible para este trade
        BigDecimal appliedPct = riskPct == null ? DEFAULT_RISK_PCT : riskPct;
        BigDecimal currentRisk = in.getAccountSize()
                .multiply(appliedPct)
                .divide(BigDecimal.valueOf(100), 8, BigDecimal.ROUND_HALF_UP);
        log.debug("appliedPct={}, currentRisk={}", appliedPct, currentRisk);

        // 2) Determinar el tamaño de stop-loss a utilizar.
        int sl = in.getStopLossSize() > 0 ? in.getStopLossSize() :
                (in.getTicksSl1() != null ? in.getTicksSl1() : 0);
        int originalSl = sl;
        if (sl <= 0) {
            sl = 1; // valor de respaldo para evitar divisiones por cero
        }

        BigDecimal bestProfit = null;
        OptimalContractRow bestRow = null;
        BigDecimal decimalTarget = BigDecimal.valueOf(in.getRiskReward())
                .multiply(BigDecimal.valueOf(sl))
                .add(BigDecimal.valueOf(offset));

        for (BdMarket bd : markets) {
            BigDecimal tickValue = BigDecimal.valueOf(bd.getTickValue());
            BigDecimal commission = BigDecimal.valueOf(bd.getCommission());

            BigDecimal riskPerContract = tickValue
                    .multiply(BigDecimal.valueOf(sl))
                    .add(commission);

            if (riskPerContract.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal optContracts = currentRisk.divide(riskPerContract, 0, BigDecimal.ROUND_DOWN);

            BigDecimal potentialProfit = optContracts
                    .multiply(tickValue.multiply(decimalTarget))
                    .subtract(commission.multiply(optContracts));

            if (bestProfit == null || potentialProfit.compareTo(bestProfit) > 0) {
                bestProfit = potentialProfit;
                int targetTicks = Math.round(decimalTarget.floatValue());
                bestRow = new OptimalContractRow(sl, bd.getSymbol().getSymbol(), optContracts, targetTicks);
            }
        }

        int targetTicks = Math.round(decimalTarget.floatValue());
        if (bestRow == null || bestRow.getOptimalContract().compareTo(BigDecimal.ONE) < 0) {
            if (in.isFirstTrade()) {
                log.debug("First trade with insufficient risk, using fallback of 5 contracts");
                return new OptimalContractRow(sl, markets.get(0).getSymbol().getSymbol(),
                        BigDecimal.valueOf(5), targetTicks);
            }
            return new OptimalContractRow(sl,
                    "The risk is too high",
                    null,
                    targetTicks);
        }

        // Si el usuario especificó stopLossSize y era inválido (<=0), mantenerlo en la respuesta
        if (originalSl <= 0) {
            bestRow.setSlSize(originalSl);
        }

        log.debug("Best contract={} contracts={} targetTicks={}",
                bestRow.getFuturesTicker(), bestRow.getOptimalContract(), bestRow.getTargetTicks());
        return bestRow;
    }
}


