package com.cwdarmm.controller;


import com.cwdarmm.config.SpringFXMLLoader;
import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.service.MarketService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class MarketFormController {
    @FXML private TableView<MarketDTO> tableMarkets;
    @FXML private TableColumn<MarketDTO, String> colAccount;
    @FXML private TableColumn<MarketDTO, String> colMarket;
    @FXML private TableColumn<MarketDTO, String> colMarketData;
    @FXML private TableColumn<MarketDTO, Double> colSize;
    @FXML private TableColumn<MarketDTO, Double> colRiskA;
    @FXML private TableColumn<MarketDTO, Double> colRiskB;
    @FXML private TableColumn<MarketDTO, Double> colFinalHouse;
    @FXML private TableColumn<MarketDTO, Double> colFinalLunch;

    private final MarketService marketService;

    private final SpringFXMLLoader fxmlLoader;

    public MarketFormController(MarketService marketService, SpringFXMLLoader fxmlLoader) {
        this.marketService = marketService;
        this.fxmlLoader = fxmlLoader;
    }


    @FXML
    public void initialize() {
        colAccount.setCellValueFactory(new PropertyValueFactory<>("account"));
        colMarket.setCellValueFactory(new PropertyValueFactory<>("market"));
        colMarketData.setCellValueFactory(new PropertyValueFactory<>("marketData"));
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
            FXMLLoader loader = fxmlLoader.load("/fxml/OpenMarketForm.fxml");
            VBox root = loader.getRoot();
            OpenMarketController ctrl = loader.getController();

            Stage dialog = new Stage();
            dialog.initOwner(tableMarkets.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Open Market");
            dialog.setScene(new Scene(root));

            ctrl.setDialogStage(dialog);
            ctrl.setOnSave(this::loadMarkets);

            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
