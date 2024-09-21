package org.example.homework03;

import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.net.MalformedURLException;

public class HelloController {

    private boolean activeRobotActor = true;

    @FXML
    private Text inFrontTxt;
    @FXML
    private Text onLeftTxt;
    @FXML
    private Text direction;

    @FXML
    private ToggleButton startAuto;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView maze;

    //Car code begins here
    @FXML
    private HBox carBox;

    @FXML
    private Group carGroup;

    @FXML
    private Rectangle carTop;

    @FXML
    private Rectangle carbody;

    @FXML
    private Button swapCarBtn;

    @FXML
    private Circle wheel1;

    @FXML
    private Circle wheel2;
  
    @FXML
    private Text initialPosition;

    @FXML
    void swapWithRobot(ActionEvent event) {
        anchorPane.getChildren().remove(robot);
        carBox.getChildren().remove(carGroup);

        // Store the robot's position
        double robotX = robot.getLayoutX();
        double robotY = robot.getLayoutY();

        // Store the carGroup's position (Group needs translate for XY coords?)
        double carX = carGroup.getLayoutX();
        double carY = carGroup.getLayoutY();

        // Find the index of the button in the HBox
        int buttonIndex = carBox.getChildren().indexOf(swapCarBtn);

        // Remove the nodes from their parent containers (Robot in AnchorPane, Car in Hbox)
        if (activeRobotActor) { //Validation to stop crashing, can implement else to swap backwards here
            activeRobotActor = false;

            // Validation to add the robot before the button in the HBox
            if (buttonIndex != -1) {
                carBox.getChildren().add(buttonIndex, robot);
            } else {
                // If button is not found, just add robot at the end
                carBox.getChildren().add(robot);
            }

            // Add carGroup to the AnchorPane
            anchorPane.getChildren().add(carGroup);

            // Update positions
            robot.setLayoutX(carX);
            robot.setLayoutY(carY);
            carGroup.setLayoutX(robotX);
            carGroup.setLayoutY(robotY + 5); // Adjust to center in the maze hallway

            System.out.println("Swapped robot and carGroup between AnchorPane and HBox");
        }
        else {
            activeRobotActor = true;
            // Validation to add the Car before the button in the HBox
            if (buttonIndex != -1) {
                carBox.getChildren().add(buttonIndex, carGroup);
            } else {
                // If button is not found, just add Car at the end
                carBox.getChildren().add(carGroup);
            }
            // Add Robot back to the AnchorPane
            anchorPane.getChildren().add(robot);

            // Update positions
            carGroup.setLayoutX(robotX);
            carGroup.setLayoutY(robotY);
            robot.setLayoutX(carX);
            robot.setLayoutY(carY-5);
        }
    }

    @FXML
    void spinWheels(MouseEvent event) {
        System.out.println("Button hovered"); //Removable debug print

        //Rotation for wheel 1
        RotateTransition spinWheel1 = new RotateTransition(Duration.seconds(.8), wheel1);
        spinWheel1.setByAngle(360);                   // Rotate by 360 degrees
        spinWheel1.setCycleCount(RotateTransition.INDEFINITE);  // Rotate infinitely

        //Rotation for wheel 2
        RotateTransition spinWheel2 = new RotateTransition(Duration.seconds(.8), wheel2);
        spinWheel2.setByAngle(360);                   // Rotate by 360 degrees
        spinWheel2.setCycleCount(RotateTransition.INDEFINITE);  // Rotate infinitely

        // Start the rotation
        spinWheel1.play();
        spinWheel2.play();

        // Stop rotation on mouse removal from button hovering
        swapCarBtn.setOnMouseExited(e -> {
            System.out.println("Mouse exited, stopping wheels"); //Removable debug print
            spinWheel1.stop();
            spinWheel2.stop();
        });
    }




    /////////////////// Line break, Car code ends here
    static final int UP=0, RIGHT=1, DOWN=2, LEFT=3;
    @FXML
    private ImageView robot;
    private final static int robotSpeed = 8;
    int robotFowardDirection=RIGHT;

    // Method to initialize the robot's position
    private void initializeRobotPosition() {
        // Add mouse event handlers to the maze ImageView
        maze.setOnMousePressed(this::onMousePressed);
        maze.setOnMouseDragged(this::onMouseDragged);
        maze.setOnMouseReleased(this::onMouseReleased);
    }

