package com.cwdarmm.controller;

import com.cwdarmm.config.SpringFXMLLoader;
import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.service.RiskService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    }

    @FXML
    private void onRiskManage() throws IOException {
        // carga el formulario de inputs en modal
        FXMLLoader loader = springFXMLLoader.load("/fxml/riskForm.fxml");
        Stage dialog = new Stage();
        dialog.initOwner(tableResults.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        // pasar contexto
        RiskFormController c = loader.getController();
        c.setDialogStage(dialog);
        c.setMarketContext(context);
        c.setOnCalculated(() -> {
            // refrescar tabla con nuevos resultados
            var res = riskService.calculate(c.buildRequest());
            tableResults.setItems(FXCollections.observableArrayList(res));
        });
        dialog.setScene(new Scene(loader.getRoot()));
        dialog.showAndWait();
    }

    @FXML private void onClose() {
        ((Stage)tableResults.getScene().getWindow()).close();
    }
}
