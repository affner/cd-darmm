package com.cwdarmm.controller;

import com.cwdarmm.event.RiskCalculatedEvent;
import com.cwdarmm.model.domain.Market;
import com.cwdarmm.model.domain.RiskResult;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
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
        // # ——————————————————————————
        TableColumn<RiskResult, Integer> c0 = new TableColumn<>("#");
        c0.setPrefWidth(40);
        c0.setCellValueFactory(r ->
                new ReadOnlyObjectWrapper<>(r.getValue().tradeNumber()));

        // Contracts ——————————————————
        TableColumn<RiskResult, Double> c1 = new TableColumn<>("Contracts");
        c1.setPrefWidth(100);
        c1.setCellValueFactory(r ->
                new ReadOnlyObjectWrapper<>(r.getValue().contracts()));

        // Risk $ ————————————————————
        TableColumn<RiskResult, Double> c2 = new TableColumn<>("Risk $");
        c2.setPrefWidth(100);
        c2.setCellValueFactory(r ->
                new ReadOnlyObjectWrapper<>(r.getValue().riskUsd()));

        // Capital used —————————————
        TableColumn<RiskResult, Double> c3 = new TableColumn<>("Capital used");
        c3.setPrefWidth(120);
        c3.setCellValueFactory(r ->
                new ReadOnlyObjectWrapper<>(r.getValue().capitalUsed()));

        // R:R ————————————————————————
        TableColumn<RiskResult, Double> c4 = new TableColumn<>("R:R");
        c4.setPrefWidth(80);
        c4.setCellValueFactory(r ->
                new ReadOnlyObjectWrapper<>(r.getValue().rrRatio()));

        // Risk % ————————————————————
        TableColumn<RiskResult, Double> c5 = new TableColumn<>("Risk %");
        c5.setPrefWidth(80);
        c5.setCellValueFactory(r ->
                new ReadOnlyObjectWrapper<>(r.getValue().darmmRiskPercent()));

        tblRisk.getColumns().setAll(c0, c1, c2, c3, c4, c5);
        tblRisk.setItems(FXCollections.observableArrayList());
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
