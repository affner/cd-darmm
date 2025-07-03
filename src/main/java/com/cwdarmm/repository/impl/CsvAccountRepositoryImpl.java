package com.cwdarmm.repository.impl;

import com.cwdarmm.model.domain.TradingAccount;
import com.cwdarmm.repository.AccountRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CsvAccountRepositoryImpl implements AccountRepository {

    private final List<TradingAccount> cache;

    public CsvAccountRepositoryImpl() { cache = load(); }

    @Override
    public List<TradingAccount> findAll() {
        return Collections.unmodifiableList(cache);
    }

    @Override
    public TradingAccount findByName(String name) {
        return cache.stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /* ---------- helpers ---------- */

    private List<TradingAccount> load() {
        try (var reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("data/accounts.csv").getInputStream(),
                StandardCharsets.UTF_8))) {

            return reader.lines()
                    .skip(1)
                    .map(this::parse)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new IllegalStateException("Cannot read accounts.csv", e);
        }
    }

    private TradingAccount parse(String line) {
        String[] t = line.split(",");
        TradingAccount a = new TradingAccount();
        a.setName(t[0]);
        a.setInitialSize(Double.parseDouble(t[1]));
        a.setCurrentSize(a.getInitialSize());
        return a;
    }
}
