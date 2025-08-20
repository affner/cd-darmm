package com.cwdarmm.controller;

/**
 * Muestra la evolución del riesgo trade a trade y
 * permite abrir el formulario de configuración.
 */

import com.cwdarmm.config.SpringFXMLLoader;
import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.model.dto.OptimalContractRow;
import com.cwdarmm.model.dto.ResultRowDTO;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.service.OutputService;
import com.cwdarmm.service.RiskAnalysisService;
import com.cwdarmm.service.OptimizationService;
import com.cwdarmm.controller.OptimalContractsController;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class MarketRiskDashboardController {
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
    @FXML private ResourceBundle resources;

    private final OutputService outputService;
    private final SpringFXMLLoader springFXMLLoader;
    private final RiskAnalysisService riskAnalysisService; // inyectado con Spring
    private final OptimizationService optimizationService;
    private final Map<String, Stage> popups = new HashMap<>();
    private MarketDTO context;

    public void setContext(MarketDTO context) {
        this.context = context;
        lblContext.setText(context.getMarket().getDescription());
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
        colWl.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item==null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    if ("WIN".equalsIgnoreCase(item)) {
                        setStyle("-fx-background-color:#92D050;");
                    } else if ("LOSS".equalsIgnoreCase(item)) {
                        setStyle("-fx-background-color:#FF0000; -fx-text-fill:white;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        // <<— Aquí sacamos el name manualmente en vez de "account.name" —>>
        colAccount.setCellValueFactory(feat -> {
            var acc = feat.getValue().getAccount();
            String txt = (acc != null ? acc.getDescription() : "");
            return new ReadOnlyStringWrapper(txt);
        });

        colMarketData.setCellValueFactory(feat -> {
            var fd = feat.getValue().getMarketData();
            String txt = (fd != null ? fd.getDescription() : "");
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
                new Alert(Alert.AlertType.INFORMATION, resources.getString("alert.csv.exported") + "\n" + file)
                        .showAndWait();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, resources.getString("alert.csv.error") + "\n" + e.getMessage())
                        .showAndWait();
            }
        });
    }

    @FXML
    private void onRiskManage() throws IOException {
        // Cargamos el FXML y obtenemos el controller
        FXMLLoader loader = springFXMLLoader.load("/fxml/RiskConfigForm.fxml");
        RiskConfigController formCtrl = loader.getController();

        // Construimos un único Stage y se lo pasamos al formulario
        Stage dialog = new Stage();
        formCtrl.setDialogStage(dialog);
        formCtrl.setMarketContext(context);
        formCtrl.setOnCalculated(req -> {
            List<RiskResultDTO> rows = riskAnalysisService.calculate(req);
            var items = tableResults.getItems();

            if (req.isFirstTrade()) {
                items.clear();
                items.addAll(rows);
            } else {
                int lastTrade = items.isEmpty() ? 0 : items.get(items.size()-1).getTradeNumber();
                BigDecimal prevAcc = items.isEmpty() ? context.getAccountSize() : items.get(items.size()-1).getAccountSize();
                RiskResultDTO r = rows.get(0);
                r.setTradeNumber(lastTrade + 1);

                List<ResultRowDTO> resultRows = optimizationService.calculate(req);
                if (!resultRows.isEmpty()) {
                    ResultRowDTO or = resultRows.get(0);
                    BigDecimal risk = BigDecimal.valueOf(or.getPotentialLoss().get());
                    BigDecimal profit = BigDecimal.valueOf(or.getPotentialProfit().get());
                    if ("WIN".equalsIgnoreCase(r.getWl())) {
                        r.setAccountSize(prevAcc.subtract(risk).add(profit));
                    } else if ("LOSS".equalsIgnoreCase(r.getWl())) {
                        r.setAccountSize(prevAcc.subtract(risk));
                    } else {
                        r.setAccountSize(prevAcc);
                    }
                } else {
                    r.setAccountSize(prevAcc);
                }

                items.add(r);
            }
        });
        formCtrl.setOnOptimalContracts((req, rows) -> {
            try {
                showOptimalContracts(req, rows);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        dialog.initOwner(tableResults.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(context.getMarket().getDescription() + " – " + resources.getString("risk.dashboard.window"));
        dialog.setScene(new Scene(loader.getRoot()));
        dialog.showAndWait();
    }


    @FXML private void onClose() {
        ((Stage)tableResults.getScene().getWindow()).close();
    }

    private void showOptimalContracts(RiskInputDTO req, List<OptimalContractRow> rows) throws IOException {
        String key = req.getAccount().getId() + "-" + req.getMarket().getId() + "-" + req.getStopLossSize();
        Stage stage = popups.get(key);
        if (stage != null) {
            OptimalContractsController ctrl = (OptimalContractsController) stage.getUserData();
            ctrl.setItems(rows);
            stage.toFront();
        } else {
            FXMLLoader loader = springFXMLLoader.load("/fxml/OptimalContractsView.fxml");
            OptimalContractsController ctrl = loader.getController();
            ctrl.setCriteriaAccount(req.getAccount().getDescription());
            ctrl.setMarket(req.getMarket());
            ctrl.setItems(rows);
            Stage popup = new Stage();
            popup.initOwner(tableResults.getScene().getWindow());
            popup.initModality(Modality.NONE);
            popup.setTitle(req.getAccount().getDescription() + " – " + resources.getString("optimal.contracts.window"));
            popup.setScene(new Scene(loader.getRoot()));
            popup.setUserData(ctrl);
            popup.setOnCloseRequest(e -> popups.remove(key));
            popups.put(key, popup);
            popup.show();
        }
    }
}
