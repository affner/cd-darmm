package com.cwdarmm.util;

import javafx.stage.Stage;
import lombok.Setter;

/** Permite inyectar el Stage principal cuando se crea MainController. */
public class StageProvider {
    @Setter
    private Stage primaryStage;

    public Stage get() { return primaryStage; }
}
