package com.cwdarmm.config;

/**
 * Carga la tabla <code>bd_markets</code> con la configuración
 * completa de mercados, contratos y símbolos.
 */

import com.cwdarmm.model.domain.*;
import com.cwdarmm.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@DependsOn({
        "catAccountLoader",
        "catMarketLoader",
        "catMarketDataLoader",
        "catContractLoader",
        "catSymbolLoader"
})
@RequiredArgsConstructor
public class BdMarketLoader {

    private final BdMarketRepository       bdRepo;
    private final CatAccountRepository     accountRepo;
    private final CatMarketRepository      marketRepo;
    private final CatMarketDataRepository  marketDataRepo;
    private final CatContractRepository    contractRepo;
    private final CatSymbolRepository      symbolRepo;

    @PostConstruct
    @Transactional
    public void loadBdMarkets() {
        if (bdRepo.count() > 0) return;

        try (var is = new ClassPathResource("data/bd_markets.csv").getInputStream();
             var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            reader.lines()
                    .filter(l -> !l.isBlank()
                            && !l.startsWith("//")
                            // Aquí tu cabecera de IDs:
                            && !l.toLowerCase().startsWith("market_id,"))
                    .map(line -> line.split(",", -1))
                    .forEach(cols -> {
                        Long marketId      = parseLong(cols[0].trim());
                        Long accountId     = parseLong(cols[1].trim());
                        Long marketDataId  = parseLong(cols[2].trim());
                        Long contractId    = parseLong(cols[3].trim());
                        Long symbolId      = parseLong(cols[4].trim());
                        Double mult        = parseDoubleOrNull(cols[5].trim());
                        Double tickSize    = parseDoubleOrNull(cols[6].trim());
                        Double tickValue   = parseDoubleOrNull(cols[7].trim());
                        Double margin      = parseDoubleOrNull(cols[8].trim());
                        Double commission  = parseDoubleOrNull(cols[9].trim());

                        var account    = accountRepo.findById(accountId)
                                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + accountId));
                        var market     = marketRepo.findById(marketId)
                                .orElseThrow(() -> new IllegalArgumentException("Market no encontrado: " + marketId));
                        var marketData = marketDataRepo.findById(marketDataId)
                                .orElseThrow(() -> new IllegalArgumentException("MarketData no encontrada: " + marketDataId));
                        var contract   = contractRepo.findById(contractId)
                                .orElseThrow(() -> new IllegalArgumentException("Contrato no encontrado: " + contractId));
                        var symbol     = symbolRepo.findById(symbolId)
                                .orElseThrow(() -> new IllegalArgumentException("Símbolo no encontrado: " + symbolId));

                        BdMarket bd = BdMarket.builder()
                                .account(account)
                                .market(market)
                                .marketData(marketData)
                                .contract(contract)
                                .symbol(symbol)
                                .multiplier(mult)
                                .tickSize(tickSize)
                                .tickValue(tickValue)
                                .margin(margin)
                                .commission(commission)
                                .build();

                        bdRepo.save(bd);
                    });

        } catch (Exception e) {
            throw new RuntimeException("Error cargando bd_markets.csv", e);
        }
    }

    // helpers
    private Long parseLong(String s) {
        return s.isBlank() ? null : Long.parseLong(s.trim());
    }
    private Double parseDoubleOrNull(String s) {
        return s.isBlank() ? null : Double.parseDouble(s.trim());
    }
}
