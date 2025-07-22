package com.cwdarmm.config;

import com.cwdarmm.model.domain.TradingAccount;
import com.cwdarmm.repository.AccountDefinitionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class AccountDefinitionDataLoader {
    private final AccountDefinitionRepository accountRepo;

    @PostConstruct
    public void loadAccountDefinitions() throws IOException {
        if (accountRepo.count() > 0) {
            return;
        }
        try (var is = new ClassPathResource("data/accounts.csv").getInputStream();
             var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            reader.lines()
                    // saltar líneas vacías, comentarios y encabezado
                    .filter(l -> !l.isBlank() && !l.startsWith("//") && !l.toLowerCase().startsWith("id,"))
                    .forEach(line -> {
                        String[] parts = line.split(",");
                        if (parts.length < 3) {
                            throw new IllegalArgumentException("Línea inválida en accounts.csv: " + line);
                        }
                        Long id = Long.parseLong(parts[0].trim());
                        String name = parts[1].trim();
                        Double initialSize = Double.parseDouble(parts[2].trim());
                        TradingAccount account = TradingAccount.builder()
                                .id(id)
                                .name(name)
                                .initialSize(initialSize)
                                .build();
                        accountRepo.save(account);
                    });
        } catch (Exception ex) {
            throw new RuntimeException("Error loading accounts.csv", ex);
        }
    }
}
