package com.cwdarmm.model.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cat_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", length = 50, nullable = false, unique = true)
    private String description;

    @Column(name = "initial_size", nullable = false)
    private Double initialSize;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "account_marketdata",
            joinColumns = @JoinColumn(name = "cat_account_id"),
            inverseJoinColumns = @JoinColumn(name = "cat_market_data_id"))
    private Set<CatMarketData> marketDataList = new HashSet<>();
}
