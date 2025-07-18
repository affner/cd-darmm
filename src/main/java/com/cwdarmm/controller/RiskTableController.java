package com.cwdarmm.controller;

import com.cwdarmm.config.SpringFXMLLoader;
import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.service.ExportService;
import com.cwdarmm.service.OutputService;
import com.cwdarmm.service.RiskService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RiskTableController {
    @FXML
    private Label lblContext;
    @FXML private TableView<RiskResultDTO> tableResults;
    // columnas:
    @FXML private TableColumn<RiskResultDTO,Integer> colTrade;
    @FXML private TableColumn<RiskResultDTO,String>  colWl;
    @FXML private TableColumn<RiskResultDTO,String>  colAccount;
    @FXML private TableColumn<RiskResultDTO,String>  colMarketData;
    @FXML private TableColumn<RiskResultDTO,Double>  colAccountSize;
    @FXML private TableColumn<RiskResultDTO,Double>  colRiskA;
    @FXML private TableColumn<RiskResultDTO,Double>  colRiskB;
    @FXML private Button btnExportCsv;

    private final ExportService exportService;
    private final OutputService outputService;
    private final SpringFXMLLoader springFXMLLoader;
    private final RiskService riskService; // inyectado con Spring
    private MarketDTO context;

    public void setContext(MarketDTO context) {
        this.context = context;
        lblContext.setText(context.getMarket());
        // inicializar la tabla con el resultado inicial (trade 0):
        var initial = List.of(RiskResultDTO.builder()
                .tradeNumber(0).wl("INITIAL")
                .account(context.getAccount())
                .marketData(context.getMarketData())
                .accountSize(context.getAccountSize())
                .riskKellyA(context.getRiskA())
                .riskKellyB(context.getRiskB())
                .build());
        tableResults.setItems(FXCollections.observableArrayList(initial));
    }

    @FXML public void initialize() {
        colTrade      .setCellValueFactory(new PropertyValueFactory<>("tradeNumber"));
        colWl         .setCellValueFactory(new PropertyValueFactory<>("wl"));
        colAccount    .setCellValueFactory(new PropertyValueFactory<>("account"));
        colMarketData .setCellValueFactory(new PropertyValueFactory<>("marketData"));
        colAccountSize.setCellValueFactory(new PropertyValueFactory<>("accountSize"));
        colRiskA      .setCellValueFactory(new PropertyValueFactory<>("riskKellyA"));
        colRiskB      .setCellValueFactory(new PropertyValueFactory<>("riskKellyB"));
        btnExportCsv.setOnAction(evt -> {
            try {
                // suponiendo que tableResults ya tiene los datos
                var list = tableResults.getItems();
                // eliges la ubicación, por ejemplo:
                Path file = Path.of(System.getProperty("user.home"), "risk-results.csv");
                outputService.exportRiskResultsToCsv(list, file);
                // muestra confirmación
                new Alert(Alert.AlertType.INFORMATION, "CSV exportado en:\n" + file).showAndWait();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Error al exportar CSV:\n" + e.getMessage()).showAndWait();
            }
        });
    }

    @FXML
    private void onRiskManage() throws IOException {
        // Cargamos el FXML y obtenemos el controller
        FXMLLoader loader = springFXMLLoader.load("/fxml/RiskForm.fxml");
        RiskFormController formCtrl = loader.getController();

        // Construimos un único Stage y se lo pasamos al formulario
        Stage dialog = new Stage();
        formCtrl.setDialogStage(dialog);
        formCtrl.setMarketContext(context);
        formCtrl.setOnCalculated(() -> {
            // Actualizar tabla con resultados recalculados
            RiskInputDTO req = formCtrl.buildRequest();
            List<RiskResultDTO> recalculated = riskService.calculate(req);
            tableResults.setItems(FXCollections.observableArrayList(recalculated));
        });

        dialog.initOwner(tableResults.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(context.getMarket() + " – Risk Manager");
        dialog.setScene(new Scene(loader.getRoot()));
        dialog.showAndWait();
    }


    @FXML private void onClose() {
        ((Stage)tableResults.getScene().getWindow()).close();
    }
}
