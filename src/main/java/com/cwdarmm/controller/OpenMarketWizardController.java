package com.cwdarmm.controller;

import com.cwdarmm.model.domain.Market;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

/**
 * Asistente sencillo (1-pantalla) que confirma los datos básicos del mercado.
 *  └─ Devuelve la misma instancia Market, porque los parámetros de riesgo se
 *     capturarán luego en RiskManager. Si en el futuro quisieras incluir
 *     “cuenta” y “% riesgo base” aquí, bastaría extender este controller y
 *     devolver un DTO distinto.
 */
@Controller
@RequiredArgsConstructor
public class OpenMarketWizardController {

    /* UI (definido en open_market_wizard.fxml) */
    @FXML private TextField txtName;
    @FXML private TextField txtSymbol;
    @FXML private TextField txtTickSize;
    @FXML private TextField txtTickValue;
    @FXML private ComboBox<Integer> cmbDefaultStop;
    @FXML private TextField txtColorHex;

    /* -------- internal -------- */
    private Market baseMarket;
    @Getter
    private Market result;          // será != null sólo si el usuario pulsa OK

    /* Prefill llamado por FxWizardService */
    public void prefill(Market base) {
        this.baseMarket = base;
        txtName.setText(base.getName());
        txtSymbol.setText(base.getSymbol());
        txtTickSize.setText(String.valueOf(base.getTickSize()));
        txtTickValue.setText(String.valueOf(base.getTickValue()));
        cmbDefaultStop.getItems().addAll(20, 40, 60, 80, 100);
        cmbDefaultStop.setValue(base.getDefaultStop());
        txtColorHex.setText(base.getColorHex());
    }

    @FXML
    private void onOk() {
        // Clona los datos (permite al usuario editarlos si quiere)
        result = new Market(
                txtName.getText(),
                txtSymbol.getText(),
                Double.parseDouble(txtTickSize.getText()),
                Double.parseDouble(txtTickValue.getText()),
                cmbDefaultStop.getValue(),
                txtColorHex.getText()
        );
        closeStage();
    }

    @FXML
    private void onCancel() {
        result = null;      // explícito
        closeStage();
    }

    private void closeStage() {
        Stage st = (Stage) txtName.getScene().getWindow();
        st.close();
    }
}
