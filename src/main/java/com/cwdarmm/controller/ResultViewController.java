package com.cwdarmm.controller;

/**
 * Controla la tabla de resultados optimizados de la pestaña
 * "Results" y permite simular el cálculo.
 */

import com.cwdarmm.service.OptimizationService;
import com.cwdarmm.service.MarketDataService;
import com.cwdarmm.model.dto.ResultRowDTO;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.MarketDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
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

    // Servicios que encapsulan la lógica de cálculo y el acceso a markets
    private final OptimizationService optimizationService;
    private final MarketDataService marketDataService;

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

        // Estilos de columnas según colores del XLSM
        colSlSize.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item==null) { setText(null); setStyle(""); }
                else {
                    setText(item.toString());
                    setStyle("-fx-background-color:#FF0000; -fx-text-fill:white;");
                }
            }
        });

        colTarget.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item==null) { setText(null); setStyle(""); }
                else {
                    setText(item.toString());
                    setStyle("-fx-background-color:#D3D3D3;");
                }
            }
        });

        var coloredFactory = new Callback<TableColumn<ResultRowDTO, ?>, TableCell<ResultRowDTO, ?>>() {
            @Override
            public TableCell<ResultRowDTO, ?> call(TableColumn<ResultRowDTO, ?> c) {
                return new TableCell<>() {
                    @Override protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item==null) { setText(null); setStyle(""); }
                        else {
                            setText(item.toString());
                            ResultRowDTO row = getTableView().getItems().get(getIndex());
                            String color = row.getRowColor().get();
                            if (color!=null && !color.isBlank()) {
                                setStyle("-fx-background-color:" + color + ";");
                            } else setStyle("");
                        }
                    }
                };
            }
        };
        colSymbol.setCellFactory(coloredFactory);
        colOptimalContract.setCellFactory(coloredFactory);

        tblResults.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(ResultRowDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item==null) { setStyle(""); }
                else if (item.getOptimalRow().get()) {
                    setStyle("-fx-background-color:yellow;");
                } else {
                    setStyle("");
                }
            }
        });

        // De momento cargamos vacío; al pulsar “Click” se rellenará
        tblResults.setItems(FXCollections.observableArrayList());
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /**
     * Ejecuta el cálculo real de la pestaña Results utilizando la primera
     * configuración guardada en la tabla OpenMarket como ejemplo.
     */
    @FXML
    private void onClick() {
        List<MarketDTO> markets = marketDataService.findAll();
        if (markets.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No market sessions configured").showAndWait();
            return;
        }

        MarketDTO m = markets.get(0);
        RiskInputDTO req = RiskInputDTO.builder()
                .account(m.getAccount())
                .market(m.getMarket())
                .marketData(m.getMarketData())
                .accountSize(m.getAccountSize())
                .riskReward(2.0)
                .ticksSl1(10)
                .ticksSl2(15)
                .house(true)
                .firstTrade(true)
                .riskPctA(java.math.BigDecimal.valueOf(m.getRiskA()))
                .build();

        List<ResultRowDTO> rows = optimizationService.calculate(req);
        tblResults.setItems(FXCollections.observableArrayList(rows));
    }

    @FXML
    private void onClose() {
        if (dialogStage != null) dialogStage.close();
    }
}
