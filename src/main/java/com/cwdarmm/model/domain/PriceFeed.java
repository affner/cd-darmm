package com.cwdarmm.model.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "feeds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PriceFeed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String name;

    @Builder.Default
    @ManyToMany(mappedBy = "feeds", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private Set<TradingAccount> accounts = new HashSet<>();
}
