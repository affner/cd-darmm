package com.cwdarmm.config;

/**
 * Carga inicial de cuentas desde <code>cat_accounts.csv</code>
 * cuando la base de datos está vacía.
 */

import com.cwdarmm.model.domain.CatAccount;
import com.cwdarmm.repository.CatAccountRepository;
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
public class CatAccountLoader {
    private final CatAccountRepository accountRepo;

    @PostConstruct
    public void loadAccountDefinitions() throws IOException {
        if (accountRepo.count() > 0) {
            return;
        }
        try (var is = new ClassPathResource("data/cat_accounts.csv").getInputStream();
             var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            reader.lines()
                    // saltar líneas vacías, comentarios y encabezado
                    .filter(l -> !l.isBlank() && !l.startsWith("//") && !l.toLowerCase().startsWith("id,"))
                    .forEach(line -> {
                        String[] parts = line.split(",");
                        if (parts.length < 3) {
                            throw new IllegalArgumentException("Línea inválida en cat_accounts.csv: " + line);
                        }
                        Long id = Long.parseLong(parts[0].trim());
                        String name = parts[1].trim();
                        Double initialSize = Double.parseDouble(parts[2].trim());
                        String color = parts.length>3 ? parts[3].trim() : null;
                        String fontColor = parts.length>4 ? parts[4].trim() : null;
                        CatAccount account = CatAccount.builder()
                                .id(id)
                                .description(name)
                                .initialSize(initialSize)
                                .color(color)
                                .fontColor(fontColor)
                                .build();
                        accountRepo.save(account);
                    });
        } catch (Exception ex) {
            throw new RuntimeException("Error loading cat_accounts.csv", ex);
        }
    }
}