    @FXML
    private void onMousePressed(MouseEvent event) {
        // Set the initial position of the robot when the mouse is pressed
        setRobotPosition(event.getX(), event.getY());
    }

    @FXML
    private void onMouseDragged(MouseEvent event) {
        // Update the robot's position as the mouse is dragged
        setRobotPosition(event.getX(), event.getY());
    }

    @FXML
    private void onMouseReleased(MouseEvent event) {
        // Finalize the robot's position when the mouse is released
        setRobotPosition(event.getX(), event.getY());
    }

    private void setRobotPosition(double x, double y) {
        // Ensure the robot's position is within the bounds of the maze
        double newX = Math.max(0, Math.min(x - robot.getImage().getWidth() / 2.0, maze.getImage().getWidth() - robot.getImage().getWidth()));
        double newY = Math.max(0, Math.min(y - robot.getImage().getHeight() / 2.0, maze.getImage().getHeight() - robot.getImage().getHeight()));
        robot.setLayoutX(newX);
        robot.setLayoutY(newY);
    }

    private void moveRobot() {
        int x = 0;
        int y = 0;
        switch (robotFowardDirection) {
            case UP:
                y = -1;
                break;
            case DOWN:
                y = 1;
                break;
            case LEFT:
                x = -1;
                break;
            case RIGHT:
                x = 1;
                break;
        }
//        if (activeRobotActor) { //If statement for running code for active actor, Placeholder for integrating carGroup into method
            robot.setRotate((y == 0) ? ((x > 0) ? 90 : -90) : (y > 0) ? 180 : 0); //set rotation based off movement.

            //test move robot to new position.
            double newXPos = (robotSpeed * x) + robot.getLayoutX();
            double newYPos = (robotSpeed * y) + robot.getLayoutY();

            // find the center of the robot image
            double roboCenterX = (robot.getImage().getWidth() / 2.0);
            double roboCenterY = (robot.getImage().getHeight() / 2.0);

            //keep movement within the bounds of the image.
            if (newXPos > maze.getImage().getWidth()) newXPos = maze.getImage().getWidth();
            else if (newXPos < 0) newXPos = 0;
            if (newYPos > maze.getImage().getHeight()) newYPos = maze.getImage().getHeight();
            else if (newYPos < 0) newYPos = 0;

            //offset scan to center and outward into the movement direction
            double scanPosX = newXPos + roboCenterX + x * roboCenterX;
            double scanPosY = newYPos + roboCenterY + y * roboCenterY;

            //Set Debug Info..
            String txt1 = isWallInFront() + "";
            inFrontTxt.setText("isWallInFront: " + txt1);
            inFrontTxt.getStyleClass().clear();
            inFrontTxt.getStyleClass().add(txt1);
            String txt2 = isWallOnLeft() + "";
            onLeftTxt.setText("isWallOnLeft: " + txt2);
            onLeftTxt.getStyleClass().clear();
            onLeftTxt.getStyleClass().add(txt2);
            int d = robotFowardDirection;
            direction.setText("direction: " + (d == UP ? "UP" : (d == DOWN ? "DOWN" : (d == LEFT ? "LEFT" : "RIGHT"))));

            //search the image at the scan location for a color. keep scan within bounds of image.
            if (isColorValid(getColorAtPosition(scanPosX, scanPosY))) {
                robot.setLayoutX(newXPos);
                robot.setLayoutY(newYPos);
  //          }
        }
    }

    @FXML
    public void onKeyPressed(KeyEvent e) {//Event passed from main stage key event
        switch (e.getCode()) {
            case UP: robotFowardDirection=UP; moveRobot(); break;
            case DOWN: robotFowardDirection=DOWN; moveRobot(); break;
            case LEFT: robotFowardDirection=LEFT; moveRobot(); break;
            case RIGHT: robotFowardDirection=RIGHT; moveRobot(); break;
        }
    }

    //Image file uploader
    FileChooser fileChooser = new FileChooser();

