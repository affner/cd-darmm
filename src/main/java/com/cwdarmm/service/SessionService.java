package com.cwdarmm.service;

import com.cwdarmm.event.WeeklyResetEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final ApplicationEventPublisher pub;
    private final ResultsAggregatorService  agg;
    private final MarketService             marketSvc;
//    private final RiskHistoryRepository     historyRepo;
    private final RiskCalcService           riskCalcSvc;

    @Getter
    private int currentWeek = LocalDate.now().get(WeekFields.ISO.weekOfYear());

    /**
     * Llamado por SchedulerConfig cada lunes 00:05.
     */
    public void performWeeklyReset() {
        int closingWeek = currentWeek;
        currentWeek = LocalDate.now().get(WeekFields.ISO.weekOfYear());

        // 1) archivar snapshot semanal
 //       historyRepo.archiveWeek(closingWeek, agg.snapshot());

        // 2) limpiar/agregar datos
        agg.resetWeek();
        marketSvc.archiveAndResetAll();
        riskCalcSvc.resetWeekly(0.20);   // vuelve al riesgo base

        // 3) notificar a los controladores
        pub.publishEvent(new WeeklyResetEvent());
    }
}
