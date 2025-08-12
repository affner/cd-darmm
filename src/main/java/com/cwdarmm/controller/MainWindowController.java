package com.cwdarmm.controller;

/**
 * Controlador de la ventana principal que contiene las pestañas
 * "Markets" y "Results".
 */

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MainWindowController {

    @FXML private TabPane tabPane;
    @FXML private javafx.scene.control.Tab tabMarkets;

    @FXML private javafx.scene.control.Tab tabResults;
    @FXML private javafx.scene.control.Tab tabMarketDb;
    @FXML private MenuButton btnLanguage;

    private final BdMarketController bdMarketController;
    private final ResultViewController resultViewController;
    private final SpringFXMLLoader springFXMLLoader;

    @FXML
    public void initialize() {
        // Seleccionamos la pestaña Markets por defecto al arrancar
        tabPane.getSelectionModel().select(tabMarkets);

        // Al cambiar a la pestaña BD Market recargamos su tabla
        tabMarketDb.setOnSelectionChanged(ev -> {
            if (tabMarketDb.isSelected()) {
                bdMarketController.refreshTable();
            }
        });

        // Al entrar a la pestaña Results recalculamos automáticamente
        tabResults.setOnSelectionChanged(ev -> {
            if (tabResults.isSelected()) {
                resultViewController.onClick();
            }
        });
    }

    // Métodos para cambiar de pestaña desde código si los necesitas:

    public void showResultsTab() {
        tabPane.getSelectionModel().select(tabResults);
    }

    @FXML
    private void switchToSpanish() throws IOException {
        reload(new Locale("es"));
    }

    @FXML
    private void switchToEnglish() throws IOException {
        reload(Locale.ENGLISH);
    }

    private void reload(Locale locale) throws IOException {
        // Guardamos la escena **antes** de recargar el FXML, porque al cargarse
        // de nuevo el controlador se reinicia y los nodos todavía no tienen escena.
        Scene scene = btnLanguage.getScene();
        springFXMLLoader.setLocale(locale);
        FXMLLoader loader = springFXMLLoader.load("/fxml/MainWindow.fxml");
        Parent root = loader.getRoot();
        scene.setRoot(root);
        scene.getWindow().setTitle(loader.getResources().getString("app.title"));
    }
}
