package com.cwdarmm.config;

import com.cwdarmm.model.domain.CatSymbol;
import com.cwdarmm.repository.CatSymbolRepository;
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
public class CatSymbolLoader {

    private final CatSymbolRepository symbolRepo;

    @PostConstruct
    @Transactional
    public void loadSymbols() {
        if (symbolRepo.count() > 0) {
            return;
        }

        try (var is = new ClassPathResource("data/cat_symbols.csv").getInputStream();
             var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            reader.lines()
                    .filter(l -> !l.isBlank() && !l.startsWith("//") && !l.toLowerCase().startsWith("id,"))
                    .map(line -> line.split(",", 2))
                    .forEach(cols -> {
                        Long id = Long.parseLong(cols[0].trim());
                        String sym = cols[1].trim();
                        symbolRepo.save(CatSymbol.builder()
                                .id(id)
                                .symbol(sym)
                                .build());
                    });

        } catch (Exception e) {
            throw new RuntimeException("Error cargando cat_symbols.csv", e);
        }
    }
}
