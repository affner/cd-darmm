package com.cwdarmm.config;

import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Beans utilitarios específicos de JavaFX para que los FXML carguen
 * controllers gestionados por Spring.
 */
@Configuration
public class FxConfig {

    /**
     * Devuelve un "controller factory" reutilizable para todos los FXMLLoader
     * que necesites crear de forma programática (no vía @FXML).
     */
    @Bean
    public javafx.util.Callback<Class<?>, Object> springControllerFactory(ApplicationContext ctx) {
        return ctx::getBean;   // simplemente delega en el contexto
    }

    /**
     * Provee un FXMLLoader pre-configurado; útil en tests o cargas dinámicas.
     */
    @Bean
    public FXMLLoader fxLoader(javafx.util.Callback<Class<?>, Object> controllerFactory) {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(controllerFactory);
        return loader;
    }
}
