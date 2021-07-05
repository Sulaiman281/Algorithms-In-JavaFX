package org.example.rotations;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

public class RotationExample {

    final int width = 600, height = 400;

    private Pane root;

    Rectangle rect;

    float angle = 0.0f;
    float aVelocity = 0.0f;
    float aAcceleration = 1f;

    public RotationExample(){
        initialize();
    }

    void initialize(){
        root = new Pane();
        root.setPrefWidth(width);
        root.setPrefHeight(height);

        rect = new Rectangle();
        rect.setWidth(40);
        rect.setHeight(80);
        rect.setLayoutX((double)width/2);
        rect.setLayoutY((double)height/2);
        rect.setFill(Color.rgb(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255)));

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1),e->{

            angle += aVelocity;
            aVelocity += aAcceleration;

            System.out.println("Before: "+rect.getRotate());
            rect.setRotate(angle);
            System.out.println("After: "+rect.getRotate()+"\n");
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();

        root.getChildren().add(rect);
    }

    public Pane getRoot() {
        return root;
    }
}
