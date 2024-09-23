package org.example.homework03;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
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
    private Button resetButton;


    @FXML
    private Circle wheel1;

    @FXML
    private Circle wheel2;

    @FXML
    private Text initialPosition;

    // Initial positions of the robot and car object
    private double initialRobotX;
    private double initialRobotY;
    private double initialCarX;
    private double initialCarY;


    private void moveCar() {
        anchorPane.requestFocus();

        int x = 0;
        int y = 0;

        switch (actorForwardDirection) {
            case UP:
                carGroup.setRotate(270); // Rotate up (270 degrees)
                carGroup.setScaleX(1);
                y = -1;
                break;
            case DOWN:
                carGroup.setRotate(90); // Rotate down (90 degrees)
                carGroup.setScaleX(1);
                y = 1;
                break;
            case LEFT:
                carGroup.setRotate(0); // Rotate left (180 degrees)
                carGroup.setScaleX(-1); //Flip car so it is not upside down
                x = -1;
                break;
            case RIGHT:
                carGroup.setRotate(0); //Set rotation back to 0 (Moving right)
                carGroup.setScaleX(1); // Normal orientation, facing right
                x = 1;
                break;
        }

        // Test moving the car to a new position.
        double newXPos = (robotSpeed * x) + carGroup.getLayoutX();
        double newYPos = (robotSpeed * y) + carGroup.getLayoutY();

        // Get the bounds of the carGroup
        Bounds carBounds = carGroup.getBoundsInParent();
        double carWidth = carBounds.getWidth() ;
        double carHeight = carBounds.getHeight();

        // Keep the movement within the bounds of the maze
        //If Car would exceed right bounds of maze, validate
        if (newXPos + carWidth > maze.getImage().getWidth()) {
            newXPos = maze.getImage().getWidth() - carWidth;
        } else if (newXPos < 0) { //Else if Car would exceed left bounds of maze, validate
            newXPos = 0;
        }

        //If Car would exceed lower bounds of maze, validate
        if (newYPos + carHeight > maze.getImage().getHeight()) {
            newYPos = maze.getImage().getHeight() - carHeight;
        } else if (newYPos < 0) { //Else if car would exceed upper bounds of maze, validate
            newYPos = 0;
        }

        // Perform collision detection (adjust for irregular shape)
        double[] center = findGroupCenter(carGroup);
        double scanPosX = center[0] + (x * robotSpeed) * 2;
        double scanPosY = center[1] + (y * robotSpeed) * 2;

        // Check color at scan position
        Color colorAtPos = getColorAtPosition(scanPosX, scanPosY);

        //search the image at the scan location for a color. keep scan within bounds of image.
        if (isColorValid(getColorAtPosition(scanPosX, scanPosY))) {
            carGroup.setLayoutX(newXPos);
            carGroup.setLayoutY(newYPos);
        }
    }

    private void setCarPosition(double x, double y) {
        double[] center = findGroupCenter(carGroup);
        double carCenterX = center[0];
        double carCenterY = center[1];

        // Ensure the carGroup's position is within the bounds of the maze
        double newX = Math.max(0, Math.min(x - carCenterX, maze.getImage().getWidth() - carCenterX * 2));
        double newY = Math.max(0, Math.min(y - carCenterY, maze.getImage().getHeight() - carCenterY * 2));

        carGroup.setLayoutX(newX);
        carGroup.setLayoutY(newY);
    }

    // Reset Button
    @FXML
    void onResetClicked(ActionEvent event) {
        anchorPane.requestFocus(); // Request focus to ensure key events are captured after resetting

        // Stop the auto solver if it's running
        if (timeline != null && timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.stop();
            startAuto.setSelected(false);
            startAuto.setText("Start Auto Solve");
        }

        // Reset the actor's direction to facing right
        actorForwardDirection = RIGHT;

        // Reset the robot's position and rotation to initial values
        robot.setLayoutX(initialRobotX);
        robot.setLayoutY(initialRobotY);
        robot.setRotate(0);

        // Reset the car's position, rotation, and scale
        carGroup.setLayoutX(initialCarX);
        carGroup.setLayoutY(initialCarY);
        carGroup.setRotate(0);
        carGroup.setScaleX(1);

        // Ensure the correct actor is displayed
        if (activeRobotActor) {
            // If the robot is the active actor
            if (!anchorPane.getChildren().contains(robot)) {
                anchorPane.getChildren().add(robot); // Add robot to the anchor pane if not already present
            }
            anchorPane.getChildren().remove(carGroup); // Remove car from the anchor pane
            swapCarBtn.setText("Switch to Car"); // Update the swap button text
        } else {
            // If the car is the active actor
            if (!anchorPane.getChildren().contains(carGroup)) {
                anchorPane.getChildren().add(carGroup); // Add car to the anchor pane if not already present
            }
            anchorPane.getChildren().remove(robot); // Remove robot from the anchor pane
            swapCarBtn.setText("Switch to Robot");
        }

        // Display status of reset
        initialPosition.setText("Reset complete!");
    }

    // Utility method to find center of carGroup, returns half width in X[0] and half Height in Y[1]
    private double[] findGroupCenter(Group group) {
        Bounds bounds = group.getBoundsInParent();
        double centerX = (bounds.getMinX() + bounds.getWidth() / 2.0);
        double centerY = (bounds.getMinY() + bounds.getHeight() / 2.0);
        return new double[] {centerX, centerY};
    }

    static final int UP=0, RIGHT=1, DOWN=2, LEFT=3;
    @FXML
    private ImageView robot;
    private final static int robotSpeed = 8;
    int actorForwardDirection =RIGHT;

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
        anchorPane.requestFocus();
        int x = 0;
        int y = 0;
        switch (actorForwardDirection) {
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

            //search the image at the scan location for a color. keep scan within bounds of image.
            if (isColorValid(getColorAtPosition(scanPosX, scanPosY))) {
                robot.setLayoutX(newXPos);
                robot.setLayoutY(newYPos);
            }
        }



    @FXML
    public void onKeyPressed(KeyEvent e) {//Event passed from main stage key event
        if (activeRobotActor) {
            switch (e.getCode()) {
                case UP: actorForwardDirection = UP;     moveRobot(); break;
                case DOWN: actorForwardDirection = DOWN; moveRobot(); break;
                case LEFT: actorForwardDirection = LEFT; moveRobot(); break;
                case RIGHT: actorForwardDirection =RIGHT; moveRobot(); break;
            }
        } else {
            switch (e.getCode()) {
                case UP: actorForwardDirection = UP;     moveCar(); break;
                case DOWN: actorForwardDirection = DOWN; moveCar(); break;
                case LEFT: actorForwardDirection = LEFT; moveCar(); break;
                case RIGHT: actorForwardDirection=RIGHT; moveCar(); break;
            }
        }
        e.consume();
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

            // Set initial positions
            initialRobotX = robot.getLayoutX();
            initialRobotY = robot.getLayoutY();

            initialCarX = carGroup.getLayoutX();
            initialCarY = carGroup.getLayoutY();
        }
    }

    // Helper method to move the active actor in auto solver
    private void moveActiveActor() {
        if (activeRobotActor) {
            moveRobot();
        } else {
            moveCar();
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

        timeline = new Timeline(new KeyFrame(Duration.millis(20), event -> {
            // Check active actor, and adjust behavior based on activeRobotActor boolean
            double currentX = activeRobotActor ? robot.getLayoutX() : carGroup.getLayoutX();

                // navigate while actor position is less than %94 of the maze's width
            if (currentX < maze.getImage().getWidth() * 0.94) {
                if (isWallOnLeft()) {
                    if (isWallInFront()) {
                        moveActiveActor();  // Move whichever actor is active
                        actorForwardDirection = (actorForwardDirection + 1) % 4;  // Rotate clockwise
                    }
                    moveActiveActor(); //left wall is present. keep moving!
                } else {
                    moveActiveActor();  // Move a bit before rotating
                    actorForwardDirection = (actorForwardDirection + 3) % 4;  // Rotate counterclockwise
                    moveActiveActor(); moveActiveActor(); moveActiveActor();  // Move forward extra steps in case left wall is not immediately there
                }
            }
            else {// It workss
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
        double scanPosX, scanPosY;

        if (activeRobotActor) {
            //set scan pos to the center of the robot.
            scanPosX = robot.getLayoutX() + (robot.getImage().getWidth() / 2.0);
            scanPosY = robot.getLayoutY() + (robot.getImage().getHeight() / 2.0);

            //Move scan toward the left of the robot.
            switch (actorForwardDirection) {
                case UP:
                    scanPosX -= robot.getImage().getWidth();
                    break;
                case DOWN:
                    scanPosX += robot.getImage().getWidth();
                    break;
                case LEFT:
                    scanPosY += robot.getImage().getHeight();
                    break;
                case RIGHT:
                    scanPosY -= robot.getImage().getHeight();
                    break;
            }
        } else {
            // Find car's center coordinates and adjust scan position
            double[] center = findGroupCenter(carGroup);
            scanPosX = center[0];
            scanPosY = center[1];

            //move scan toward current facing direction
            switch (actorForwardDirection) {
                case UP:
                    scanPosX -= carGroup.getBoundsInParent().getWidth();
                    break;
                case DOWN:
                    scanPosX += carGroup.getBoundsInParent().getWidth();
                    break;
                case LEFT:
                    scanPosY += carGroup.getBoundsInParent().getHeight();
                    break;
                case RIGHT:
                    scanPosY -= carGroup.getBoundsInParent().getHeight();
                    break;
            }
        }
        // Read the color at the scan position
        return !isColorValid(getColorAtPosition(scanPosX,scanPosY));
    }

    private boolean isWallInFront() {
        double scanPosX, scanPosY;

        if (activeRobotActor) {
            //set scan pos to the center of the robot.
            scanPosX = robot.getLayoutX() + (robot.getImage().getWidth() / 2.0);
            scanPosY = robot.getLayoutY() + (robot.getImage().getHeight() / 2.0);
            //Move scan toward the current facing direction.
            switch (actorForwardDirection) {
                case UP:
                    scanPosY -= robot.getImage().getHeight();
                    break;
                case DOWN:
                    scanPosY += robot.getImage().getHeight();
                    break;
                case LEFT:
                    scanPosX -= robot.getImage().getWidth();
                    break;
                case RIGHT:
                    scanPosX += robot.getImage().getWidth();
                    break;
            }
        } else {
            //set scan position to center of carGroup
            // Find car's center coordinates and adjust scan position
            double[] center = findGroupCenter(carGroup);
            scanPosX = center[0];
            scanPosY = center[1];
            //move scan toward current facing direction
            switch (actorForwardDirection) {
                case UP:
                    scanPosY -= carGroup.getBoundsInParent().getHeight();
                    break;
                case DOWN:
                    scanPosY += carGroup.getBoundsInParent().getHeight();
                    break;
                case LEFT:
                    scanPosX -= carGroup.getBoundsInParent().getWidth();
                    break;
                case RIGHT:
                    scanPosX += carGroup.getBoundsInParent().getWidth();
                    break;
            }
        }
        // Read the color at the scan position
        return !isColorValid(getColorAtPosition(scanPosX, scanPosY));
    }
    //endregion

    //region Color Test Methods
    public boolean isColorValid(Color colorAtPos) {
        return (colorAtPos.getRed()>0.9 && colorAtPos.getGreen()>0.9 && colorAtPos.getBlue()>0.9 );
    }
    private Color getColorAtPosition(double posX, double posY) {
        return (posX > 0 && posY > 0 && posX < maze.getImage().getWidth() && posY < maze.getImage().getHeight()) ? maze.getImage().getPixelReader().getColor((int) (posX), (int) (posY)) : Color.BLACK;
    }

    //Completed car code
    @FXML
    void swapWithRobot(ActionEvent event) {
        anchorPane.requestFocus();
        anchorPane.getChildren().remove(robot);
        carBox.getChildren().remove(carGroup);

        // Store the robot & car's positions
        double robotX = robot.getLayoutX();
        double robotY = robot.getLayoutY();

        double carX = carGroup.getLayoutX();
        double carY = carGroup.getLayoutY();

        // Find the index of the button in the HBox
        int buttonIndex = carBox.getChildren().indexOf(swapCarBtn);

        // Remove the nodes from their parent containers (Robot in AnchorPane, Car in Hbox)
        if (activeRobotActor) { //Validation to stop crashing, can implement else to swap backwards here
            activeRobotActor = false;
            swapCarBtn.setText("Switch to Robot");

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
            //Orient car back to standard position
            carGroup.setRotate(0);
            carGroup.setScaleX(1);
            //Swapped robot and carGroup between AnchorPane and HBox
        }
        else {
            activeRobotActor = true;
            swapCarBtn.setText("Switch to Car");
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
            //Orient car back to standard position
            carGroup.setRotate(0);
            carGroup.setScaleX(1);
        }
    }

    @FXML
    void spinWheels(MouseEvent event) {
        //Rotation for wheel 1
        RotateTransition spinWheel1 = new RotateTransition(Duration.seconds(.8), wheel1);
        spinWheel1.setByAngle(360);                   // Rotate by 360 degrees
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
            spinWheel1.stop();
            spinWheel2.stop();
        });
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
