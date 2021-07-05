package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.rotations.RotationExample;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(new RotationExample().getRoot()));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}