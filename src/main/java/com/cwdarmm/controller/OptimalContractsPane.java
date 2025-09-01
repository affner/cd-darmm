package com.cwdarmm.controller;

import com.cwdarmm.model.domain.CatMarket;
import com.cwdarmm.model.dto.OptimalContractRow;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.util.List;

/**
 * Fábrica de TableView para mostrar tablas de Optimal Contracts.
 * Cada invocación genera una tabla nueva lista para agregarse a un contenedor.
 */
public class OptimalContractsPane {

    public static TableView<OptimalContractRow> build(List<OptimalContractRow> items, CatMarket market) {
        TableView<OptimalContractRow> tbl = new TableView<>();
        tbl.setItems(FXCollections.observableArrayList(items));

        TableColumn<OptimalContractRow, Integer> colSlSize = new TableColumn<>("%optimal.contracts.slsize");
        TableColumn<OptimalContractRow, String>  colTicker = new TableColumn<>("%optimal.contracts.ticker");
        TableColumn<OptimalContractRow, BigDecimal> colOptimal = new TableColumn<>("%optimal.contracts.optimal");
        TableColumn<OptimalContractRow, Integer> colTarget = new TableColumn<>("%optimal.contracts.target");

        colSlSize.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getSlSize()));
        colTicker.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getFuturesTicker()));
        colOptimal.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getOptimalContract()));
        colTarget.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTargetTicks()));

        colSlSize.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item.toString());
                    setStyle("-fx-background-color:#FF0000; -fx-text-fill:white;");
                }
            }
        });

        colTarget.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item.toString());
                    setStyle("-fx-background-color:#D3D3D3;");
                }
            }
        });

        Callback<TableColumn<OptimalContractRow, ?>, TableCell<OptimalContractRow, ?>> colorFactory = column -> new TableCell<>() {
            @Override protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item.toString());
                    OptimalContractRow row = getTableView().getItems().get(getIndex());
                    String c = colorForTicker(market, row.getFuturesTicker());
                    if (c != null && !c.isBlank()) {
                        setStyle("-fx-background-color:" + c + ";");
                    } else {
                        setStyle("");
                    }
                }
            }
        };

        colTicker.setCellFactory(colorFactory);
        colOptimal.setCellFactory(colorFactory);

        tbl.getColumns().addAll(colSlSize, colTicker, colOptimal, colTarget);
        tbl.setPrefHeight(Region.USE_COMPUTED_SIZE);
        tbl.setPrefWidth(Region.USE_COMPUTED_SIZE);
        return tbl;
    }

    private static String colorForTicker(CatMarket market, String ticker) {
        if (market == null || ticker == null) return null;
        boolean isMicro = ticker.startsWith("M");
        return isMicro ? market.getColor1() : market.getColor2();
    }
}
