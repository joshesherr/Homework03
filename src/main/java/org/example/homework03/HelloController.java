package org.example.homework03;

import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

public class HelloController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView background;

    @FXML
    private ImageView robot;

    @FXML
    private VBox vBox;

    @FXML
    public void onMouseClick() {
        //Setting up the path
        Path path = new Path();
        path.getElements().add (new MoveTo(537f, 228f));
        path.getElements().add (new CubicCurveTo(240f, 230f, 500f, 340f, 600, 50f));

        //Instantiating PathTransition class
        javafx.animation.PathTransition pathTransition = new javafx.animation.PathTransition();

        //Setting duration for the PathTransition
        pathTransition.setDuration(Duration.millis(10000));

        //Setting Node on which the path transition will be applied
        pathTransition.setNode(robot);

        //setting path for the path transition
        pathTransition.setPath(path);

        //setting orientation for the path transition
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);

        //setting up the cycle count
        pathTransition.setCycleCount(10);

        //setting auto reverse to be true
        pathTransition.setAutoReverse(true);

        //Playing path transition
        pathTransition.play();
    }

}
