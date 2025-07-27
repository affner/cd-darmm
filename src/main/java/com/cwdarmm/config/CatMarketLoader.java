package com.cwdarmm.config;

/**
 * Pobla el catálogo de mercados a partir de
 * <code>cat_markets.csv</code> en el arranque.
 */

import com.cwdarmm.model.domain.CatMarket;
import com.cwdarmm.repository.CatMarketRepository;
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
public class CatMarketLoader {

    private final CatMarketRepository marketRepo;

    @PostConstruct
    @Transactional
    public void loadMarkets() {
        if (marketRepo.count() > 0) return;

        try (var is = new ClassPathResource("data/cat_markets.csv").getInputStream();
             var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            reader.lines()
                    .filter(l -> !l.isBlank() && !l.startsWith("//") && !l.toLowerCase().startsWith("id,"))
                    .map(l -> l.split(","))
                    .forEach(cols -> {
                        Long id   = Long.parseLong(cols[0].trim());
                        String name = cols[1].trim();
                        String c1 = cols.length>2 ? cols[2].trim() : null;
                        String c2 = cols.length>3 ? cols[3].trim() : null;
                        marketRepo.save(CatMarket.builder()
                                .id(id)
                                .description(name)
                                .color1(c1)
                                .color2(c2)
                                .build());
                    });

        } catch (Exception e) {
            throw new RuntimeException("Error cargando cat_markets.csv", e);
        }
    }
}
