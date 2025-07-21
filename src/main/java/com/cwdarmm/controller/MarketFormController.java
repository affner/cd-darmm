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
import javafx.scene.control.TableRow;
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

        tableMarkets.setRowFactory(tv -> {
            TableRow<MarketDTO> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (!row.isEmpty() && evt.getClickCount() == 2) {
                    MarketDTO clicked = row.getItem();
                    openRiskTableWindow(clicked);
                }
            });
            return row;
        });
    }

    private void loadMarkets() {
        List<MarketDTO> list = marketService.findAll();
        tableMarkets.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void onOpenMarket() {
        try {
            // 1) Prepara el diálogo
            FXMLLoader openLoader = springFXMLLoader.load("/fxml/openMarketForm.fxml");
            Stage openStage = new Stage();
            openStage.initOwner(tableMarkets.getScene().getWindow());
            openStage.initModality(Modality.WINDOW_MODAL);     // WINDOW_MODAL en lugar de APPLICATION_MODAL
            openStage.setTitle("Open Market");
            openStage.setScene(new Scene(openLoader.getRoot()));

            // 2) Configura callback para guardar el DTO y recargar la tabla
            OpenMarketController omc = openLoader.getController();
            omc.setDialogStage(openStage);
            omc.setOnSave(() -> {
                this.lastSaved = omc.getLastSavedDTO();
                loadMarkets();
            });

            // 3) Muestra el diálogo y espera a que se cierre
            openStage.showAndWait();

            // 4) Una vez cerrado, abres la ventana de Risk **fuera** del callback
            if (lastSaved != null) {
                FXMLLoader riskLoader = springFXMLLoader.load("/fxml/riskTableView.fxml");
                Stage riskStage = new Stage();
                riskStage.initOwner(tableMarkets.getScene().getWindow());
                riskStage.initModality(Modality.NONE);
                riskStage.setTitle(lastSaved.getMarket().getName() + " – Risk Manager");
                riskStage.setScene(new Scene(riskLoader.getRoot()));

                RiskTableController rtc = riskLoader.getController();
                rtc.setContext(lastSaved);
                riskStage.show();

                // limpiamos la marca para la próxima vez
                lastSaved = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void openRiskTableWindow(MarketDTO context) {
        try {
            // 1) Carga el FXML de la vista de tabla de riesgo
            FXMLLoader riskLoader = springFXMLLoader.load("/fxml/riskTableView.fxml");
            Stage riskStage = new Stage();
            riskStage.initOwner(tableMarkets.getScene().getWindow());
            riskStage.initModality(Modality.NONE);
            riskStage.setTitle(context.getMarket().getName() + " – Risk Manager");
            riskStage.setScene(new Scene(riskLoader.getRoot()));

            // 2) Pasa el contexto al controller de Risk Table
            RiskTableController rtc = riskLoader.getController();
            rtc.setContext(context);

            // 3) Muestra la ventana
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
