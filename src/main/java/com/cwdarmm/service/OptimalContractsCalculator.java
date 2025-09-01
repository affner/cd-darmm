package com.cwdarmm.service;

import com.cwdarmm.model.domain.BdMarket;
import com.cwdarmm.model.domain.CatContract;
import com.cwdarmm.model.domain.CatMarket;
import com.cwdarmm.model.domain.CatSymbol;
import com.cwdarmm.model.dto.OptimalContractRow;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.repository.BdMarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Servicio que calcula los contratos óptimos replicando las fórmulas
 * del libro Excel. Extraído de RiskAnalysisService para reutilización.
 */
@Service
@RequiredArgsConstructor
public class OptimalContractsCalculator {

    private final BdMarketRepository bdMarketRepo;

    private static double r2(double v){
        return new BigDecimal(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
    private static double r3(double v){
        return new BigDecimal(v).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }

    public List<OptimalContractRow> calculate(RiskInputDTO in){
        List<BdMarket> rows = bdMarketRepo.findOneByMktAccMdata(
                in.getMarket().getId(),
                in.getAccount().getId(),
                in.getMarketData().getId()
        );
        if(rows.isEmpty()){
            throw new IllegalArgumentException("No BD_MARKET rows for selection");
        }

        BdMarket chosen;
        if(in.isFirstTrade()){
            chosen = rows.stream()
                    .filter(r -> !r.getSymbol().getSymbol().startsWith("M"))
                    .findFirst()
                    .orElse(rows.get(0));
        }else{
            chosen = rows.stream()
                    .filter(r -> r.getSymbol().getSymbol().startsWith("M"))
                    .findFirst()
                    .orElse(rows.stream()
                            .min(Comparator.comparing(BdMarket::getTickValue))
                            .orElse(rows.get(0)));
        }

        CatContract contract = chosen.getContract();
        CatSymbol symbol = chosen.getSymbol();

        BigDecimal riskA = in.getRiskPctA();
        BigDecimal riskB = in.getRiskPctB();
        int offset = offsetForMarket(in.getMarket());

        return Stream.of(
                in.isHouse() ? makeRowRange(in, chosen, riskA, contract, symbol, offset, in.isFirstTrade()) : null,
                in.isLunch() ? makeRowRange(in, chosen, riskB, contract, symbol, offset, in.isFirstTrade()) : null
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private int offsetForMarket(CatMarket market){
        String name = market.getDescription();
        return "NASDAQ".equalsIgnoreCase(name) ? 2 : 1;
    }

    private OptimalContractRow makeRowRange(RiskInputDTO in,
                                            BdMarket bd,
                                            BigDecimal riskPct,
                                            CatContract contract,
                                            CatSymbol symbol,
                                            int offset,
                                            boolean firstTrade){
        BigDecimal appliedPct = riskPct == null ? BigDecimal.ZERO : riskPct;
        BigDecimal currentRisk = in.getAccountSize()
                .multiply(appliedPct)
                .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);

        BigDecimal tickValue = BigDecimal.valueOf(bd.getTickValue());
        BigDecimal commission = BigDecimal.valueOf(bd.getCommission());

        int start = in.getTicksSl1();
        int end = start;

        BigDecimal bestProfit = null;
        BigDecimal bestContracts = null;
        int bestSl = start;

        for(int sl=start; sl<=end; sl++){
            BigDecimal riskPerContract = tickValue
                    .multiply(BigDecimal.valueOf(sl))
                    .add(commission);
            if(riskPerContract.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal optContracts = currentRisk.divide(riskPerContract,0,RoundingMode.DOWN);
            if(firstTrade && optContracts.compareTo(BigDecimal.ONE) < 0){
                optContracts = BigDecimal.valueOf(5);
            }

            BigDecimal decimalTarget = BigDecimal.valueOf(in.getRiskReward())
                    .multiply(BigDecimal.valueOf(sl))
                    .add(BigDecimal.valueOf(offset));

            BigDecimal potentialProfit = optContracts
                    .multiply(tickValue.multiply(decimalTarget))
                    .subtract(commission.multiply(optContracts));

            if(bestProfit == null || potentialProfit.compareTo(bestProfit) > 0){
                bestProfit = potentialProfit;
                bestContracts = optContracts;
                bestSl = sl;
            }
        }

        BigDecimal bestDecimalTarget = BigDecimal.valueOf(in.getRiskReward())
                .multiply(BigDecimal.valueOf(bestSl))
                .add(BigDecimal.valueOf(offset));
        int targetTicks = Math.round(bestDecimalTarget.floatValue());
        String futuresTicker = symbol.getSymbol();
        if(bestContracts == null || bestContracts.compareTo(BigDecimal.ONE) < 0){
            return new OptimalContractRow(start, "The risk is too high", null, targetTicks);
        }
        return new OptimalContractRow(bestSl, futuresTicker, bestContracts, targetTicks);
    }
}
