package com.cwdarmm.controller;

/**
 * Muestra la evolución del riesgo trade a trade y
 * permite abrir el formulario de configuración.
 */

import com.cwdarmm.config.SpringFXMLLoader;
import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.service.OutputService;
import com.cwdarmm.service.RiskAnalysisService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class MarketRiskDashboardController {
    @FXML
    private Label lblContext;
    @FXML private TableView<RiskResultDTO> tableResults;
    @FXML private VBox tablesContainer;
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
    private MarketDTO context;
    private int tradesCount = 0;
    private TableView<RiskResultDTO> currentTable;
    private static final int MAX_TRADES_PER_TABLE = 2;

    public void setContext(MarketDTO context) {
        this.context = context;
        lblContext.setText(context.getAccount().getDescription());
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
        currentTable = tableResults;
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

        tableResults.setOnMouseClicked(e -> currentTable = tableResults);

        btnExportCsv.setOnAction(evt -> {
            try {
                var list = currentTable.getItems();
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
            if (req.isFirstTrade()) {
                currentTable.getItems().clear();
            }
            for (RiskResultDTO row : rows) {
                if (row.getTradeNumber() == 1) {
                    tradesCount++;
                }
                currentTable.getItems().add(row);
                if (tradesCount > MAX_TRADES_PER_TABLE ||
                        (row.getWl() != null && row.getWl().equalsIgnoreCase("WIN"))) {
                    startNewTable(row);
                    tradesCount = 0;
                }
            }
        });
        dialog.initOwner(currentTable.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(context.getAccount().getDescription() + " – " + resources.getString("risk.dashboard.window"));
        dialog.setScene(new Scene(loader.getRoot()));
        dialog.showAndWait();
    }


    private void startNewTable(RiskResultDTO last) {
        TableView<RiskResultDTO> tv = createResultTable();
        tv.setOnMouseClicked(e -> currentTable = tv);
        tablesContainer.getChildren().add(tv);
        currentTable = tv;
        RiskResultDTO init = RiskResultDTO.builder()
                .tradeNumber(0).wl("INITIAL")
                .account(last.getAccount())
                .marketData(last.getMarketData())
                .accountSize(last.getAccountSize())
                .riskKellyA(last.getRiskKellyA())
                .riskKellyB(last.getRiskKellyB())
                .build();
        tv.setItems(FXCollections.observableArrayList(init));
    }

    private TableView<RiskResultDTO> createResultTable() {
        TableView<RiskResultDTO> tv = new TableView<>();

        TableColumn<RiskResultDTO, Integer> trade = new TableColumn<>(resources.getString("risk.dashboard.trade"));
        trade.setCellValueFactory(new PropertyValueFactory<>("tradeNumber"));

        TableColumn<RiskResultDTO, String> wl = new TableColumn<>(resources.getString("risk.dashboard.wl"));
        wl.setCellValueFactory(new PropertyValueFactory<>("wl"));
        wl.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    if ("WIN".equalsIgnoreCase(item)) {
                        setStyle("-fx-background-color:#92D050;");
                    } else if ("LOSS".equalsIgnoreCase(item)) {
                        setStyle("-fx-background-color:#FF0000; -fx-text-fill:white;");
                    } else { setStyle(""); }
                }
            }
        });

        TableColumn<RiskResultDTO, String> acc = new TableColumn<>(resources.getString("risk.dashboard.account"));
        acc.setCellValueFactory(feat -> {
            var a = feat.getValue().getAccount();
            String txt = (a != null ? a.getDescription() : "");
            return new ReadOnlyStringWrapper(txt);
        });

        TableColumn<RiskResultDTO, String> market = new TableColumn<>(resources.getString("risk.dashboard.marketdata"));
        market.setCellValueFactory(feat -> {
            var fd = feat.getValue().getMarketData();
            String txt = (fd != null ? fd.getDescription() : "");
            return new ReadOnlyStringWrapper(txt);
        });

        TableColumn<RiskResultDTO, BigDecimal> size = new TableColumn<>(resources.getString("risk.dashboard.accountsize"));
        size.setCellValueFactory(feat -> new ReadOnlyObjectWrapper<>(feat.getValue().getAccountSize()));

        TableColumn<RiskResultDTO, Double> riskA = new TableColumn<>(resources.getString("risk.dashboard.riska"));
        riskA.setCellValueFactory(feat -> new ReadOnlyObjectWrapper<>(feat.getValue().getRiskKellyA()));

        TableColumn<RiskResultDTO, Double> riskB = new TableColumn<>(resources.getString("risk.dashboard.riskb"));
        riskB.setCellValueFactory(feat -> new ReadOnlyObjectWrapper<>(feat.getValue().getRiskKellyB()));

        tv.getColumns().addAll(trade, wl, acc, market, size, riskA, riskB);
        return tv;
    }

    @FXML private void onClose() {
        ((Stage) currentTable.getScene().getWindow()).close();
    }
}
