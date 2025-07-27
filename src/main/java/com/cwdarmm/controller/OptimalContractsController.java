package com.cwdarmm.controller;

import com.cwdarmm.model.domain.CatMarket;
import com.cwdarmm.model.dto.OptimalContractRow;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OptimalContractsController {

    @FXML private Label                         lblAccount;
    @FXML private TableView<OptimalContractRow> tblOptimal;
    @FXML private TableColumn<OptimalContractRow, Integer>    colSlSize;
    @FXML private TableColumn<OptimalContractRow, String>     colTicker;
    @FXML private TableColumn<OptimalContractRow, BigDecimal> colOptimal;
    @FXML private TableColumn<OptimalContractRow, Integer>    colTarget;

    private CatMarket market;

    @FXML
    public void initialize() {
        // CellValueFactories
        colSlSize .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getSlSize()));
        colTicker .setCellValueFactory(c -> new ReadOnlyStringWrapper(    c.getValue().getFuturesTicker()));
        colOptimal.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getOptimalContract()));
        colTarget .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTargetTicks()));

        // Estilos fijos
        colSlSize.setCellFactory(col -> new TableCell<OptimalContractRow, Integer>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    setStyle("-fx-background-color:#FF0000; -fx-text-fill:white;");
                }
            }
        });

        colTarget.setCellFactory(col -> new TableCell<OptimalContractRow, Integer>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    setStyle("-fx-background-color:#D3D3D3;");
                }
            }
        });

        // Usamos el factory genérico para ticker y optimal
        colTicker .setCellFactory(colorFactory());
        colOptimal.setCellFactory(colorFactory());
    }

    /**
     * Método genérico que crea un Callback para colorear cualquier TableColumn<TipoFila,T>
     * basándose en el ticker de la fila.
     */
    private <T> Callback<TableColumn<OptimalContractRow, T>, TableCell<OptimalContractRow, T>> colorFactory() {
        return column -> new TableCell<OptimalContractRow, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    OptimalContractRow row = getTableView().getItems().get(getIndex());
                    String c = colorForTicker(row.getFuturesTicker());
                    if (c != null && !c.isBlank()) {
                        setStyle("-fx-background-color:" + c + ";");
                    } else {
                        setStyle("");
                    }
                }
            }
        };
    }

    private String colorForTicker(String ticker) {
        if (market == null || ticker == null) return null;
        boolean isMicro = ticker.startsWith("M");
        return isMicro ? market.getColor1() : market.getColor2();
    }

    public void setMarket(CatMarket market) {
        this.market = market;
    }

    public void setCriteriaAccount(String acc) {
        lblAccount.setText(acc);
    }

    public void setItems(List<OptimalContractRow> items) {
        tblOptimal.setItems(FXCollections.observableArrayList(items));
    }
}
