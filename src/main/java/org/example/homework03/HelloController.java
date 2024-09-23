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

    /**
     * Swaps the car and robot between the AnchorPane and HBox
     * @param event
     */
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

    /**
     * Spins the wheels of the car when the button is hovered
     * @param event
     */
    @FXML
    void spinWheels(MouseEvent event) {

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

    /**
     * Initializes the mouse event handlers
     */
    private void initializeRobotPosition() {
        // Add mouse event handlers to the maze ImageView
        maze.setOnMousePressed(this::onMousePressed);
        maze.setOnMouseDragged(this::onMouseDragged);
        maze.setOnMouseReleased(this::onMouseReleased);
    }

    /**
     * These methods set the robot's initial position when the mouse is pressed, dragged, and released.
     * It's been used when the maze was changed, so the robot can be placed in a new position.
     */
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

    /**
     * Sets the robot's position to the specified x and y coordinates.
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    private void setRobotPosition(double x, double y) {
        // Ensure the robot's position is within the bounds of the maze
        double newX = Math.max(0, Math.min(x - robot.getImage().getWidth() / 2.0, maze.getImage().getWidth() - robot.getImage().getWidth()));
        double newY = Math.max(0, Math.min(y - robot.getImage().getHeight() / 2.0, maze.getImage().getHeight() - robot.getImage().getHeight()));
        robot.setLayoutX(newX);
        robot.setLayoutY(newY);
    }

    /**
     * Moves the robot in the UP, DOWN, LEFT or RIGHT directions.
     */
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

//            //Set Debug Info..
//            String txt1 = isWallInFront() + "";
//            inFrontTxt.setText("isWallInFront: " + txt1);
//            inFrontTxt.getStyleClass().clear();
//            inFrontTxt.getStyleClass().add(txt1);
//            String txt2 = isWallOnLeft() + "";
//            onLeftTxt.setText("isWallOnLeft: " + txt2);
//            onLeftTxt.getStyleClass().clear();
//            onLeftTxt.getStyleClass().add(txt2);
//            int d = robotFowardDirection;
//            direction.setText("direction: " + (d == UP ? "UP" : (d == DOWN ? "DOWN" : (d == LEFT ? "LEFT" : "RIGHT"))));

            //search the image at the scan location for a color. keep scan within bounds of image.
            if (isColorValid(getColorAtPosition(scanPosX, scanPosY))) {
                robot.setLayoutX(newXPos);
                robot.setLayoutY(newYPos);
  //          }
        }
    }

    /**
     * Moves the robot according to a key pressed event.
     * @param e
     */
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

    /**
     * Opens a file chooser dialog to select an image file.
     * @throws MalformedURLException
     */
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
            initialPosition.setText("Please set the initial position of the robot, helping him to find the left wall :)");
            initializeRobotPosition(); // Initialize robot position after loading the maze
        }
    }

    //Auto Solver
    @FXML
    Timeline timeline;

    /**
     * Starts or stops the auto solve feature.
     */
    public void onStartClicked() {

        if (!startAuto.isSelected()) {
            timeline.stop();
            startAuto.setText("Start Auto Solve");
            return;
        }
        startAuto.setText("Stop Auto Solve");

        timeline = new Timeline(new KeyFrame(Duration.millis(20), event -> {

            if (robot.getLayoutX() < maze.getImage().getWidth() * .94) {



                // navigate while robot's position is less than %94 of the maze's width
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
            else {
                startAuto.setSelected(false);
                startAuto.setText("Start Auto Solve");
                timeline.stop();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * checks if there is a wall on the left of the robot.
     * @return boolean
     */
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

    /**
     * checks if there is a wall in front of the robot.
     * @return boolean
     */
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

    /**
     * Checks if the color at the specified position is valid.
     * @param colorAtPos
     * @return
     */
    public boolean isColorValid(Color colorAtPos) {
        return (colorAtPos.getRed()>0.9 && colorAtPos.getGreen()>0.9 && colorAtPos.getBlue()>0.9 );
    }

    /**
     * Gets the color at the specified position.
     * @param posX
     * @param posY
     * @return
     */
    private Color getColorAtPosition(double posX, double posY) {
        return (posX > 0 && posY > 0 && posX < maze.getImage().getWidth() && posY < maze.getImage().getHeight()) ? maze.getImage().getPixelReader().getColor((int) (posX), (int) (posY)) : Color.BLACK;
    }
}
