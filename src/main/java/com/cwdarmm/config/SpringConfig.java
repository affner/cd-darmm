package com.cwdarmm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuración raíz de Spring.
 * Activa el escaneo de componentes e inyección en toda la app.
 */
@Configuration
@ComponentScan(basePackages = "com.cwdarmm")   // escanea todo el proyecto
@EnableScheduling                              // activa @Scheduled (SchedulerConfig)
public class SpringConfig {

    /**
     * Bean auxiliar para inyectar el Stage principal en otros componentes,
     * si fuera necesario (opcional, ej. para diálogos comunes).
     */
    @Bean
    public com.cwdarmm.util.StageProvider stageProvider() {
        return new com.cwdarmm.util.StageProvider();
    }
}
