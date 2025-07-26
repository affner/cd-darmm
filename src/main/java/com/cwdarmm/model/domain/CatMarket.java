package com.cwdarmm.model.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cat_markets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatMarket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false, unique = true)
    private String description;
}
