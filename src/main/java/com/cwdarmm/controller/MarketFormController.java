// MarketFormController.java
package com.cwdarmm.controller;

import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.service.MarketService;
import com.cwdarmm.config.SpringFXMLLoader;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class MarketFormController {
    private final MarketService marketService;
    private final SpringFXMLLoader springFXMLLoader;

    @FXML
    private TableView<MarketDTO> tableMarkets;
    @FXML
    private TableColumn<MarketDTO, String> colAccount;
    @FXML
    private TableColumn<MarketDTO, String> colMarket;
    @FXML
    private TableColumn<MarketDTO, String> colMarketData;
    @FXML
    private TableColumn<MarketDTO, Double> colSize;
    @FXML
    private TableColumn<MarketDTO, Double> colRiskA;
    @FXML
    private TableColumn<MarketDTO, Double> colRiskB;
    @FXML
    private TableColumn<MarketDTO, Double> colFinalHouse;
    @FXML
    private TableColumn<MarketDTO, Double> colFinalLunch;

    private MarketDTO lastSaved;

    public MarketFormController(MarketService marketService,
                                SpringFXMLLoader springFXMLLoader) {
        this.marketService = marketService;
        this.springFXMLLoader = springFXMLLoader;
    }

    @FXML
    public void initialize() {
        colAccount.setCellValueFactory(feat ->
                new ReadOnlyStringWrapper(feat.getValue().getAccount().getName()));
        colMarket.setCellValueFactory(feat ->
                new ReadOnlyStringWrapper(feat.getValue().getMarket().getName()));
        colMarketData.setCellValueFactory(feat ->
                new ReadOnlyStringWrapper(feat.getValue().getMarketData().getName()));
        colSize.setCellValueFactory(new PropertyValueFactory<>("accountSize"));
        colRiskA.setCellValueFactory(new PropertyValueFactory<>("riskA"));
        colRiskB.setCellValueFactory(new PropertyValueFactory<>("riskB"));
        colFinalHouse.setCellValueFactory(new PropertyValueFactory<>("riskFinalHouse"));
        colFinalLunch.setCellValueFactory(new PropertyValueFactory<>("riskFinalLunch"));

        loadMarkets();
    }

    private void loadMarkets() {
        List<MarketDTO> list = marketService.findAll();
        tableMarkets.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void onOpenMarket() {
        try {
            // 1) Abrir modal de OpenMarket
            FXMLLoader openLoader = springFXMLLoader.load("/fxml/openMarketForm.fxml");
            Stage openStage = new Stage();
            openStage.initOwner(tableMarkets.getScene().getWindow());
            openStage.initModality(Modality.APPLICATION_MODAL);
            openStage.setTitle("Open Market");
            openStage.setScene(new Scene(openLoader.getRoot()));

            // 2) Configurar callback tras guardar
            OpenMarketController omc = openLoader.getController();
            omc.setDialogStage(openStage);
            omc.setOnSave(() -> {
                // recarga y guarda el DTO recién creado
                this.lastSaved = omc.getLastSavedDTO();
                loadMarkets();
                // New: abre ventana con tabla inicial + botón Risk Manage
                FXMLLoader tableLoader = null;
                try {
                    tableLoader = springFXMLLoader.load("/fxml/riskTableView.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Stage tableStage = new Stage();
                tableStage.initOwner(tableMarkets.getScene().getWindow());
                tableStage.initModality(Modality.NONE);
                tableStage.setTitle(lastSaved.getMarket() + " – Risk Manager");
                tableStage.setScene(new Scene(tableLoader.getRoot()));
                // pasar contexto al controlador
                RiskTableController rtc = tableLoader.getController();
                rtc.setContext(lastSaved);
                tableStage.show();
            });

            openStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openRiskWindow(MarketDTO context) {
        try {
            FXMLLoader riskLoader = springFXMLLoader.load("/fxml/RiskForm.fxml");
            Stage riskStage = new Stage();
            riskStage.initOwner(tableMarkets.getScene().getWindow());
            riskStage.initModality(Modality.NONE);
            riskStage.setTitle(context.getMarket() + " – Risk Manager");
            riskStage.setScene(new Scene(riskLoader.getRoot()));

            RiskFormController rfc = riskLoader.getController();
            rfc.setDialogStage(riskStage);
            rfc.setMarketContext(context);
            rfc.setOnCalculated(this::loadMarkets);

            riskStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permite acceder al DTO creado desde OpenMarketController.
     */
    public MarketDTO getLastSavedDTO() {
        return lastSaved;
    }
}
