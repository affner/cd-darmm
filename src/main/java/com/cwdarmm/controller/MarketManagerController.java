package com.cwdarmm.controller;

import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.service.MarketDataService;
import com.cwdarmm.config.SpringFXMLLoader;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
public class MarketManagerController {
    private final MarketDataService marketDataService;
    private final SpringFXMLLoader springFXMLLoader;

    @FXML
    private TableView<MarketDTO> tableMarkets;

    @FXML private TableColumn<MarketDTO, Long> colId;
    @FXML
    private TableColumn<MarketDTO, String> colAccount;
    @FXML
    private TableColumn<MarketDTO, String> colMarket;
    @FXML
    private TableColumn<MarketDTO, String> colMarketData;
    @FXML
    private TableColumn<MarketDTO, BigDecimal> colSize;
    @FXML
    private TableColumn<MarketDTO, Double> colRiskA;
    @FXML
    private TableColumn<MarketDTO, Double> colRiskB;
    @FXML
    private TableColumn<MarketDTO, Double> colFinalHouse;
    @FXML
    private TableColumn<MarketDTO, Double> colFinalLunch;
    @FXML private TableColumn<MarketDTO, Void> colActions;


    private MarketDTO lastSaved;

    public MarketManagerController(MarketDataService marketDataService,
                                   SpringFXMLLoader springFXMLLoader) {
        this.marketDataService = marketDataService;
        this.springFXMLLoader = springFXMLLoader;
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAccount.setCellValueFactory(feat ->
                new ReadOnlyStringWrapper(feat.getValue().getAccount().getName()));
        colMarket.setCellValueFactory(feat ->
                new ReadOnlyStringWrapper(feat.getValue().getMarket().getName()));
        colMarketData.setCellValueFactory(feat ->
                new ReadOnlyStringWrapper(feat.getValue().getMarketData().getName()));
        colSize.setCellValueFactory(new PropertyValueFactory<>("accountSize"));
        colSize.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    // convierte el double a BigDecimal para toPlainString()
                    setText(value.toPlainString());
                }
            }
        });

        colRiskA.setCellValueFactory(new PropertyValueFactory<>("riskA"));
        colRiskB.setCellValueFactory(new PropertyValueFactory<>("riskB"));
        colFinalHouse.setCellValueFactory(new PropertyValueFactory<>("riskFinalHouse"));
        colFinalLunch.setCellValueFactory(new PropertyValueFactory<>("riskFinalLunch"));

        loadMarkets();

        tableMarkets.setRowFactory(tv -> {
            TableRow<MarketDTO> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (!row.isEmpty() && evt.getClickCount() == 2) {
                    MarketDTO clicked = row.getItem();
                    openRiskTableWindow(clicked);
                }
            });
            return row;
        });

        initActionsColumn();
    }

    private void loadMarkets() {
        List<MarketDTO> list = marketDataService.findAll();
        tableMarkets.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void onOpenMarket() {
        try {
            // 1) Prepara el diálogo
            FXMLLoader openLoader = springFXMLLoader.load("/fxml/OpenMarketSessionForm.fxml");
            Stage openStage = new Stage();
            openStage.initOwner(tableMarkets.getScene().getWindow());
            openStage.initModality(Modality.WINDOW_MODAL);     // WINDOW_MODAL en lugar de APPLICATION_MODAL
            openStage.setTitle("Open Market");
            openStage.setScene(new Scene(openLoader.getRoot()));

            // 2) Configura callback para guardar el DTO y recargar la tabla
            OpenMarketSessionController omc = openLoader.getController();
            omc.initForm(null);
            omc.setDialogStage(openStage);
            omc.setOnSave(() -> {
                this.lastSaved = omc.getLastSavedDTO();
                loadMarkets();
            });

            // 3) Muestra el diálogo y espera a que se cierre
            openStage.showAndWait();

            // 4) Una vez cerrado, abres la ventana de Risk **fuera** del callback
            if (lastSaved != null) {
                FXMLLoader riskLoader = springFXMLLoader.load("/fxml/MarketRiskDashboard.fxml");
                Stage riskStage = new Stage();
                riskStage.initOwner(tableMarkets.getScene().getWindow());
                riskStage.initModality(Modality.NONE);
                riskStage.setTitle(lastSaved.getMarket().getName() + " – Risk Manager");
                riskStage.setScene(new Scene(riskLoader.getRoot()));

                MarketRiskDashboardController rtc = riskLoader.getController();
                rtc.setContext(lastSaved);
                riskStage.show();

                // limpiamos la marca para la próxima vez
                lastSaved = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void openRiskTableWindow(MarketDTO context) {
        try {
            // 1) Carga el FXML de la vista de tabla de riesgo
            FXMLLoader riskLoader = springFXMLLoader.load("/fxml/MarketRiskDashboard.fxml");
            Stage riskStage = new Stage();
            riskStage.initOwner(tableMarkets.getScene().getWindow());
            riskStage.initModality(Modality.NONE);
            riskStage.setTitle(context.getMarket().getName() + " – Risk Manager");
            riskStage.setScene(new Scene(riskLoader.getRoot()));

            // 2) Pasa el contexto al controller de Risk Table
            MarketRiskDashboardController rtc = riskLoader.getController();
            rtc.setContext(context);

            // 3) Muestra la ventana
            riskStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initActionsColumn() {
        colActions.setCellFactory(new Callback<>() {
            @Override
            public TableCell<MarketDTO, Void> call(TableColumn<MarketDTO, Void> param) {
                return new TableCell<>() {
                    private final Button btnEdit   = new Button("✎");
                    private final Button btnDelete = new Button("✖");
                    private final HBox pane = new HBox(5, btnEdit, btnDelete);

                    {
                        btnEdit.setOnAction(e -> {
                            MarketDTO dto = getTableView().getItems().get(getIndex());
                            onEditMarket(dto);
                        });
                        btnDelete.setOnAction(e -> {
                            MarketDTO dto = getTableView().getItems().get(getIndex());
                            onDeleteMarket(dto);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : pane);
                    }
                };
            }
        });
    }

    // Método de edición (reusa tu flujo de onOpenMarket pero precargando existingDto)
    private void onEditMarket(MarketDTO dto) {
        try {
            FXMLLoader loader = springFXMLLoader.load("/fxml/OpenMarketSessionForm.fxml");
            Stage dialog = new Stage();
            dialog.initOwner(tableMarkets.getScene().getWindow());
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setTitle("Editar Market");
            dialog.setScene(new Scene(loader.getRoot()));

            OpenMarketSessionController omc = loader.getController();
            omc.setDialogStage(dialog);
            omc.initForm(dto);        // <-- precarga
            omc.setOnSave(() -> {
                lastSaved = omc.getLastSavedDTO();
                loadMarkets();
            });

            dialog.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Método de borrado con confirmación
    private void onDeleteMarket(MarketDTO dto) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar este market?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.orElse(ButtonType.NO) == ButtonType.YES) {
            marketDataService.delete(dto.getId());  // necesitas este método en tu servicio
            loadMarkets();
        }
    }

    /**
     * Permite acceder al DTO creado desde OpenMarketSessionController.
     */
    public MarketDTO getLastSavedDTO() {
        return lastSaved;
    }
}
