package com.cwdarmm.config;

import com.cwdarmm.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Tareas programadas de mantenimiento de la sesión CW-DARMM.
 * Por ahora solo realiza el reset semanal.
 */
@Configuration
@RequiredArgsConstructor
public class SchedulerConfig {

    private final SessionService sessionService;

    /**
     * Ejecuta un reset cada lunes a las 00:05 (hora local del sistema).
     *  └─ cron =  s  m  h  d  M  w  (Spring cron)
     */
    @Scheduled(cron = "0 5 0 ? * MON")
    public void weeklyReset() {
        sessionService.performWeeklyReset();
    }
}
