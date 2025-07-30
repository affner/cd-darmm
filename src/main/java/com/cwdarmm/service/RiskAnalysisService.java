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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RiskAnalysisService {

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

        // 2) Selección del contrato “principal”
        //    En el XLSM no pedías al usuario elegir “ES” vs “MES”,
        //    así que usamos la fila cuyo multiplier == 1 (si existiera),
        //    o, de lo contrario, la primera de la lista (equivalente al .get(0) previo).
        // 1) Elegir primero el "micro" (MES, M2K, etc.) por tickValue más pequeño:
        // 2) Selección: elegimos siempre el micro (symbol empieza por "M") o, si no, el de menor tickValue
        // 2) Selección del contrato “principal”
        BdMarket chosen;
        if (in.isFirstTrade()) {
            // Primer trade: elige siempre el “grande”, es decir, el que NO empieza con 'M'
            chosen = rows.stream()
                    .filter(r -> !r.getSymbol().getSymbol().startsWith("M"))
                    .findFirst()
                    .orElse(rows.get(0));
        } else {
            // Trades siguientes: usas tu lógica de micro / tickValue …
            chosen = rows.stream()
                    .filter(r -> r.getSymbol().getSymbol().startsWith("M"))
                    .findFirst()
                    .orElse(rows.stream()
                            .min(Comparator.comparing(BdMarket::getTickValue))
                            .orElse(rows.get(0)));
        }

        // Extraemos aquí los objetos contract y symbol para pasarlos a makeRow:
        CatContract contract = chosen.getContract();
        CatSymbol symbol = chosen.getSymbol();

        // 3) Ajustamos el riesgo con un multiplicador (compounding/drawdown)
        //    salvo en el primer trade, donde se usa el valor "en crudo" tal
        //    como se indicó en el formulario.
        BigDecimal riskA = in.getRiskPctA();
        BigDecimal riskB = in.getRiskPctB();
        if (!in.isFirstTrade()) {
            BigDecimal multiplier = in.isWin() ? new BigDecimal("1.05") : new BigDecimal("0.98");
            riskA = riskA == null ? null : riskA.multiply(multiplier);
            riskB = riskB == null ? null : riskB.multiply(multiplier);
        }

        // 4) Determinamos el offset para el cálculo de targets según el mercado
        int offset = offsetForMarket(in.getMarket());

        // 5) Generamos una fila para cada "bote" (House/Lunch)
        return Stream.of(
                        in.isHouse() ? makeRowRange(in, chosen, riskA, contract, symbol, offset, in.isFirstTrade()) : null,
                        in.isLunch() ? makeRowRange(in, chosen, riskB, contract, symbol, offset, in.isFirstTrade()) : null
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
                                            CatSymbol symbol,
                                            int offset,
                                            boolean firstTrade) {
        // --- Mapeo directo de las celdas de Excel al código Java ---
        // 1) Calcular el riesgo disponible para este trade
        //    (sección "Risk per trade" en docs/GOOD ARTICLE.pdf)
        BigDecimal appliedPct = riskPct == null ? DEFAULT_RISK_PCT : riskPct;
        BigDecimal currentRisk = in.getAccountSize()
                .multiply(appliedPct)
                .divide(BigDecimal.valueOf(100), 8, BigDecimal.ROUND_HALF_UP);

        BigDecimal tickValue = BigDecimal.valueOf(bd.getTickValue());
        BigDecimal commission = BigDecimal.valueOf(bd.getCommission());

        // 2) Establecemos el rango de SL ticks a evaluar
        //    según lo introducido en la ventana de configuración
        int start = in.getTicksSl1();
        int end = Math.max(in.getTicksSl2(), start);

        BigDecimal bestProfit = null;
        BigDecimal bestContracts = null;
        int bestSl = start;

        // 3) Recorremos cada posible SL buscando la mejor relación
        for (int sl = start; sl <= end; sl++) {
            // 3.a) Riesgo por contrato = ticks SL * tickValue.
            //     En trades posteriores sumamos la comisión, pero para el
            //     primer trade se utiliza la fórmula "pura" del artículo.
            BigDecimal riskPerContract = tickValue
                    .multiply(BigDecimal.valueOf(sl));
            if (!firstTrade) {
                riskPerContract = riskPerContract.add(commission);
            }

            if (riskPerContract.compareTo(BigDecimal.ZERO) <= 0) continue;

            // 3.b) Número óptimo de contratos = floor(currentRisk / riskPerContract)
            BigDecimal optContracts = currentRisk
                    .divide(riskPerContract, 0, BigDecimal.ROUND_DOWN);

            // 3.c) Ticks objetivo en formato decimal.
            //     En el XLSM el valor se utiliza sin redondear para calcular
            //     el beneficio potencial y luego se trunca al mostrarlo.
            BigDecimal decimalTarget = BigDecimal.valueOf(in.getRiskReward())
                    .multiply(BigDecimal.valueOf(sl))
                    .add(BigDecimal.valueOf(offset));

            // 3.d) Beneficio potencial restando comisiones
            BigDecimal potentialProfit = optContracts
                    .multiply(tickValue.multiply(decimalTarget))
                    .subtract(commission.multiply(optContracts));

            // 3.e) Guardamos el mejor SL encontrado
            if (bestProfit == null || potentialProfit.compareTo(bestProfit) > 0) {
                bestProfit = potentialProfit;
                bestContracts = optContracts;
                bestSl = sl;
            }
        }

        // ——————————————————————————————
        // REDONDEO del target **igual que en VBA** (no truncar)
        BigDecimal bestDecimalTarget = BigDecimal.valueOf(in.getRiskReward())
                .multiply(BigDecimal.valueOf(bestSl))
                .add(BigDecimal.valueOf(offset));
        int targetTicks = Math.round(bestDecimalTarget.floatValue());
        String futuresTicker = symbol.getSymbol();

        if (bestContracts == null || bestContracts.compareTo(BigDecimal.ONE) < 0) {
            // riesgo excesivo → mensaje
            return new OptimalContractRow(
                    start,
                    "The risk is too high",
                    null,
                    targetTicks
            );
        }

        // 5) Devolvemos la fila que representa el escenario óptimo
        return new OptimalContractRow(bestSl, futuresTicker, bestContracts, targetTicks);
    }
}


