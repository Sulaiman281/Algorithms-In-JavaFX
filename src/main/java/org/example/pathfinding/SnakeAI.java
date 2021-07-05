package org.example.pathfinding;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class SnakeAI extends Application {

    private final BorderPane borderPane = new BorderPane();
    private final Pane container = new Pane();

    class BottomBox extends HBox{
        Button play_btn = new Button("Play");
        Button pause_btn = new Button("Pause");
        Label score = new Label();

        BottomBox(){
            play_btn.setOnAction(e->{
                timeline.play();
            });
            pause_btn.setOnAction(e->{
                timeline.pause();
            });
            score.setText("Snake Size: "+snake.size());
            score.setFont(Font.font("Arial",24));
            setAlignment(Pos.CENTER);
            setSpacing(20);

            getChildren().addAll(play_btn,pause_btn,score);
        }
    }

    class Spot{
        int i;
        int j;
        double f,g, h;
        boolean object;

        Shape shape;
        ArrayList<Spot> neighbours = new ArrayList<>();
        Spot previous;

        Spot(int i, int j, Shape shape){
            this.i = i;
            this.j = j;
            this.f = 0;
            this.g = 0;
            this.h = 0;
            this.shape = shape;

            object = false;
            shape.setStroke(Color.rgb(50,50,50,.5));
            container.getChildren().add(shape);
        }

        void pattern(Paint _pattern){
            shape.setFill(_pattern);
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
//
//            if(i>0 && j>0)
//                this.neighbours.add(grid[i-1][j-1]);
//            if(i<col-1 && j<row-1)
//                this.neighbours.add(grid[i+1][j+1]);
//            if(i<col-1 && j>0)
//                this.neighbours.add(grid[i+1][j-1]);
//            if(i>0 && j<row-1)
//                this.neighbours.add(grid[i-1][j+1]);
        }
    }

    private final float width = 600;
    private final float height = 600;
    private final int col = 30, row = 30;
    private final float cell_w = width/col, cell_h = height/row;

    //background
    private Spot[][] grid = new Spot[col][row];
    private final Paint bg_color = Color.rgb(80,80,80);

    private final Paint face = Color.rgb(75, 245, 66);
    private final Paint body = Color.rgb(74, 115, 72);
    private final Paint tail = Color.rgb(125, 158, 123);

    //snake
    class Snake{
        int x,y;

        Snake(int i,int j){
            this.x = i;
            this.y = j;
        }
    }
    private LinkedList<Snake> snake = new LinkedList<>();

    // food
    private int foodX,foodY;
    private final Paint food_pattern = Color.rgb(255, 50, 50,1);

    // path finding.
    private Spot start, goal;
    private ArrayList<Spot> openSet = new ArrayList<>(),closeSet = new ArrayList<>();

    private ArrayList<Spot> path;

    private Timeline timeline = new Timeline();

    private BottomBox bottomBox;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        initiate();
        draw();
        loop();

        bottomBox = new BottomBox();
        borderPane.setTop(bottomBox);

        stage.setScene(new Scene(borderPane));
        stage.setResizable(false);
        stage.show();
    }

    private void initiate(){
        container.setPrefWidth(width);
        container.setPrefHeight(height);
        borderPane.setCenter(container);
        borderPane.setMaxHeight(Region.USE_COMPUTED_SIZE);

        initializeGrid();

        snake.add(new Snake(col/2,row/2));
        spawnFood();

        start = grid[snake.getFirst().x][snake.getFirst().y];
        goal = grid[foodX][foodY];

        openSet.add(start);
    }

    private void initializeGrid() {
        for (int i = 0; i< col; i++) {
            for (int j = 0; j<row;j++) {
                Rectangle rect = new Rectangle();
                rect.setWidth(cell_w);
                rect.setHeight(cell_h);
                rect.setLayoutX(i*cell_w);
                rect.setLayoutY(j*cell_h);
                grid[i][j] = new Spot(i,j,rect);
            }
        }
        for(int i = 0;i < col; i++){
            for(int j = 0; j<row; j++){
                grid[i][j].addNeighbours();
            }
        }
    }

    private void resetGrid(){
        for (int i = 0; i< col; i++) {
            for (int j = 0; j<row;j++) {
                grid[i][j].previous = null;
            }
        }
    }

    int index;
    private boolean shortest = true;
    private void draw(){
        if(isPathFound == 0) {
            shortest = snake.size() < col*2;
            findPath();
            index = path.size()-1;
        }else {
            if (!path.isEmpty()) {
                moveSnake(path.get(index));
                path.remove(index--);
                if (eatFood()) {
                    bottomBox.score.setText("Snake size: " + snake.size());
                    spawnFood();
                    snake.add(new Snake(prev.x, prev.y));
                    reset();
                }
            }
        }

        for (int i = 0; i< col; i++) {
            for (int j = 0; j<row;j++) {
                if(i == foodX && j == foodY){
                    grid[i][j].pattern(food_pattern);
                } else if(grid[i][j].object){
                    grid[i][j].pattern(Color.BROWN);
                }else {
                    grid[i][j].pattern(bg_color);
                }
            }
        }

        // snake draw..
        for(int i = 0; i< snake.size(); i++){
            if(i == 0) grid[snake.get(i).x][snake.get(i).y].pattern(face);
            else if(i == snake.size()-1){
                grid[snake.get(i).x][snake.get(i).y].pattern(tail);
            }  else{
                grid[snake.get(i).x][snake.get(i).y].pattern(body);
            }
        }
    }

    private void updateObjects(){
        for (int i = 0; i< col; i++) {
            for (int j = 0; j<row;j++) {
                grid[i][j].object = false;
            }
        }
        for(int i = 0; i< snake.size(); i++){
            if(i == 0) continue;
            else if(i == snake.size()-1){
                grid[snake.get(i).x][snake.get(i).y].object = true;
            }  else{
                grid[snake.get(i).x][snake.get(i).y].object = true;
            }
        }
    }

    private void spawnFood(){
        foodX = new Random().nextInt(col);
        foodY = new Random().nextInt(row);

        for (Snake s : snake) {
            if(s.x == foodX && s.y == foodY) spawnFood();
        }
    }

    private boolean eatFood(){
        return (foodX == snake.getFirst().x && foodY == snake.getFirst().y);
    }

    private void loop(){
        KeyFrame keyFrame = new KeyFrame(Duration.millis(100),e->{
            draw();
        });
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(keyFrame);
    }

    void reset(){
        isPathFound = 0;
        path = null;
        openSet.clear();
        closeSet.clear();
        resetGrid();
        start = grid[snake.getFirst().x][snake.getFirst().y];
        goal = grid[foodX][foodY];
        openSet.add(start);
        findPath();
        index = path.size()-1;
    }

    Snake prev = new Snake(0,0);
    private void moveSnake(Spot spot){
        // check if it's blocked find new path.
        boolean block = false;
        for(int i = 0; i< snake.size();i++){
            if(spot.i == snake.get(i).x && spot.j == snake.get(i).y){
                block = true;
                return;
            }
        }
        if(block){
            reset();
        }
        prev.x = snake.getFirst().x;
        prev.y = snake.getFirst().y;
        snake.getFirst().x = spot.i;
        snake.getFirst().y = spot.j;
        Snake prev2 = new Snake(prev.x,prev.y);
        for(int i = 1; i< snake.size(); i++){
            prev2.x = snake.get(i).x;
            prev2.y = snake.get(i).y;
            snake.get(i).x = prev.x;
            snake.get(i).y = prev.y;
            prev.x = prev2.x;
            prev.y = prev2.y;
        }
    }

    int isPathFound = 0;
    void findPath() {
        System.out.println("Finding the path....");
        updateObjects();
        path = new ArrayList<>();
        if(!openSet.isEmpty()){
            int winner = 0;
            for(int i = 0;i< openSet.size(); i++){
//                if(shortest) {
                    if (openSet.get(i).f < openSet.get(winner).f) {
                        winner = i;
                    }
//                }else{
//                    if (openSet.get(i).f > openSet.get(winner).f) {
//                        winner = i;
//                    }
//                }
                Spot current = openSet.get(winner);

                if(current == goal){
                    Spot temp = current;
                    path.clear();
                    path.add(temp);
                    while(temp.previous != null){
                        path.add(temp.previous);
                        temp = temp.previous;
                    }
                    isPathFound = 2;
                    System.out.println("Path Found");
                }

                openSet.remove(current);
                closeSet.add(current);
                // neighbours.
                for (Spot neighbour : current.neighbours) {
                    if(closeSet.contains(neighbour) || neighbour.object) continue;
                    double tempG = current.g + 1;
                    System.out.println("tempG: "+tempG+", "+current.g);
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
                    if(newPath){
                        System.out.println("Distance: "+heuristic(neighbour,goal));
                        neighbour.h = heuristic(neighbour, goal);
                        neighbour.f = neighbour.g + neighbour.h;
                        System.out.println(neighbour.f+" = "+neighbour.g+"+"+neighbour.h);
                        neighbour.previous = current;
                    }
                }
            }
        }else{
            reset();
            goal = grid[snake.getLast().x][snake.getLast().y];
            findPath();
//            System.out.println("changing the goal.");
            // stop finding the path no solution.
//            isPathFound = 1;
        }
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
}