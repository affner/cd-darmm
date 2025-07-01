package com.cwdarmm.model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class StopRange {

    private int from;
    private int to;
    private int step;

    public boolean contains(int ticks) {
        return ticks >= from && ticks <= to;
    }

}
