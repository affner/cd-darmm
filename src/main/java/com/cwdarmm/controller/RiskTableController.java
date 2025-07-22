package com.cwdarmm.controller;

import com.cwdarmm.config.SpringFXMLLoader;
import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.service.ExportService;
import com.cwdarmm.service.OutputService;
import com.cwdarmm.service.RiskService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import java.math.BigDecimal;
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
    @FXML private TableColumn<RiskResultDTO, BigDecimal>  colAccountSize;
    @FXML private TableColumn<RiskResultDTO,Double>  colRiskA;
    @FXML private TableColumn<RiskResultDTO,Double>  colRiskB;
    @FXML private Button btnExportCsv;

    private final OutputService outputService;
    private final SpringFXMLLoader springFXMLLoader;
    private final RiskService riskService; // inyectado con Spring
    private MarketDTO context;

    public void setContext(MarketDTO context) {
        this.context = context;
        lblContext.setText(context.getMarket().getName());
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

    @FXML
    public void initialize() {
        colTrade.setCellValueFactory(new PropertyValueFactory<>("tradeNumber"));
        colWl   .setCellValueFactory(new PropertyValueFactory<>("wl"));

        // <<— Aquí sacamos el name manualmente en vez de "account.name" —>>
        colAccount.setCellValueFactory(feat -> {
            var acc = feat.getValue().getAccount();
            String txt = (acc != null ? acc.getName() : "");
            return new ReadOnlyStringWrapper(txt);
        });

        colMarketData.setCellValueFactory(feat -> {
            var fd = feat.getValue().getMarketData();
            String txt = (fd != null ? fd.getName() : "");
            return new ReadOnlyStringWrapper(txt);
        });

        // Para los doubles podemos seguir con wrappers
        colAccountSize.setCellValueFactory(feat ->
                new ReadOnlyObjectWrapper<>(feat.getValue().getAccountSize()));
        colRiskA       .setCellValueFactory(feat ->
                new ReadOnlyObjectWrapper<>(feat.getValue().getRiskKellyA()));
        colRiskB       .setCellValueFactory(feat ->
                new ReadOnlyObjectWrapper<>(feat.getValue().getRiskKellyB()));

        btnExportCsv.setOnAction(evt -> {
            try {
                var list = tableResults.getItems();
                Path file = Path.of(System.getProperty("user.home"), "risk-results.csv");
                outputService.exportRiskResultsToCsv(list, file);
                new Alert(Alert.AlertType.INFORMATION, "CSV exportado en:\n" + file)
                        .showAndWait();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Error al exportar CSV:\n" + e.getMessage())
                        .showAndWait();
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
