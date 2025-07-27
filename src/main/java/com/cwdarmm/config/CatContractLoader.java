package com.cwdarmm.config;

/**
 * Lee <code>cat_contracts.csv</code> y llena la tabla de contratos
 * si aún no existen registros.
 */

import com.cwdarmm.model.domain.CatContract;
import com.cwdarmm.repository.CatContractRepository;
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
public class CatContractLoader {

    private final CatContractRepository contractRepo;

    @PostConstruct
    @Transactional
    public void loadContracts() {
        // 1) Si ya hay datos, no hacemos nada
        if (contractRepo.count() > 0) {
            return;
        }

        // 2) Carga de cat_contracts.csv
        try (var is = new ClassPathResource("data/cat_contracts.csv").getInputStream();
             var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            reader.lines()
                    // saltar líneas vacías, comentarios y encabezado
                    .filter(l -> !l.isBlank() && !l.startsWith("//") && !l.toLowerCase().startsWith("id,"))
                    .map(line -> line.split(",", 2))
                    .forEach(cols -> {
                        Long id = Long.parseLong(cols[0].trim());
                        String description = cols[1].trim();
                        contractRepo.save(CatContract.builder()
                                .id(id)
                                .description(description)
                                .build());
                    });

        } catch (Exception e) {
            throw new RuntimeException("Error cargando cat_contracts.csv", e);
        }
    }
}
