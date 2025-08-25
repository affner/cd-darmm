package com.cwdarmm.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

/**
 * Controller for the About dialog that displays application version information.
 */
@Component
public class AboutDialogController {

    @FXML
    private Label lblVersion;

    @FXML
    public void initialize() {
        String version = getClass().getPackage().getImplementationVersion();
        if (version == null) {
            version = "1.0.0";
        }
        lblVersion.setText("CW-DARMM version " + version);
    }
}

