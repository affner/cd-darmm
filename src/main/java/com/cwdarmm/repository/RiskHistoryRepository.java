package com.cwdarmm.repository;

import com.cwdarmm.model.domain.RiskResult;
import java.util.List;
import java.util.Map;

public interface RiskHistoryRepository {
    /** Guarda un snapshot <marketId, lista resultados> */
    void archiveWeek(int weekNumber, Map<String, List<RiskResult>> data);
}
