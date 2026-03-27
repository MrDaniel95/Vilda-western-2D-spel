package org.example.litetspel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.litetspel.engine.GamePane;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        GamePane gamePane = new GamePane();

        Scene scene = new Scene(gamePane, 800, 600);

        stage.setTitle("Wild West Game");
        stage.setScene(scene);
        stage.show();

        gamePane.requestFocus();
    }

    public static void main(String[] args) {
        launch();
    }
}
