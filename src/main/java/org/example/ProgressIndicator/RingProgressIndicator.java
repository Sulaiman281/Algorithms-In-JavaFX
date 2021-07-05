package org.example.ProgressIndicator;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RingProgressIndicator extends Application {
    final float width = 600, height = 600;
    @Override
    public void start(Stage stage) throws Exception {
        Arc arc = new Arc();
        arc.setRadiusY(80);
        arc.setRadiusX(80);
        arc.setLayoutX(width/2);
        arc.setLayoutY(height/2);
        arc.setFill(Color.TRANSPARENT);
        arc.setStrokeWidth(30);
        arc.setStroke(Color.GREEN);
        arc.setStartAngle(85);
//        arc.setCenterX(100);
//        arc.setCenterY(100);
        arc.setLength(0);

        Label label = new Label();
        label.setFont(Font.font("Arial",16));
        label.setPrefSize(50,50);
        label.setLayoutX(width/2-10);
        label.setLayoutY(height/2-10);


        Timeline timeline = new Timeline();

        KeyFrame keyFrame = new KeyFrame(Duration.millis(50), e->{
            double length = arc.getLength();
            if(Math.abs(length) <= 360) {
                int per = (int) (length/360 * 100);
                label.setText(Math.abs(per)+"%");
                arc.setLength(length-1);
            } else
                timeline.stop();
        });
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(keyFrame);

        Pane pane = new Pane();
        pane.getChildren().add(arc);
        pane.getChildren().add(label);
        pane.setPrefSize(width,height);

        stage.setScene(new Scene(pane));
        stage.show();
        timeline.play();
    }

    public static void main(String[] args){
        launch(args);
    }
}
