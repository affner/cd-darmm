// src/main/java/com/cwdarmm/config/FeedDataLoader.java
package com.cwdarmm.config;

import com.cwdarmm.model.domain.AccountDefinition;
import com.cwdarmm.model.domain.FeedDefinition;
import com.cwdarmm.model.domain.AccountFeed;
import com.cwdarmm.repository.FeedDefinitionRepository;
import com.cwdarmm.repository.AccountFeedRepository;
import com.cwdarmm.repository.AccountDefinitionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class FeedDataLoader {
    private final FeedDefinitionRepository feedRepo;
    private final AccountFeedRepository acctFeedRepo;
    private final AccountDefinitionRepository accountRepo;

    @PostConstruct
    public void loadFeeds() {
        if (feedRepo.count() > 0 && acctFeedRepo.count() > 0) return;

        // 1) Carga de proveedores (feeds.csv)
        try (var is = new ClassPathResource("data/feeds.csv").getInputStream();
             var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            reader.lines()
                    .filter(line -> !line.isBlank() && !line.startsWith("//") && !line.toLowerCase().startsWith("id,"))
                    .map(line -> line.split(",", 2))
                    .forEach(cols -> {
                        Long id = Long.parseLong(cols[0].trim());
                        String name = cols[1].trim();
                        feedRepo.save(FeedDefinition.builder()
                                .id(id)
                                .name(name)
                                .build());
                    });
        } catch (Exception e) {
            throw new RuntimeException("Error cargando feeds.csv", e);
        }

        // 2) Carga de asignaciones cuenta->feed (account_feeds.csv)
        try (var is2 = new ClassPathResource("data/account_feeds.csv").getInputStream();
             var reader2 = new BufferedReader(new InputStreamReader(is2, StandardCharsets.UTF_8))) {
            reader2.lines()
                    .filter(line -> !line.isBlank() && !line.startsWith("//") && !line.toLowerCase().startsWith("accountid,"))
                    .map(line -> line.split(",", 2))
                    .forEach(cols -> {
                        Long accountId = Long.parseLong(cols[0].trim());
                        Long feedId = Long.parseLong(cols[1].trim());

                        accountRepo.findById(accountId).ifPresent(acc -> {
                            feedRepo.findById(feedId).ifPresent(feed -> {
                                acctFeedRepo.save(AccountFeed.builder()
                                        .account(acc)
                                        .feed(feed)
                                        .build());
                            });
                        });
                    });
        } catch (Exception e) {
            throw new RuntimeException("Error cargando account_feeds.csv", e);
        }
    }
}
