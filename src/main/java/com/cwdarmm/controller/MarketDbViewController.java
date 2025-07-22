package com.cwdarmm.controller;

import com.cwdarmm.model.dto.MarketDbRowDTO;
import com.cwdarmm.service.MarketDbService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MarketDbViewController {

    @FXML private TableView<MarketDbRowDTO> tblMarketDb;
    @FXML private TableColumn<MarketDbRowDTO, String>  colFuture;
    @FXML private TableColumn<MarketDbRowDTO, String>  colAccount;
    @FXML private TableColumn<MarketDbRowDTO, String>  colMarketData;
    @FXML private TableColumn<MarketDbRowDTO, String>  colName;
    @FXML private TableColumn<MarketDbRowDTO, String>  colSymbol;
    @FXML private TableColumn<MarketDbRowDTO, Double>  colMultiplier;
    @FXML private TableColumn<MarketDbRowDTO, Double>  colTickSize;
    @FXML private TableColumn<MarketDbRowDTO, Double>  colTickValue;
    @FXML private TableColumn<MarketDbRowDTO, Double>  colMargin;
    @FXML private TableColumn<MarketDbRowDTO, Double>  colCommission;

    private final MarketDbService marketDbService;
    private Stage dialogStage;

    @FXML
    public void initialize() {
        // binding columnas ↔ propiedades DTO
        colFuture     .setCellValueFactory(cd -> cd.getValue().getFuture());
        colAccount    .setCellValueFactory(cd -> cd.getValue().getAccount());
        colMarketData .setCellValueFactory(cd -> cd.getValue().getMarketData());
        colName       .setCellValueFactory(cd -> cd.getValue().getName());
        colSymbol     .setCellValueFactory(cd -> cd.getValue().getSymbol());
        colMultiplier .setCellValueFactory(cd -> cd.getValue().getMultiplier().asObject());
        colTickSize   .setCellValueFactory(cd -> cd.getValue().getTickSize().asObject());
        colTickValue  .setCellValueFactory(cd -> cd.getValue().getTickValue().asObject());
        colMargin     .setCellValueFactory(cd -> cd.getValue().getMargin().asObject());
        colCommission .setCellValueFactory(cd -> cd.getValue().getCommission().asObject());

        // cargamos datos al inicio
        refreshTable();
    }

    private void refreshTable() {
        List<MarketDbRowDTO> rows = marketDbService.listAll();
        tblMarketDb.setItems(FXCollections.observableArrayList(rows));
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    @FXML
    private void onRefresh() {
        refreshTable();
    }

    @FXML
    private void onClose() {
        if (dialogStage != null) dialogStage.close();
    }
}
