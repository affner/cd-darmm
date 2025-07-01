package com.cwdarmm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class MainApp extends Application {

    private ConfigurableApplicationContext ctx;

    @Override
    public void init() {
        ctx = new SpringApplicationBuilder(SpringConfig.class).run();
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_view.fxml"));
        loader.setControllerFactory(ctx::getBean);   // imprescindible
        Parent root = loader.load();                    // se carga UNA sola vez
        Scene scene = new Scene(root);                  // usa ese root
        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        primaryStage.setTitle("CW-DARMM Desktop");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(false);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/img/icon.png")));
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.centerOnScreen();

        primaryStage.show();
    }

    @Override
    public void stop() {
        ctx.close();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
