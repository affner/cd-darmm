package com.cwdarmm.config;

import com.cwdarmm.model.domain.AccountMarket;
import com.cwdarmm.repository.AccountMarketRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class AccountMarketDataLoader {
    private final AccountMarketRepository repo;

    @PostConstruct
    public void load() {
        if (repo.count() > 0) return;
        try (var is = new ClassPathResource("data/account_markets.csv").getInputStream();
             var rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            rd.lines()
                    .filter(l -> !l.isBlank() && !l.startsWith("//"))
                    .forEach(line -> {
                        var cols = line.split(",", 2);
                        repo.save(AccountMarket.builder()
                                .account(cols[0].trim())
                                .market(cols[1].trim())
                                .build());
                    });
        } catch (Exception e) {
            throw new RuntimeException("Error cargando account_markets.csv", e);
        }
    }
}
