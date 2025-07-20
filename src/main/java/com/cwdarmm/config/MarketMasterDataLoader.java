package com.cwdarmm.config;

import com.cwdarmm.model.domain.MarketMaster;
import com.cwdarmm.repository.MarketMasterRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class MarketMasterDataLoader {

    private final MarketMasterRepository marketRepo;

    @PostConstruct
    @Transactional
    public void loadMarkets() {
        if (marketRepo.count() > 0) return;

        try (var is = new ClassPathResource("data/markets.csv").getInputStream();
             var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            reader.lines()
                    .filter(l -> !l.isBlank() && !l.startsWith("//") && !l.toLowerCase().startsWith("id,"))
                    .map(l -> l.split(",", 2))
                    .forEach(cols -> {
                        Long id   = Long.parseLong(cols[0].trim());
                        String name = cols[1].trim();
                        marketRepo.save(MarketMaster.builder()
                                .id(id)
                                .name(name)
                                .build());
                    });

        } catch (Exception e) {
            throw new RuntimeException("Error cargando markets.csv", e);
        }
    }
}
