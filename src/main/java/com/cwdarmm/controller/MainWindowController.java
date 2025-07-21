package com.cwdarmm.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MainWindowController {

    @FXML private TabPane tabPane;
    @FXML private javafx.scene.control.Tab tabMarkets;

    @FXML private javafx.scene.control.Tab tabResults;

    @FXML
    public void initialize() {
        // Seleccionamos la pestaña Markets por defecto al arrancar
        tabPane.getSelectionModel().select(tabMarkets);
    }

    // Métodos para cambiar de pestaña desde código si los necesitas:

    public void showResultsTab() {
        tabPane.getSelectionModel().select(tabResults);
    }
}
