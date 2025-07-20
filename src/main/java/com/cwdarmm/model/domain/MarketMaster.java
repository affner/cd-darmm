package com.cwdarmm.model.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "market_masters") // renamed to avoid conflict
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
