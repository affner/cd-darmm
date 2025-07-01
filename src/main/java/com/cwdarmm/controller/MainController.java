package com.cwdarmm.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype")   // <- 1 instancia por carga de FXML
public class MainController {

    @FXML private StackPane contentPane;
    private final ApplicationContext ctx;    // inyecta Spring

    public MainController(ApplicationContext ctx) {
        this.ctx = ctx;
    }


    @FXML
    private void initialize() throws Exception {
        // carga inicial = Risk Manager
        loadIntoContent("/fxml/risk_manager.fxml");
    }

    /* ---------- Menu actions ---------- */

    @FXML
    private void onExit() {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void openMarketCatalog() throws Exception {
        loadIntoContent("/fxml/market_catalog.fxml");
    }

    /* ---------- helper ---------- */

    private void loadIntoContent(String path) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        loader.setControllerFactory(ctx::getBean);      // ←★ MISMA CLAVE
        Node view = loader.load();
        contentPane.getChildren().setAll(view);
    }
}
