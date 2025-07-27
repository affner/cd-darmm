package com.cwdarmm.model.domain;

/**
 * Símbolo del futuro (ticker) utilizado para la operativa.
 * Corresponde a la tabla <code>cat_symbols</code>.
 */

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
