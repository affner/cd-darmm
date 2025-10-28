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
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    @FXML private TableColumn<RiskResultDTO, Void>   colActions;
    @FXML private Button btnExportCsv;
    @FXML private ResourceBundle resources;

    private final OutputService outputService;
    private final SpringFXMLLoader springFXMLLoader;
    private MarketDTO context;
    private final Map<RiskResultDTO, RiskInputDTO> requestByResult = new IdentityHashMap<>();

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
        requestByResult.clear();
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

        initActionsColumn();
    }

    private void initActionsColumn() {
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnDelete = new Button("✖");
            private final HBox pane = new HBox(5, btnEdit, btnDelete);

            {
                pane.setAlignment(Pos.CENTER);
                btnEdit.setOnAction(e -> {
                    RiskResultDTO dto = getTableView().getItems().get(getIndex());
                    onEditResult(dto);
                });
                btnDelete.setOnAction(e -> {
                    RiskResultDTO dto = getTableView().getItems().get(getIndex());
                    onDeleteResult(dto);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getIndex() < 0) {
                    setGraphic(null);
                } else {
                    RiskResultDTO dto = getTableView().getItems().get(getIndex());
                    btnEdit.setDisable(!requestByResult.containsKey(dto));
                    setGraphic(pane);
                }
            }
        });
    }

    private void onEditResult(RiskResultDTO dto) {
        RiskInputDTO baseRequest = requestByResult.get(dto);
        if (baseRequest == null) {
            new Alert(Alert.AlertType.INFORMATION,
                    resources.getString("risk.dashboard.edit.unavailable"))
                    .showAndWait();
            return;
        }

        try {
            FXMLLoader loader = springFXMLLoader.load("/fxml/RiskConfigForm.fxml");
            RiskConfigController formCtrl = loader.getController();

            Stage dialog = new Stage();
            dialog.initOwner(tableResults.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(context.getMarket().getDescription() + " – "
                    + resources.getString("risk.dashboard.edit.window"));
            dialog.setScene(new Scene(loader.getRoot()));

            formCtrl.setDialogStage(dialog);
            formCtrl.setMarketContext(context);
            formCtrl.setInitialRequest(baseRequest);
            formCtrl.setOnCalculated((req, rows) -> {
                if (req.isFirstTrade()) {
                    tableResults.getItems().setAll(rows);
                    requestByResult.clear();
                    rows.forEach(r -> requestByResult.put(r, req.toBuilder().build()));
                } else if (!rows.isEmpty()) {
                    RiskResultDTO calculated = rows.get(rows.size() - 1);
                    applyResultValues(dto, calculated);
                    requestByResult.put(dto, req.toBuilder().build());
                }

                if (req.getRiskPctA() != null) {
                    context.setRiskA(req.getRiskPctA().doubleValue());
                }
                if (req.getRiskPctB() != null) {
                    context.setRiskB(req.getRiskPctB().doubleValue());
                }

                tableResults.refresh();
            });

            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onDeleteResult(RiskResultDTO dto) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                resources.getString("risk.dashboard.delete.confirm"),
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> answer = confirm.showAndWait();
        if (answer.orElse(ButtonType.NO) == ButtonType.YES) {
            tableResults.getItems().remove(dto);
            requestByResult.remove(dto);
            tableResults.refresh();
        }
    }

    private void applyResultValues(RiskResultDTO target, RiskResultDTO source) {
        target.setWl(source.getWl());
        target.setAccount(source.getAccount());
        target.setMarketData(source.getMarketData());
        target.setAccountSize(source.getAccountSize());
        target.setRiskKellyA(source.getRiskKellyA());
        target.setRiskKellyB(source.getRiskKellyB());
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
        formCtrl.setOnCalculated((req, rows) -> {
            var items = tableResults.getItems();

            if (req.isFirstTrade()) {
                // primer trade: limpiamos y mostramos sólo INITIAL
                items.clear();
                requestByResult.clear();
            }
            // tanto si es primer trade como posterior, añadimos las filas calculadas
            items.addAll(rows);
            rows.forEach(r -> requestByResult.put(r, req.toBuilder().build()));

            if (req.getRiskPctA() != null) {
                context.setRiskA(req.getRiskPctA().doubleValue());
            }
            if (req.getRiskPctB() != null) {
                context.setRiskB(req.getRiskPctB().doubleValue());
            }

            tableResults.refresh();
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
}
