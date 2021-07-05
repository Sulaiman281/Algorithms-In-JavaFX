package org.example.pathfinding;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Test1 extends Application {

    private Pane pane;

    private final float width = 600;
    private final float height = 600;

    private final int col = 40,row = 40;
    private Spot[][] grid;

    private ArrayList<Spot> openSet,closeSet;

    private final float w = width/col, h = height/row;

    private Spot start, goal;

    private ArrayList<Spot> path;

    Timeline timeline = new Timeline();
    boolean goalAchieved = false;

    @Override
    public void start(Stage stage) throws Exception {
        setup();
        draw();
        stage.setScene(new Scene(pane));
        stage.setResizable(false);
        stage.show();
    }

    private class Spot{
        int i, j;
        double f,g,hx;
        ArrayList<Spot> neighbours = new ArrayList<>();
        Rectangle rect;
        Spot previous;
        boolean wall;
        Spot(int x, int y){
            this.i = x;
            this.j = y;
            this.f = 0;
            this.g = 0;
            this.hx = 0;
            rect = new Rectangle();
            rect.setWidth(w-1);
            rect.setHeight(h-1);
            rect.setStroke(Color.rgb(0,0,0,1));
            rect.setLayoutX(i*w);
            rect.setLayoutY(j*h);

            pane.getChildren().add(rect);

            this.wall = false;
            int random = new Random().nextInt(15);
            if(random <3) this.wall = true;

            setEvent();
        }

        void show(Color color){
            rect.setFill(this.wall ? Color.BROWN : color);
        }

        void setEvent(){
            if(wall) return;
            rect.setOnMouseClicked(e->{
                goal = this;
                reset();
            });
        }

        void addNeighbours(){
            if(i > 0){
                this.neighbours.add(grid[i-1][j]);
            }
            if(j > 0)
                this.neighbours.add(grid[i][j-1]);
            if(j < row-1)
                this.neighbours.add(grid[i][j+1]);
            if(i < col -1)
                this.neighbours.add(grid[i+1][j]);

//            if(i>0 && j>0)
//                this.neighbours.add(grid[i-1][j-1]);
//            if(i<col-1 && j<row-1)
//                this.neighbours.add(grid[i+1][j+1]);
//            if(i<col-1 && j>0)
//                this.neighbours.add(grid[i+1][j-1]);
//            if(i>0 && j<row-1)
//                this.neighbours.add(grid[i-1][j+1]);
        }

        @Override
        public String toString() {
            return "Spot{" +
                    "i=" + i +
                    ", j=" + j +
                    ", f=" + f +
                    ", g=" + g +
                    ", hx=" + hx +
                    '}';
        }
    }

    private void setup(){
        pane = new Pane();
        pane.setPrefWidth(width);
        pane.setPrefHeight(height);

        grid = new Spot[col][row];

        openSet = new ArrayList<>();
        closeSet = new ArrayList<>();
        path = new ArrayList<>();

        for(int i = 0;i < col; i++){
            for(int j = 0; j<row; j++){
                grid[i][j] = new Spot(i,j);
            }
        }

        for(int i = 0;i < col; i++){
            for(int j = 0; j<row; j++){
                grid[i][j].addNeighbours();
            }
        }

        start = grid[0][0];
        goal = grid[col-1][row-1];
        start.wall = false;
        goal.wall = false;

        openSet.add(start);
    }

    public void draw(){
        AtomicInteger count = new AtomicInteger();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(1),e->{
            System.out.println("Cycle: "+count.getAndIncrement());
            display();
        });
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    private void display() {
        Spot temp = null;
        if(!openSet.isEmpty()){
            int winner = 0;
            for(int i = 0;i< openSet.size(); i++){
                if(openSet.get(i).f < openSet.get(winner).f){
                    winner = i;
                }
                Spot current = openSet.get(winner);
                temp = current;
                if(current == goal){
                    path.clear();
                    path.add(temp);
                    while (temp.previous != null) {
                        path.add(temp.previous);
                        temp = temp.previous;
                    }
                    goalAchieved = true;
//                    System.out.println("done! blocks: "+path.size());
                }
                openSet.remove(current);
                closeSet.add(current);

                if(!goalAchieved)
                    for (Spot neighbour : current.neighbours) {
                        if(closeSet.contains(neighbour) || neighbour.wall) continue;
                        double tempG = current.g +1; // add the difference of their distance in my case it's 1.
                        boolean newPath = false;
                        if(openSet.contains(neighbour)){
                            if(tempG < neighbour.g){
                                neighbour.g = tempG;
                                newPath = true;
                            }
                        }else{
                            neighbour.g = tempG;
                            openSet.add(neighbour);
                            newPath = true;
                        }
                        if(newPath) {
                            neighbour.hx = heuristic(neighbour, goal);
                            neighbour.f = neighbour.g + neighbour.hx;
                            neighbour.previous = current;
                        }
                    }
            }
            // we can keep going
        }else {
            timeline.stop();
            System.out.println("unable to find the path.");
            // no solution.
        }
        for (Spot[] spots : grid) {
            for (Spot spot : spots) {
                spot.show(Color.WHITE);
            }
        }
        for (Spot spot : openSet) {
            spot.show(Color.BLUE);
        }
        for (Spot spot : closeSet) {
            spot.show(Color.PINK);
        }
        for (Spot spot : path) {
            spot.show(Color.GREEN);
        }
        temp.show(Color.BLUEVIOLET);
        start.show(Color.LIGHTGREEN);
        goal.show(Color.YELLOW);
        if(goalAchieved) timeline.stop();
    }

    private void reset(){
        goalAchieved = false;
        openSet.clear();
        closeSet.clear();
        path.clear();
        start = grid[0][0];
        openSet.add(start);
        timeline.play();
    }

    private double heuristic(Spot a, Spot b){
        return dist(a.i,a.j,b.i,b.j);
//        return Math.abs(a.i-b.i) + Math.abs(a.j-b.j);
    }

    private double dist(float x1,float y1, float x2, float y2){
        float x = (x2-x1)*(x2-x1);
        float y = (y2-y1)*(y2-y1);
        return Math.sqrt(x+y);
    }

    public static void main(String[] args){
        launch(args);
    }
}