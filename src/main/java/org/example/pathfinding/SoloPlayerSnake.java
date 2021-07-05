package org.example.pathfinding;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

public class SoloPlayerSnake extends Application {

    private Pane container = new Pane();
    private final Paint bg_color = Color.rgb(80,80,80);

    enum Direction{
        RIGHT,
        LEFT,
        UP,
        DOWN,
        STOP
    }
    Direction dir = Direction.STOP;

    class Node {
        int i,j;
        double f,g,h;
        Node previous;
        boolean walkable;
        Shape shape;
        Node(int i, int j){
            this.i = i;
            this.j = j;
            f = g = h = 0;
            walkable = true;
            previous = null;
            shape = null;
        }
        void pattern(Paint paint){
            shape.setFill(paint);
        }
        void shape(Shape shape){
            this.shape = shape;
            shape.setLayoutX(i*w_cell);
            shape.setLayoutY(j*h_cell);
            shape.setStroke(Color.rgb(10,10,10,1));
            // add into the container.
            container.getChildren().add(shape);
        }
    }

    private final float width = 500, height = 500;
    private final int col = 20, row = 20;
    private final float w_cell = width/col, h_cell = height/row;

    private Node[][] grid = new Node[col][row];

    private LinkedList<Node> snake = new LinkedList<>();


    private ArrayList<Node> openSet = new ArrayList<>(), closeSet = new ArrayList<>();
    private Stack<Node> path = new Stack<>();

    private Node goal,start;

    private int foodX,foodY;
    private boolean shortest;

    private void initialize(){
        shortest = true;
        for(int i = 0; i < col; i++){
            for(int j = 0; j<row;j++){
                Rectangle rect = new Rectangle();
                rect.setWidth(w_cell);
                rect.setHeight(h_cell);
                grid[i][j] = new Node(i,j);
                grid[i][j].shape(rect);
                grid[i][j].pattern(bg_color);
            }
        }
        Node head = new Node(col/2,row/2);
        snake.add(0,head);

        spawnFood();
        setGoals();
    }

    private void setGoals() {
        goal = grid[foodX][foodY];
        start = grid[snake.get(0).i][snake.get(0).j];
        openSet.add(start);
//        findPath();
    }

    void spawnFood(){
        foodX = new Random().nextInt(col);
        foodY = new Random().nextInt(row);
    }

    boolean eatFood(){
        return snake.getFirst().i == foodX && snake.getFirst().j == foodY;
    }

    void reset(){
        openSet.clear();
        closeSet.clear();
        path.clear();
    }

    Node prev = new Node(0,0);
    private void moveSnake(){
        prev.i = snake.getFirst().i;
        prev.j = snake.getFirst().j;

        switch (dir){
            case UP:
                if(snake.getFirst().j > 0) snake.getFirst().j = snake.getFirst().j-1;
                break;
            case DOWN:
                if(snake.getFirst().j < row-1) snake.getFirst().j = snake.getFirst().j+1;
                break;
            case LEFT:
                if(snake.getFirst().i > 0) snake.getFirst().i = snake.getFirst().i-1;
                break;
            case RIGHT:
                if(snake.getFirst().i < col-1) snake.getFirst().i = snake.getFirst().i+1;
                break;
        }
//        System.out.println(path.size()+" path size.");
//        if(!path.isEmpty()){
//            Node s = path.pop();
//            snake.getFirst().i = s.i;
//            snake.getFirst().j = s.j;
//        }

        Node prev2 = new Node(prev.i, prev.j);
        int index = 0;
        for (Node node : snake) {
            if(index++ == 0) continue;
            prev2.i = node.i;
            prev2.j = node.j;
            node.i = prev.i;
            node.j = prev.j;
            prev.i = prev2.i;
            prev.j = prev2.j;
        }
    }

