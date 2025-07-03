package com.cwdarmm.controller;

import com.cwdarmm.event.RiskCalculatedEvent;
import com.cwdarmm.model.domain.Market;
import com.cwdarmm.model.domain.RiskResult;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MarketTabController {

    private final ApplicationContext ctx;
    private Market market;                // la entidad asociada a ESTA pestaña

    // --- UI ---
    @FXML private Label lblMarket;
    @FXML private TableView<RiskResult> tblRisk;


    @FXML
    private void initialize() {
        // —— define columnas ——
        TableColumn<RiskResult, Integer> t0 = new TableColumn<>("#");
        t0.setPrefWidth(40);
        t0.setCellValueFactory(new PropertyValueFactory<>("tradeNumber"));

        TableColumn<RiskResult, Double> t1 = new TableColumn<>("Contracts");
        t1.setPrefWidth(100);
        t1.setCellValueFactory(new PropertyValueFactory<>("contracts"));

        TableColumn<RiskResult, Double> t2 = new TableColumn<>("Risk $");
        t2.setPrefWidth(100);
        t2.setCellValueFactory(new PropertyValueFactory<>("riskUsd"));

        TableColumn<RiskResult, Double> t3 = new TableColumn<>("Capital used");
        t3.setPrefWidth(120);
        t3.setCellValueFactory(new PropertyValueFactory<>("capitalUsed"));

        TableColumn<RiskResult, Double> t4 = new TableColumn<>("R:R");
        t4.setPrefWidth(80);
        t4.setCellValueFactory(new PropertyValueFactory<>("rrRatio"));

        TableColumn<RiskResult, Double> t5 = new TableColumn<>("% Risk");
        t5.setPrefWidth(80);
        t5.setCellValueFactory(new PropertyValueFactory<>("darmmRiskPercent"));

        tblRisk.getColumns().setAll(t0, t1, t2, t3, t4, t5);

        // inicia lista vacía (por si no se asignó en initWithMarket)
        if (tblRisk.getItems().isEmpty()) {
            tblRisk.setItems(javafx.collections.FXCollections.observableArrayList());
        }
    }


    /* ---------- API pública ---------- */

    /** llamado por MainController después de cargar el FXML */
    public void initWithMarket(Market m) {
        this.market = m;
        lblMarket.setText(m.getName());
        tblRisk.setItems(javafx.collections.FXCollections.observableArrayList());
    }

    /* ---------- Botón "Gestionar riesgo" ---------- */

    @FXML
    private void openRiskManager() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/risk_manager.fxml"));
        loader.setControllerFactory(ctx::getBean);
        Region root = loader.load();

        RiskManagerController ctrl = loader.getController();
        ctrl.initWithMarket(market);          // pasa referencia

        javafx.stage.Stage stage = new javafx.stage.Stage();
        stage.setTitle("Risk Manager – " + market.getName());
        stage.setScene(new javafx.scene.Scene(root));
        stage.initOwner(lblMarket.getScene().getWindow());
        stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
        stage.showAndWait();
    }

    /* ---------- Escucha resultados ---------- */

    @EventListener
    public void onRiskCalculated(RiskCalculatedEvent ev) {
        if (!ev.marketId().equals(market.getSymbol())) return; // ignora otros mercados
        Platform.runLater(() -> tblRisk.getItems().add(ev.result()));
    }
}
