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
        // 1) Construir la petición inicial y rellenar la tabla con trade 0
        RiskInputDTO initialReq = RiskInputDTO.builder()
                .account(   context.getAccount()   )
                .market(    context.getMarket()    )
                .marketData(context.getMarketData())
                .accountSize(context.getAccountSize())
                // Tomamos riskA como reward inicial
                .riskReward(context.getRiskA())
                // Valores por defecto para ticks y stop loss
                .ticksSl1(0).ticksSl2(0).stopLossSize(0)
                // Flags iniciales (no influyen)
                .house(true).lunch(false).win(false).loss(false)
                .build();

        List<RiskResultDTO> results = riskService.calculate(initialReq);
        tableResults.setItems(FXCollections.observableArrayList(results));

        // 2) Preguntar si es el primer trade (puedes usar este flag si cambias la lógica)
        boolean firstTrade = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Es este el primer trade?")
                .showAndWait()
                .filter(ButtonType.OK::equals)
                .isPresent();

        // 3) Preguntar si quiere generar el XML de estrategia
        if (new Alert(Alert.AlertType.CONFIRMATION,
                "¿Deseas generar el XML de estrategia?")
                .showAndWait()
                .filter(ButtonType.OK::equals)
                .isPresent()) {

            Path xmlFile = Path.of(System.getProperty("user.home"),
                    context.getMarket() + ".xml");
            // exportService.exportStrategyToXml(MarketDTO, List<Result>, Path)
            exportService.exportStrategyToXml(context, results, xmlFile);
            new Alert(Alert.AlertType.INFORMATION,
                    "XML generado en:\n" + xmlFile)
                    .showAndWait();
        }

        // 4) Preguntar si quiere generar el script AutoHotkey
        if (new Alert(Alert.AlertType.CONFIRMATION,
                "¿Deseas generar el script AutoHotkey?")
                .showAndWait()
                .filter(ButtonType.OK::equals)
                .isPresent()) {

            Path ahkFile = Path.of(System.getProperty("user.home"),
                    context.getMarket() + ".ahk");
            // exportService.exportStrategyToAhk(MarketDTO, List<Result>, Path)
          //  exportService.exportStrategyToAhk(context, results, ahkFile);
            new Alert(Alert.AlertType.INFORMATION,
                    "AHK generado en:\n" + ahkFile)
                    .showAndWait();
        }

        // 5) Abrir finalmente el formulario de inputs para ajustar parámetros
        FXMLLoader loader = springFXMLLoader.load("/fxml/RiskForm.fxml");
        RiskFormController formCtrl = loader.getController();
        formCtrl.setDialogStage(new Stage());
        formCtrl.setMarketContext(context);
        formCtrl.setOnCalculated(() -> {
            // cuando re-calculamos desde el form, refrescamos la tabla
            RiskInputDTO req = formCtrl.buildRequest();
            List<RiskResultDTO> recalculated = riskService.calculate(req);
            tableResults.setItems(FXCollections.observableArrayList(recalculated));
        });

        Stage dialog = new Stage();
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
