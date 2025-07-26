package com.cwdarmm.model.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cat_symbols")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatSymbol {
    @Id
    private Long id;

    @Column(name = "symbol", nullable = false, unique = true, length = 10)
    private String symbol;
}