    void draw(){
        for(int i = 0; i< col;i++){
            for(int j = 0; j<row;j++){
                if(foodY == j && foodX == i)
                    grid[i][j].pattern(Color.RED);
                else if(!grid[i][j].walkable)
                    grid[i][j].pattern(Color.BROWN);
                else
                    grid[i][j].pattern(bg_color);
            }
        }
        for(int i = 0;i<snake.size();i++){
            grid[snake.get(i).i][snake.get(i).j].pattern(Color.LIGHTGREEN);
        }
    }
//    void clearWalkable(){
//        for(int i = 0; i< col;i++){
//            for(int j = 0; j<row;j++){
//                grid[i][j].walkable = true;
//            }
//        }
//        for(int i = 0;i<snake.size();i++){
//            grid[snake.get(i).i][snake.get(i).j].walkable = false;
//        }
//    }
//    private void findPath(){
//        clearWalkable();
//        System.out.println(openSet.size()+" open set");
//        if(!openSet.isEmpty()){
//            int winner = 0;
//            for(int i = 0; i<openSet.size(); i++){
//                if(shortest) {
//                    if (openSet.get(i).f < openSet.get(winner).f) {
//                        winner = i;
//                    }
//                }else{
//                    if (openSet.get(i).f > openSet.get(winner).f) {
//                        winner = i;
//                    }
//                }
//
//                Node current = openSet.get(winner);
//                openSet.remove(current);
//                closeSet.add(current);
//
//                //check if the current is winning node.
//                if(current == goal){
//                    Node temp = current;
//                    path.add(temp);
//                    while(temp.previous != null){
//                        path.add(temp.previous);
//                        temp = temp.previous;
//                    }
//                    return;
//                }
//
//                // check the neighbours
//                Node neighbour = null;
//                for(int x = 0; x< 4;x++){
//                    if(x == 0) {
//                        if (current.j > 0) {
//                            neighbour = grid[current.i][current.j - 1];
//                        }
//                    }else if(x == 1) {
//                        if (current.j < row - 1) {
//                            neighbour = grid[current.i][current.j + 1];
//                        }
//                    }else if(x == 2) {
//                        if (current.i > 0) {
//                            neighbour = grid[current.i - 1][current.j];
//                        }
//                    } else {
//                        if(current.i < col-1){
//                            neighbour = grid[current.i+1][current.j];
//                        }
//                    }
//                    if(closeSet.contains(neighbour) || !neighbour.walkable) continue;
//                    double tempG = current.g +1;
//                    boolean newPath = false;
//                    if(openSet.contains(neighbour)){
//                        if(tempG < neighbour.g){
//                            neighbour.g = tempG;
//                            newPath = true;
//                        }
//                    }else{
//                        neighbour.g = tempG;
//                        openSet.add(neighbour);
//                        newPath = true;
//                    }
//                    if(newPath){
//                        neighbour.h = heuristic(neighbour, goal);
//                        neighbour.f = neighbour.g + neighbour.h;
//                        neighbour.previous = current;
//                    }
//                }
//            }
//        }
//    }

//    private double heuristic(Node a, Node b){
//        return dist(a.i,a.j,b.i,b.j);
//        return Math.abs(a.i-b.i) + Math.abs(a.j-b.j);
//    }
//
//    private double dist(float x1,float y1, float x2, float y2){
//        float x = (x2-x1)*(x2-x1);
//        float y = (y2-y1)*(y2-y1);
//        return Math.sqrt(x+y);
//    }

    @Override
    public void start(Stage stage) throws Exception {
        container.setPrefWidth(width);
        container.setPrefHeight(height);
        initialize();
        Scene scene = new Scene(container);
        scene.setOnKeyPressed(e->{
            switch (e.getCode()){
                case UP:
                    if(dir == Direction.DOWN) return;
                    dir = Direction.UP;
                    break;
                case DOWN:
                    if(dir == Direction.UP) return;
                    dir = Direction.DOWN;
                    break;
                case LEFT:
                    if(dir == Direction.RIGHT) return;
                    dir = Direction.LEFT;
                    break;
                case RIGHT:
                    if(dir == Direction.LEFT) return;
                    dir = Direction.RIGHT;
                    break;
            }
        });
        stage.setScene(scene);
        stage.setResizable(false);
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(200),e->{
            moveSnake();
            draw();
            if(eatFood()){
                spawnFood();
                reset();
                setGoals();
                snake.add(new Node(0,0));
                stage.setTitle("Score: "+(snake.size()-1));
            }
        });
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
