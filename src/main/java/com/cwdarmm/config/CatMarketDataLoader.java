package com.cwdarmm.config;

import com.cwdarmm.model.domain.CatMarketData;
import com.cwdarmm.repository.CatMarketDataRepository;
import com.cwdarmm.repository.CatAccountRepository;
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
public class CatMarketDataLoader {

    private final CatMarketDataRepository feedRepo;
    private final CatAccountRepository accountRepo;

    @PostConstruct
    @Transactional
    public void loadFeeds() {
        // 1) Carga de proveedores (cat_market_data.csv) solo si aún no existen
        if (feedRepo.count() == 0) {
            try (var is = new ClassPathResource("data/cat_market_data.csv").getInputStream();
                 var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                reader.lines()
                        .filter(l -> !l.isBlank() && !l.startsWith("//") && !l.toLowerCase().startsWith("id,"))
                        .map(l -> l.split(",", 2))
                        .forEach(cols -> {
                            var feed = CatMarketData.builder()
                                    .id(Long.parseLong(cols[0].trim()))
                                    .description(cols[1].trim())
                                    .build();
                            feedRepo.save(feed);
                        });
            } catch (Exception e) {
                throw new RuntimeException("Error cargando cat_market_data.csv", e);
            }
        }

        // 2) Carga de asignaciones cuenta->feed (account_marketdata.csv)
        try (var is2 = new ClassPathResource("data/account_marketdata.csv").getInputStream();
             var reader2 = new BufferedReader(new InputStreamReader(is2, StandardCharsets.UTF_8))) {
            reader2.lines()
                    .filter(l -> !l.isBlank() && !l.startsWith("//") && !l.toLowerCase().startsWith("accountid,"))
                    .map(l -> l.split(",", 2))
                    .forEach(cols -> {
                        Long accountId = Long.parseLong(cols[0].trim());
                        Long feedId    = Long.parseLong(cols[1].trim());
                        accountRepo.findById(accountId).ifPresent(account -> {
                            feedRepo.findById(feedId).ifPresent(feed -> {
                                if (account.getMarketDataList().add(feed)) {
                                    accountRepo.save(account);   // JPA llenará account_marketdata
                                }
                            });
                        });
                    });
        } catch (Exception e) {
            throw new RuntimeException("Error cargando account_marketdata.csv", e);
        }
    }
}
