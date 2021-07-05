package org.example.mouse_aim;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.Vector2D;

public class Mouse_Aim extends Application {

    final float width = 600, height = 600;
    double angle;
    @Override
    public void start(Stage stage) throws Exception {

        Pane pane = new Pane();
        pane.setPrefSize(width, height);
        String path = getClass().getResource("res/up_arrow.png").toString();
        Image up_arrow = new Image(path);

        ImageView imageView = new ImageView(up_arrow);
        imageView.setLayoutX(width/2);
        imageView.setLayoutY(height/2);
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        pane.getChildren().add(imageView);

        Scene scene = new Scene(pane);
        scene.setOnMouseMoved(e->{
            Vector2D mouse = new Vector2D(e.getSceneX(), e.getSceneY());
            Vector2D player = new Vector2D(imageView.getLayoutX(), imageView.getLayoutY());

            Vector2D dir = Vector2D.subtract(player, mouse);
            System.out.println(dir.toString());
            angle = Math.atan2(dir.y,dir.x) * (180/Math.PI) - 90f;
            imageView.setRotate(angle);
        });
        scene.setOnMouseClicked(e->{
            //TODO: shooting here.

        });
        stage.setScene(scene);
        stage.setTitle("Mouse Aiming ");
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
