package com.cwdarmm.service;

import com.cwdarmm.controller.OpenMarketController;
import com.cwdarmm.model.domain.Market;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FxWizardService {

    private final ApplicationContext ctx;

    /** Abre el wizard “Open Market” y devuelve un Market completo o null si cancelan. */
    public Market runOpenMarketWizard(Market base) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/open_market.fxml"));
        loader.setControllerFactory(ctx::getBean);
        Parent root = loader.load();

        OpenMarketController ctrl = loader.getController();
        ctrl.prefill(base);

        Stage st = new Stage();
        st.setTitle("Open Market Wizard");
        st.initModality(Modality.APPLICATION_MODAL);
        st.setScene(new javafx.scene.Scene(root));
        st.showAndWait();

        return ctrl.getResult();   // null si cancelado
    }
}
