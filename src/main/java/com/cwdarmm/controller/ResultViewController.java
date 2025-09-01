package com.cwdarmm.controller;

/**
 * Controla la tabla de resultados optimizados de la pestaña
 * "Results" y permite simular el cálculo.
 */

import com.cwdarmm.service.OptimizationService;
import com.cwdarmm.model.dto.ResultRowDTO;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.service.RiskContext;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.ResourceBundle;
import java.util.Locale;

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

    // Servicios que encapsulan la lógica de cálculo y el acceso a la última
    // selección de parámetros (RiskContext)
    private final OptimizationService optimizationService;
    private final RiskContext riskContext;
    private Stage dialogStage;

    @FXML private ResourceBundle resources;

    private RiskInputDTO request;


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
        colRiskPct.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format(Locale.US, "%.6f", item));
            }
        });

        // Estilos de filas resaltadas

        // 4) RowFactory para filas óptimas
        tblResults.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(ResultRowDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    getStyleClass().remove("optimal-row");
                } else if (item.getOptimalRow().get()) {
                    if (!getStyleClass().contains("optimal-row")) {
                        getStyleClass().add("optimal-row");
                    }
                } else {
                    getStyleClass().remove("optimal-row");
                }
            }
        });

        // 5) Inicialmente vacío
        tblResults.setItems(FXCollections.observableArrayList());
    }

    /**
     * Permite inyectar desde fuera la configuración con la que
     * se desea ejecutar la optimización.
     */
    public void setRequest(RiskInputDTO request) {
        this.request = request;
        riskContext.set(request); // <-- importante
    }

    /**
     * Ejecuta el cálculo real de la pestaña Results.
     *
     * <p>Si otro controlador proporcionó un {@link RiskInputDTO} mediante
     * {@link #setRequest(RiskInputDTO)}, se utilizará esa configuración
     * exactamente. En caso contrario se intentará usar la primera sesión
     * almacenada en la base de datos como ejemplo sencillo.</p>
     */
    @FXML
    public void onClick() {
        // 1) Preferimos el request inyectado directamente por otro controlador
        RiskInputDTO req = this.request;

        // 2) Si no llegó, usamos el último guardado por Risk Manager
        if (req == null) req = riskContext.get();

        // 3) Si sigue sin existir configuración mostramos un mensaje y salimos
        if (req == null) {
            new Alert(Alert.AlertType.INFORMATION,
                    resources.getString("alert.norisk")).showAndWait();
            return;
        }

        // 4) Calcular y poblar la tabla
        List<ResultRowDTO> rows = optimizationService.calculate(req);
        tblResults.setItems(FXCollections.observableArrayList(rows));
    }

    @FXML
    private void onClose() {
        if (dialogStage != null) dialogStage.close();
    }
}
