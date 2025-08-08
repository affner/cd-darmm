package com.cwdarmm.controller;

/**
 * Controlador de la ventana principal que contiene las pestañas
 * "Markets" y "Results".
 */

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainWindowController {

    @FXML private TabPane tabPane;
    @FXML private javafx.scene.control.Tab tabMarkets;

    @FXML private javafx.scene.control.Tab tabResults;
    @FXML private javafx.scene.control.Tab tabMarketDb;

    private final BdMarketController bdMarketController;
    private final ResultViewController resultViewController;

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
}
