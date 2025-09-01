package com.cwdarmm.controller;

import com.cwdarmm.config.SpringFXMLLoader;
import com.cwdarmm.model.domain.CatMarket;
import com.cwdarmm.model.dto.OptimalContractRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.util.List;

/**
 * Fábrica de paneles de contratos óptimos. Cada invocación carga el FXML de
 * una tabla, asigna los datos y devuelve el nodo listo para insertarse en el
 * contenedor padre.
 */
@Component
@RequiredArgsConstructor
public class OptimalContractsPane {

    private final SpringFXMLLoader springFXMLLoader;

    public Node create(List<OptimalContractRow> rows, String account, CatMarket market) {
        try {
            FXMLLoader loader = springFXMLLoader.load("/fxml/OptimalContractsView.fxml");
            OptimalContractsController ctrl = loader.getController();
            ctrl.setCriteriaAccount(account);
            ctrl.setMarket(market);
            ctrl.setItems(rows);
            return loader.getRoot();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load OptimalContractsView.fxml", e);
        }
    }
}
