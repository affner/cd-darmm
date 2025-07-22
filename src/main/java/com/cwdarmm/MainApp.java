package com.cwdarmm;

import com.cwdarmm.config.SpringFXMLLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MainApp extends Application {
    private ConfigurableApplicationContext context;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        // Inicializa el contexto de Spring antes de mostrar UI
        context = new SpringApplicationBuilder(MainApp.class).run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Usa nuestro SpringFXMLLoader para inyectar controladores
        SpringFXMLLoader loader = context.getBean(SpringFXMLLoader.class);
        FXMLLoader fxmlLoader = loader.load("/fxml/MainWindow.fxml");
        Parent root = fxmlLoader.getRoot();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        primaryStage.setTitle("CW-DARMM - Market Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Cierra Spring al salir
        context.close();
    }
}
