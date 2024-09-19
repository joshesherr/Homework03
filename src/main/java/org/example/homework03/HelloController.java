package org.example.homework03;

import javafx.animation.PathTransition;
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
        Color colorAtPos = ( scanPosX<maze.getImage().getWidth() && scanPosY<maze.getImage().getHeight())?maze.getImage().getPixelReader().getColor((int)(scanPosX),(int)(scanPosY)):Color.BLACK;;
        // if the color is white or close to white, then proceed to move
        if (colorAtPos.getRed()>0.9 && colorAtPos.getGreen()>0.9 && colorAtPos.getBlue()>0.9 ) {
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

        //Instantiating PathTransition class
        javafx.animation.PathTransition pathTransition = new javafx.animation.PathTransition();

        //Setting duration for the PathTransition
        pathTransition.setDuration(Duration.millis(1000));

        //Setting Node on which the path transition will be applied
        pathTransition.setNode(robot);

        //setting path for the path transition
        pathTransition.setPath(findPath());

        //setting orientation for the path transition
        //pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);

        //setting up the cycle count
        pathTransition.setCycleCount(1);

        //setting auto reverse to be true
        pathTransition.setAutoReverse(true);

        //Playing path transition
        pathTransition.play();
    }

    private Path findPath() {
        //Setting up the path
        Path path = new Path();
        path.getElements().add (new MoveTo(0.0f, 0.0f));



        path.getElements().add (new LineTo(100f, 0f));
        path.getElements().add (new LineTo(-100f, 0f));
        path.getElements().add (new LineTo(0f, -100f));


        return path;
    }
}
