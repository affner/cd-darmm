package com.cwdarmm.service;

import com.cwdarmm.event.RiskCalculatedEvent;
import com.cwdarmm.model.domain.RiskResult;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ResultsAggregatorService {

    // Map<marketId, List<RiskResult>>
    private final Map<String, List<RiskResult>> data = new HashMap<>();

    @EventListener
    public void onRisk(RiskCalculatedEvent e) {
        data.computeIfAbsent(e.marketId(), k -> new ArrayList<>())
                .add(e.result());
    }

    /** resumen global (ej. suma capital usado) */
    public double getTotalCapital() {
        return data.values().stream()
                .flatMap(Collection::stream)
                .mapToDouble(RiskResult::capitalUsed)
                .sum();
    }

    public Map<String, List<RiskResult>> snapshot() {
        return Collections.unmodifiableMap(data);
    }

    /** vacía resultados tras archivarlos */
    public void resetWeek() {
        data.clear();
    }
}
