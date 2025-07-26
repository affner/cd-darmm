package com.cwdarmm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OptimalContractRow {
    private Integer slSize;             // SL Size (Ticks)
    private String futuresTicker;       // FUTURES TICKER o mensaje
    private BigDecimal optimalContract; // Null si “risk too high”
    private Integer targetTicks;        // Target (Ticks)
}
