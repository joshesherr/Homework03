package org.example.homework03;

import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.awt.image.ColorConvertOp;

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
        double newXPos = (robotSpeed*x) + robot.getX();
        double newYPos = (robotSpeed*y) + robot.getY();

        // find the center of the robot image
        double roboCenterX = (robot.getImage().getWidth()/2.0);
        double roboCenterY = (robot.getImage().getHeight()/2.0);

        //keep pixel scan within the bounds of the image.
        double scanPosX = newXPos + roboCenterX + x*roboCenterX;
        double scanPosY = newYPos + roboCenterY + y*roboCenterY;
        if (scanPosX>maze.getImage().getWidth()) scanPosX = maze.getImage().getWidth(); else if (scanPosX<0) scanPosX = 0;
        if (scanPosY>maze.getImage().getHeight()) scanPosY = maze.getImage().getHeight(); else if (scanPosY<0) scanPosY = 0;

        //search the image at the scan location for a color.
        Color colorAtPos = maze.getImage().getPixelReader().getColor((int)(scanPosX),(int)(scanPosY));
        // if the color is white, then proceed to move
        if (colorAtPos.equals(Color.color(1.0,1.0,1.0,1.0))) {
            robot.setX(newXPos);
            robot.setY(newYPos);
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

    /*Todo Maybe add a button to activate the animation.
    */
    @FXML
    public void onMouseClick(MouseEvent e) {
        if (true) return; //ToDo remove this line. This is to stop the animation playing on accidental clicks.

        //set the initial start pos for the animation
        robot.setX(10.0);
        robot.setY(260.0);

        //Setting up the path
        Path path = new Path();
        path.getElements().add (new MoveTo(0f, 0f));
        path.getElements().add (new LineTo(100f, 0f));
        path.getElements().add (new LineTo(-100f, 0f));
        path.getElements().add (new LineTo(0f, -100f));

        //Instantiating PathTransition class
        javafx.animation.PathTransition pathTransition = new javafx.animation.PathTransition();

        //Setting duration for the PathTransition
        pathTransition.setDuration(Duration.millis(1000));

        //Setting Node on which the path transition will be applied
        pathTransition.setNode(robot);

        //setting path for the path transition
        pathTransition.setPath(path);

        //setting orientation for the path transition
        //pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);

        //setting up the cycle count
        pathTransition.setCycleCount(1);

        //setting auto reverse to be true
        pathTransition.setAutoReverse(true);

        //Playing path transition
        pathTransition.play();
    }

}
