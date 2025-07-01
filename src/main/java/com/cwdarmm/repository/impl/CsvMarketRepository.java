package com.cwdarmm.repository.impl;

import com.cwdarmm.model.Market;
import com.cwdarmm.repository.MarketRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CsvMarketRepository implements MarketRepository {

    private final List<Market> cache;

    public CsvMarketRepository() {
        cache = load();                // lee una sola vez
    }

    @Override
    public List<Market> findAll() {
        return Collections.unmodifiableList(cache);
    }

    @Override
    public Market findBySymbol(String symbol) {
        return cache.stream()
                .filter(m -> m.getSymbol().equalsIgnoreCase(symbol))
                .findFirst()
                .orElse(null);
    }

    /* ---------- helpers ---------- */

    private List<Market> load() {
        try (var reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("data/markets.csv").getInputStream(),
                StandardCharsets.UTF_8))) {

            return reader.lines()
                    .skip(1)                      // ignora cabecera
                    .map(this::parse)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new IllegalStateException("Cannot read markets.csv", e);
        }
    }

    private Market parse(String line) {
        String[] t = line.split(",");
        Market m = new Market();
        m.setSymbol(t[0]);
        m.setName(t[1]);
        m.setTickSize(Double.parseDouble(t[2]));
        m.setTickValue(Double.parseDouble(t[3]));
        m.setDefaultStop(Integer.parseInt(t[4]));
        m.setColorHex(t[5]);
        return m;
    }
}