    @FXML
    public void onImageSelectClicked() throws MalformedURLException {
        // Setting the initial directory to our images folder
        File initialDirectory = null;
        try {
            // Get the resources/org.../images folder
            initialDirectory = new File(getClass().getResource("/org/example/homework03/images").toURI());
        } catch (Exception e) {
            e.printStackTrace(); // Handle any potential exceptions
        }

        // Check if the directory exists and set it as the initial directory
        if (initialDirectory != null && initialDirectory.exists()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }

        File file = fileChooser.showOpenDialog(maze.getScene().getWindow());
        if (file != null) {
            maze.setImage(new Image(file.toURI().toURL().toExternalForm()));
            initialPosition.setText("Please set the initial position of the robot");
            initializeRobotPosition(); // Initialize robot position after loading the maze
        }
    }

    //Auto Solver
    @FXML
    Timeline timeline;
    public void onStartClicked() {

        if (!startAuto.isSelected()) {
            timeline.stop();
            startAuto.setText("Start Auto Solve");
            return;
        }
        startAuto.setText("Stop Auto Solve");

//        Timeline InitialTimeline = new Timeline(new KeyFrame(Duration.millis(20), event -> {
//            if (!isWallOnLeft()) {
//                moveRobot();
//            }
//        }));

//        InitialTimeline.setCycleCount(Timeline.INDEFINITE);
//        InitialTimeline.play();

        timeline = new Timeline(new KeyFrame(Duration.millis(20), event -> {

            if (robot.getLayoutX() < maze.getImage().getWidth() * .94) {



                // navigate while robo position is less than %94 of the maze's width
                if (isWallOnLeft()) {
                    if (isWallInFront()) {
                        moveRobot();// move a bit before rotating
                        robotFowardDirection=(robotFowardDirection+1)%4;// Rotate CW
                    }
                    moveRobot();//left wall is present. keep moving!
                } else {
                    moveRobot();// move a bit before rotating
                    robotFowardDirection=(robotFowardDirection+3)%4;// Rotate CCW
                    moveRobot();moveRobot();moveRobot();//extra moves in case left wall is not immediately there.
                }
            }
            else {// It workss
                System.out.println("DOONNNNEE!!!!!");
                startAuto.setSelected(false);
                startAuto.setText("Start Auto Solve");
                timeline.stop();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    //region Collision Test Methods
    private boolean isWallOnLeft() {
        //set scan pos to the center of the robot.
        double scanPosX = robot.getLayoutX() + (robot.getImage().getWidth()/2.0);;
        double scanPosY = robot.getLayoutY() + (robot.getImage().getHeight()/2.0);;
        //Move scan toward the left of the robot.
        switch ( robotFowardDirection ) {
            case UP: scanPosX-=robot.getImage().getWidth();break;
            case DOWN: scanPosX+=robot.getImage().getWidth();break;
            case LEFT: scanPosY+=robot.getImage().getHeight();break;
            case RIGHT: scanPosY-=robot.getImage().getHeight();break;
        }
        // Read the color at the scan position
        return !isColorValid(getColorAtPosition(scanPosX,scanPosY));
    }

    private boolean isWallInFront() {
        //set scan pos to the center of the robot.
        double scanPosX = robot.getLayoutX() + (robot.getImage().getWidth()/2.0);;
        double scanPosY = robot.getLayoutY() + (robot.getImage().getHeight()/2.0);;
        //Move scan toward the current facing direction.
        switch (robotFowardDirection) {
            case UP: scanPosY-=robot.getImage().getHeight();break;
            case DOWN: scanPosY+=robot.getImage().getHeight();break;
            case LEFT: scanPosX-=robot.getImage().getWidth();break;
            case RIGHT: scanPosX+=robot.getImage().getWidth();break;
        }
        // Read the color at the scan position
        return !isColorValid(getColorAtPosition(scanPosX,scanPosY));
    }
    //endregion

    //region Color Test Methods
    public boolean isColorValid(Color colorAtPos) {
        return (colorAtPos.getRed()>0.9 && colorAtPos.getGreen()>0.9 && colorAtPos.getBlue()>0.9 );
    }
    private Color getColorAtPosition(double posX, double posY) {
        return (posX > 0 && posY > 0 && posX < maze.getImage().getWidth() && posY < maze.getImage().getHeight()) ? maze.getImage().getPixelReader().getColor((int) (posX), (int) (posY)) : Color.BLACK;
    }
    //endregion

    //ToDo clean up path code.
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
