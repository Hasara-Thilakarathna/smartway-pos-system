package lk.ijse.dep11.app.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Optional;

public class SplashScreenController {

    public AnchorPane root;
    public ImageView imgLogo;

    public static void initialize(Stage primary) throws IOException {
        AnchorPane root= FXMLLoader.load(SplashScreenController.class.getResource("/view/MainView.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Text Editor");
        stage.centerOnScreen();
        stage.setOnCloseRequest(windowEvent -> {
            Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure want to exit?", ButtonType.YES, ButtonType.NO).showAndWait();
            if(buttonType.get()==ButtonType.NO) windowEvent.consume();
        });

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(3000), event -> {
            primary.close();
            stage.show();
        }));
        timeline.setCycleCount(1);

        primary.show();
        timeline.play();

    }

}
