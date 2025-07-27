package com.cwdarmm.model.domain;

/**
 * Entidad JPA que representa una cuenta de trading.
 *
 * <p>Se corresponde con la tabla <code>cat_accounts</code> y
 * almacena la descripción de la cuenta, su capital inicial y las
 * relaciones con los feeds de mercado permitidos.</p>
 */

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
