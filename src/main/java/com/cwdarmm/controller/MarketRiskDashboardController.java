package com.cwdarmm.controller;

/**
 * Muestra la evolución del riesgo trade a trade y
 * permite abrir el formulario de configuración.
 */

import com.cwdarmm.config.SpringFXMLLoader;
import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.service.OutputService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
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
    @FXML private TableColumn<RiskResultDTO, Void>  colActions;
    @FXML private Button btnExportCsv;
    @FXML private ResourceBundle resources;

    private final OutputService outputService;
    private final SpringFXMLLoader springFXMLLoader;
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

        initActionsColumn();
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
            }
            // tanto si es primer trade como posterior, añadimos las filas calculadas
            items.addAll(rows);

            if (req.getRiskPctA() != null) {
                context.setRiskA(req.getRiskPctA().doubleValue());
            }
            if (req.getRiskPctB() != null) {
                context.setRiskB(req.getRiskPctB().doubleValue());
            }
        });
        dialog.initOwner(tableResults.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(context.getMarket().getDescription() + " – " + resources.getString("risk.dashboard.window"));
        dialog.setScene(new Scene(loader.getRoot()));
        dialog.showAndWait();
    }


    private void initActionsColumn() {
        if (colActions == null) {
            return;
        }

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnDelete = new Button("✖");
            private final HBox pane = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.setOnAction(evt -> {
                    int index = getIndex();
                    if (index < 0 || index >= getTableView().getItems().size()) {
                        return;
                    }
                    RiskResultDTO dto = getTableView().getItems().get(index);
                    onEditRow(dto);
                });
                btnDelete.setOnAction(evt -> {
                    int index = getIndex();
                    if (index < 0 || index >= getTableView().getItems().size()) {
                        return;
                    }
                    RiskResultDTO dto = getTableView().getItems().get(index);
                    onDeleteRow(dto);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    int index = getIndex();
                    if (index < 0 || index >= getTableView().getItems().size()) {
                        setGraphic(null);
                        return;
                    }
                    RiskResultDTO dto = getTableView().getItems().get(index);
                    btnDelete.setDisable(dto.getTradeNumber() == 0);
                    setGraphic(pane);
                }
            }
        });
    }

    private void onEditRow(RiskResultDTO dto) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(tableResults.getScene().getWindow());
        dialog.setTitle(resources.getString("risk.dashboard.edit.title"));
        dialog.setHeaderText(resources.getString("risk.dashboard.edit.header"));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField tfRiskA = new TextField(dto.getRiskKellyA() == null ? "" : dto.getRiskKellyA().toString());
        TextField tfRiskB = new TextField(dto.getRiskKellyB() == null ? "" : dto.getRiskKellyB().toString());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label(resources.getString("risk.dashboard.riska")), 0, 0);
        grid.add(tfRiskA, 1, 0);
        grid.add(new Label(resources.getString("risk.dashboard.riskb")), 0, 1);
        grid.add(tfRiskB, 1, 1);
        dialog.getDialogPane().setContent(grid);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, evt -> {
            try {
                Double newRiskA = parseNullableDouble(tfRiskA.getText());
                Double newRiskB = parseNullableDouble(tfRiskB.getText());
                dto.setRiskKellyA(newRiskA);
                dto.setRiskKellyB(newRiskB);
                if (dto.getTradeNumber() == 0 && context != null) {
                    if (newRiskA != null) {
                        context.setRiskA(newRiskA);
                    }
                    if (newRiskB != null) {
                        context.setRiskB(newRiskB);
                    }
                }
            } catch (NumberFormatException ex) {
                evt.consume();
                new Alert(Alert.AlertType.ERROR, resources.getString("risk.dashboard.edit.error"))
                        .showAndWait();
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            tableResults.refresh();
        }
    }

    private void onDeleteRow(RiskResultDTO dto) {
        if (dto.getTradeNumber() == 0) {
            new Alert(Alert.AlertType.WARNING, resources.getString("risk.dashboard.delete.initial"))
                    .showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                resources.getString("risk.dashboard.delete.confirm"), ButtonType.YES, ButtonType.NO);
        confirm.initOwner(tableResults.getScene().getWindow());
        Optional<ButtonType> response = confirm.showAndWait();
        if (response.orElse(ButtonType.NO) == ButtonType.YES) {
            tableResults.getItems().remove(dto);
            tableResults.refresh();
        }
    }

    private Double parseNullableDouble(String text) {
        String value = text == null ? "" : text.trim();
        if (value.isEmpty()) {
            return null;
        }
        return Double.valueOf(value);
    }

    @FXML private void onClose() {
        ((Stage)tableResults.getScene().getWindow()).close();
    }
}
