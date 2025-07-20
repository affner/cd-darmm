package com.cwdarmm.config;

import com.cwdarmm.model.domain.AccountDefinition;
import com.cwdarmm.model.domain.FeedDefinition;
import com.cwdarmm.repository.FeedDefinitionRepository;
import com.cwdarmm.repository.AccountDefinitionRepository;
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
public class FeedDataLoader {

    private final FeedDefinitionRepository feedRepo;
    private final AccountDefinitionRepository accountRepo;

    @PostConstruct
    @Transactional
    public void loadFeeds() {
        // 1) Carga de proveedores (feeds.csv) solo si aún no existen
        if (feedRepo.count() == 0) {
            try (var is = new ClassPathResource("data/feeds.csv").getInputStream();
                 var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                reader.lines()
                        .filter(l -> !l.isBlank() && !l.startsWith("//") && !l.toLowerCase().startsWith("id,"))
                        .map(l -> l.split(",", 2))
                        .forEach(cols -> {
                            var feed = FeedDefinition.builder()
                                    .id(Long.parseLong(cols[0].trim()))
                                    .name(cols[1].trim())
                                    .build();
                            feedRepo.save(feed);
                        });
            } catch (Exception e) {
                throw new RuntimeException("Error cargando feeds.csv", e);
            }
        }

        // 2) Carga de asignaciones cuenta->feed (account_feeds.csv)
        try (var is2 = new ClassPathResource("data/account_feeds.csv").getInputStream();
             var reader2 = new BufferedReader(new InputStreamReader(is2, StandardCharsets.UTF_8))) {
            reader2.lines()
                    .filter(l -> !l.isBlank() && !l.startsWith("//") && !l.toLowerCase().startsWith("accountid,"))
                    .map(l -> l.split(",", 2))
                    .forEach(cols -> {
                        Long accountId = Long.parseLong(cols[0].trim());
                        Long feedId    = Long.parseLong(cols[1].trim());

                        accountRepo.findById(accountId).ifPresent(account -> {
                            feedRepo.findById(feedId).ifPresent(feed -> {
                                // Añade el feed al Set y persiste la asociación
                                if (account.getFeeds().add(feed)) {
                                    accountRepo.save(account);
                                }
                            });
                        });
                    });
        } catch (Exception e) {
            throw new RuntimeException("Error cargando account_feeds.csv", e);
        }
    }
}
