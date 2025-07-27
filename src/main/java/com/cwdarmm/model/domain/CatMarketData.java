package com.cwdarmm.model.domain;

/**
 * Proveedor de datos de mercado (por ejemplo, IQFeed, Rithmic...).
 *
 * <p>Tabla <code>cat_market_data</code> que se relaciona de
 * forma Many‑To‑Many con {@link CatAccount} a través de
 * <code>account_marketdata</code>.</p>
 */

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cat_market_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class CatMarketData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(name = "description", nullable = false, unique = true)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String description;

    @Builder.Default
    @ManyToMany(mappedBy = "marketDataList", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private Set<CatAccount> accounts = new HashSet<>();
}
