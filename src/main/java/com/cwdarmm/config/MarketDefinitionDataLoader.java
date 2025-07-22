package com.cwdarmm.config;

import com.cwdarmm.model.domain.MarketCatalog;
import com.cwdarmm.repository.MarketDefinitionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class MarketDefinitionDataLoader {
    private final MarketDefinitionRepository repository;

    @PostConstruct
    public void loadMarketDefinitions() {
        if (repository.count() > 0) {
            return; // Ya hay datos cargados
        }
        try (var is = new ClassPathResource("data/market_definitions.csv").getInputStream();
             var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            reader.lines()
                    .filter(line -> !line.isBlank() && !line.startsWith("//"))
                    .forEach(line -> {
                        String[] c = line.split(",");
                        MarketCatalog def = MarketCatalog.builder()
                                .symbol(c[0].trim())
                                .multiplier(Double.parseDouble(c[1].trim()))
                                .tickSize(Double.parseDouble(c[2].trim()))
                                .tickValue(Double.parseDouble(c[3].trim()))
                                .margin(Double.parseDouble(c[4].trim()))
                                .commission(Double.parseDouble(c[5].trim()))
                                .build();
                        repository.save(def);
                    });
            System.out.println("[DataLoader] Cargados " + repository.count() + " MarketDefinitions.");
        } catch (Exception e) {
            throw new RuntimeException("Error cargando market_definitions.csv", e);
        }
    }
}
