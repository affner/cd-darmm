package com.cwdarmm.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class MainApp extends Application {

    private ConfigurableApplicationContext springCtx;

    @Override
    public void init() {
        // Arranca Spring sin servidor web
        springCtx = new SpringApplicationBuilder()
                .sources(com.cwdarmm.config.SpringConfig.class) // raíz de configuración
                .run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carga la vista raíz
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_view.fxml"));
        // Permite que Spring cree/inyecte los controllers
        loader.setControllerFactory(springCtx::getBean);

        Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 800);               // resolución inicial
        scene.getStylesheets().add(getClass()
                .getResource("/styles/styles.css")
                .toExternalForm());

        primaryStage.setTitle("CW-DARMM Desktop");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    @Override
    public void stop() {
        springCtx.close();          // apaga Spring al cerrar la app
    }

    public static void main(String[] args) {
        launch(args);               // método estático de Application
    }
}
