package org.example.homework03;

import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.image.ColorConvertOp;
import java.io.File;
import java.net.MalformedURLException;

public class HelloController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView maze;

    @FXML
    private ImageView robot;
    private final static int robotSpeed = 8;

    private void moveRobot(int x, int y) {
        robot.setRotate( (y==0)?((x>0)?90:-90):(y>0)?180:0 ); //set rotation based off movement.

        //test move robot to new position.
        double newXPos = (robotSpeed*x) + robot.getLayoutX();
        double newYPos = (robotSpeed*y) + robot.getLayoutY();

        // find the center of the robot image
        double roboCenterX = (robot.getImage().getWidth()/2.0);
        double roboCenterY = (robot.getImage().getHeight()/2.0);

        //keep movement within the bounds of the image.
        if (newXPos>maze.getImage().getWidth()) newXPos = maze.getImage().getWidth();
        else if (newXPos<0) newXPos = 0;
        if (newYPos>maze.getImage().getHeight()) newYPos = maze.getImage().getHeight();
        else if (newYPos<0) newYPos = 0;

        //offset scan to center and outward into the movement direction
        double scanPosX = newXPos + roboCenterX + x*roboCenterX;
        double scanPosY = newYPos + roboCenterY + y*roboCenterY;

        //search the image at the scan location for a color. keep scan within bounds of image.
        if (isColorValid(getColorAtPosition((int) scanPosX, (int) scanPosY))) {
            robot.setLayoutX(newXPos);
            robot.setLayoutY(newYPos);
        }
    }

    @FXML
    public void onKeyPressed(KeyEvent e) {//Event passed from main stage key event
        switch (e.getCode()) {
            case UP: moveRobot(0,-1); break;
            case DOWN: moveRobot(0,1); break;
            case LEFT: moveRobot(-1,0); break;
            case RIGHT: moveRobot(1,0); break;
        }
    }

    FileChooser fileChooser = new FileChooser();

    @FXML
    public void onImageSelectClicked() throws MalformedURLException {
        File file = fileChooser.showOpenDialog(maze.getScene().getWindow());
        if (file != null) maze.setImage(new Image(file.toURI().toURL().toExternalForm()));
    }

    @FXML
    public void onStartAnimationClicked() {

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            if (robot.getLayoutX() < maze.getImage().getWidth() / 2.0 - 8) {
                if (isWallOnLeft()) {
                    if (isWallInFront()) {
                        robot.setRotate(robot.getRotate() + 90);
                        robotFowardDirection=(robotFowardDirection+1)%4;
                    } else {
                        moveRobotFoward();
                    }
                } else {
                    robot.setRotate(robot.getRotate() - 90);
                    robotFowardDirection=(robotFowardDirection-1)%4;
                    moveRobotFoward();
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

//        //Instantiating PathTransition class
//        javafx.animation.PathTransition pathTransition = new javafx.animation.PathTransition();
//
//        //Setting duration for the PathTransition
//        pathTransition.setDuration(Duration.millis(1000));
//
//        //Setting Node on which the path transition will be applied
//        pathTransition.setNode(robot);
//
//        //setting path for the path transition
//        pathTransition.setPath(findPath());
//
//        //setting orientation for the path transition
//        //pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
//
//        //setting up the cycle count
//        pathTransition.setCycleCount(1);
//
//        //setting auto reverse to be true
//        pathTransition.setAutoReverse(true);
//
//        //Playing path transition
//        pathTransition.play();
    }

    int robotFowardDirection=RIGHT;
    static final int UP=0, RIGHT=1, DOWN=2, LEFT=3;

    private void moveRobotFoward() {
        switch (robotFowardDirection) {
            case UP: robot.setLayoutY(robot.getLayoutY()-robotSpeed); break;
            case DOWN: robot.setLayoutY(robot.getLayoutY()+robotSpeed);break;
            case LEFT: robot.setLayoutX(robot.getLayoutX()-robotSpeed);break;
            case RIGHT: robot.setLayoutX(robot.getLayoutX()+robotSpeed);break;
        }
    }

//    private Path findPath() {
//        // Setting up the path
//        Path path = new Path();
//        robot.setLayoutX(0);
//        robot.setLayoutY(0);
//        path.getElements().add(new MoveTo(0.0f, (maze.getImage().getHeight() / 2.0)));
//
//        while (robot.getLayoutX() < maze.getImage().getWidth() - 8) {
//            // Find the center of the robot image
//
//
//            // Offset scan to center and outward into the movement direction
////            double scanPosX = robot.getLayoutX() + roboCenterX + robotSpeed * roboCenterX;
////            double scanPosY = robot.getLayoutY() + roboCenterY + 0 * roboCenterY;
////
////            // Search the image at the scan location for a color. Keep scan within bounds of image.
////            Color colorAtPos = (scanPosX < maze.getImage().getWidth() && scanPosY < maze.getImage().getHeight()) ? maze.getImage().getPixelReader().getColor((int) (scanPosX), (int) (scanPosY)) : Color.BLACK;
//
//            // If the color is white or close to white, then proceed to move
////            if (colorAtPos.getRed() > 0.9 && colorAtPos.getGreen() > 0.9 && colorAtPos.getBlue() > 0.9) {
////                double newX = robot.getLayoutX() + robotSpeed;
////                if (newX != robot.getLayoutX()) {
////                    path.getElements().add(new LineTo(newX, robot.getLayoutY()));
////                    robot.setLayoutX(newX);
////                }
////            } else {
////                double newY = robot.getLayoutY() + robotSpeed;
////                if (newY != robot.getLayoutY()) {
////                    path.getElements().add(new LineTo(robot.getLayoutX(), newY));
////                    robot.setLayoutY(newY);
////                }
////            }
//        }
//
//        return path;
//    }

    private boolean isWallOnLeft() {
        //test move robot to new position.
        double newXPos = robot.getLayoutX();
        double newYPos = robot.getLayoutY() + ((robotFowardDirection==RIGHT)?robotSpeed:((robotFowardDirection==LEFT)?-robotSpeed:0));;

        // find the center of the robot image
        double roboCenterX = (robot.getImage().getWidth()/2.0);
        double roboCenterY = (robot.getImage().getHeight()/2.0);

        //keep movement within the bounds of the image.
        if (newXPos>maze.getImage().getWidth()) newXPos = maze.getImage().getWidth();
        else if (newXPos<0) newXPos = 0;
        if (newYPos>maze.getImage().getHeight()) newYPos = maze.getImage().getHeight();
        else if (newYPos<0) newYPos = 0;

        //offset scan to center and outward into the movement direction
        double scanPosX = newXPos + roboCenterX;
        double scanPosY = newYPos + roboCenterY - roboCenterY;


        // Read the color at the scan position
        return !isColorValid(getColorAtPosition((int) scanPosX, (int) scanPosY));
    }

    private boolean isWallInFront() {
        //test move robot to new position.
        double newXPos = robot.getLayoutX() + ((robotFowardDirection==RIGHT)?robotSpeed:((robotFowardDirection==LEFT)?-robotSpeed:0));
        double newYPos = robot.getLayoutY();

        // find the center of the robot image
        double roboCenterX = (robot.getImage().getWidth()/2.0);
        double roboCenterY = (robot.getImage().getHeight()/2.0);

        //keep movement within the bounds of the image.
        if (newXPos>maze.getImage().getWidth()) newXPos = maze.getImage().getWidth();
        else if (newXPos<0) newXPos = 0;
        if (newYPos>maze.getImage().getHeight()) newYPos = maze.getImage().getHeight();
        else if (newYPos<0) newYPos = 0;

        //offset scan to center and outward into the movement direction
        double scanPosX = newXPos + roboCenterX + roboCenterX;
        double scanPosY = newYPos + roboCenterY;

        return !isColorValid(getColorAtPosition((int) scanPosX, (int) scanPosY));
    }


    public boolean isColorValid(Color colorAtPos) {
        return (colorAtPos.getRed()>0.9 && colorAtPos.getGreen()>0.9 && colorAtPos.getBlue()>0.9 );
    }
    private Color getColorAtPosition(double posX, double posY) {
        return (posX > 0 && posY > 0 && posX < maze.getImage().getWidth() && posY < maze.getImage().getHeight()) ? maze.getImage().getPixelReader().getColor((int) (posX), (int) (posY)) : Color.BLACK;
    }
}
