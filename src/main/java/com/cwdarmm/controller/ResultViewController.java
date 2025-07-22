package com.cwdarmm.controller;

import com.cwdarmm.service.OptimizationService;
import com.cwdarmm.model.dto.ResultRowDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ResultViewController {

    @FXML private TableView<ResultRowDTO> tblResults;
    @FXML private TableColumn<ResultRowDTO, String> colAsset;
    @FXML private TableColumn<ResultRowDTO, String> colBroker;
    @FXML private TableColumn<ResultRowDTO, String> colSymbol;
    @FXML private TableColumn<ResultRowDTO, Integer> colTarget;
    @FXML private TableColumn<ResultRowDTO, Integer> colSlSize;
    @FXML private TableColumn<ResultRowDTO, Double> colRiskPerContract;
    @FXML private TableColumn<ResultRowDTO, Integer> colOptimalContract;
    @FXML private TableColumn<ResultRowDTO, Double> colCapitalUsed;
    @FXML private TableColumn<ResultRowDTO, Double> colRealRisk;
    @FXML private TableColumn<ResultRowDTO, Double> colProfit;
    @FXML private TableColumn<ResultRowDTO, Double> colLoss;
    @FXML private TableColumn<ResultRowDTO, Double> colRiskPct;

    // Servicio que encapsulará la lógica del “Results” del XLSM
    private final OptimizationService optimizationService;

    private Stage dialogStage;

    @FXML
    public void initialize() {
        // Configuramos cada columna para que lea la propiedad correspondiente de ResultRowDTO
        colAsset              .setCellValueFactory(cd -> cd.getValue().getAsset());
        colBroker             .setCellValueFactory(cd -> cd.getValue().getBroker());
        colSymbol             .setCellValueFactory(cd -> cd.getValue().getSymbol());
        colTarget             .setCellValueFactory(cd -> cd.getValue().getTarget().asObject());
        colSlSize             .setCellValueFactory(cd -> cd.getValue().getSlSize().asObject());
        colRiskPerContract    .setCellValueFactory(cd -> cd.getValue().getRiskPerContract().asObject());
        colOptimalContract    .setCellValueFactory(cd -> cd.getValue().getOptimalContract().asObject());
        colCapitalUsed        .setCellValueFactory(cd -> cd.getValue().getCapitalUsed().asObject());
        colRealRisk           .setCellValueFactory(cd -> cd.getValue().getRealRisk().asObject());
        colProfit             .setCellValueFactory(cd -> cd.getValue().getPotentialProfit().asObject());
        colLoss               .setCellValueFactory(cd -> cd.getValue().getPotentialLoss().asObject());
        colRiskPct            .setCellValueFactory(cd -> cd.getValue().getRiskPercentage().asObject());

        // De momento cargamos vacío; al pulsar “Click” se rellenará
        tblResults.setItems(FXCollections.observableArrayList());
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /** Simula el botón CLICK de la hoja RESULTS y pinta filas “óptimas” según lógica. */
    @FXML
    private void onClick() {
        // TODO: reemplazar con: List<ResultRowDTO> rows = optimizationService.calculate(...);
        List<ResultRowDTO> rows = optimizationService.dummyCalculate();
        tblResults.setItems(FXCollections.observableArrayList(rows));
    }

    @FXML
    private void onClose() {
        if (dialogStage != null) dialogStage.close();
    }
}
