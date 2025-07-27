package com.cwdarmm.controller;

/**
 * Ventana emergente que muestra los contratos óptimos
 * calculados para un trade.
 */

import com.cwdarmm.model.domain.CatMarket;
import com.cwdarmm.model.dto.OptimalContractRow;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OptimalContractsController {

    @FXML private Label lblAccount;
    @FXML private TableView<OptimalContractRow> tblOptimal;
    @FXML private TableColumn<OptimalContractRow,Integer> colSlSize;
    @FXML private TableColumn<OptimalContractRow,String>  colTicker;
    @FXML private TableColumn<OptimalContractRow, BigDecimal> colOptimal;
    @FXML private TableColumn<OptimalContractRow,Integer> colTarget;

    private CatMarket market;

    @FXML
    public void initialize() {
        colSlSize .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getSlSize()));
        colTicker .setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getFuturesTicker()));
        colOptimal.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getOptimalContract()));
        colTarget .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTargetTicks()));

        // --- Cell styling to mimic the XLSM colors ---
        colSlSize.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item==null) { setText(null); setStyle(""); }
                else {
                    setText(item.toString());
                    setStyle("-fx-background-color:#FF0000; -fx-text-fill:white;");
                }
            }
        });

        colTarget.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item==null) { setText(null); setStyle(""); }
                else {
                    setText(item.toString());
                    setStyle("-fx-background-color:#D3D3D3;");
                }
            }
        });

        Callback<TableColumn<OptimalContractRow, ?>, TableCell<OptimalContractRow, ?>> colorFactory = col -> new TableCell<>() {
            @Override protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item==null) { setText(null); setStyle(""); }
                else {
                    setText(item.toString());
                    String c = colorForTicker(getTableView().getItems().get(getIndex()).getFuturesTicker());
                    if (c != null && !c.isBlank()) setStyle("-fx-background-color:"+c+";");
                    else setStyle("");
                }
            }
        };

        colTicker.setCellFactory(colorFactory);
        colOptimal.setCellFactory(colorFactory);
    }

    private String colorForTicker(String ticker) {
        if (market == null || ticker == null) return null;
        boolean isMicro = ticker.startsWith("M");
        String color = isMicro ? market.getColor1() : market.getColor2();
        return color;
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
