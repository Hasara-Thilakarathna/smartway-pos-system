package lk.ijse.dep11.app;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lk.ijse.dep11.app.controller.SplashScreenController;
import lk.ijse.dep11.app.db.SingleConnectionDataSource;

import java.io.IOException;
import java.sql.SQLException;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        try (var connection = SingleConnectionDataSource.getInstance().getConnection()) {
            launch(args);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        AnchorPane root = FXMLLoader.load(getClass().getResource("/view/SplashScreen.fxml"));
        Scene mainScene = new Scene(root);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("SMARTWAY POS");
        primaryStage.centerOnScreen();
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        root.setBackground(Background.fill(Color.TRANSPARENT));
        mainScene.setFill(Color.TRANSPARENT);

        primaryStage.show();
        SplashScreenController.initialize(primaryStage);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000),root);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }
}
