package com.cwdarmm.controller;

import com.cwdarmm.model.domain.CatMarket;
import com.cwdarmm.model.dto.OptimalContractRow;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Controlador de la ventana que acumula múltiples tablas de "Optimal Contracts".
 * Cada cálculo añade una nueva tabla al contenedor horizontal sin eliminar las
 * previas. Un botón de "Reset" permite limpiar manualmente el contenedor.
 */
@Component
@RequiredArgsConstructor
public class OptimalContractsContainerController {

    @FXML private HBox tablesBox;

    private final OptimalContractsPane paneFactory;

    /** Agrega una nueva tabla al contenedor. */
    public void addTable(List<OptimalContractRow> rows, String account, CatMarket market) {
        tablesBox.getChildren().add(paneFactory.create(rows, account, market));
    }

    /** Elimina todas las tablas acumuladas. */
    @FXML
    private void onReset() {
        tablesBox.getChildren().clear();
    }
}
