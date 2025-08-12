package com.cwdarmm.config;

/**
 * Carga archivos FXML integrándolos con el contenedor Spring
 * para que los controladores sean inyectados automáticamente.
 */

import javafx.fxml.FXMLLoader;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class SpringFXMLLoader {
    private final ApplicationContext context;
    private Locale locale = Locale.ENGLISH;

    public SpringFXMLLoader(ApplicationContext context) {
        this.context = context;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    public FXMLLoader load(String fxmlPath) throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), bundle);
        loader.setControllerFactory(context::getBean);
        loader.load();
        return loader;
    }
}

