package com.cwdarmm.config;

/**
 * Carga archivos FXML integrándolos con el contenedor Spring
 * para que los controladores sean inyectados automáticamente.
 */

import javafx.fxml.FXMLLoader;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SpringFXMLLoader {
    private final ApplicationContext context;

    public SpringFXMLLoader(ApplicationContext context) {
        this.context = context;
    }

    public FXMLLoader load(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(context::getBean);
        loader.load();
        return loader;
    }
}

