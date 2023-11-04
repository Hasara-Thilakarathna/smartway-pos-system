package lk.ijse.dep11.app.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.dep11.app.db.OrderDataAccess;
import lk.ijse.dep11.app.tm.Order;

import java.io.IOException;
import java.sql.SQLException;

public class SearchOrderFormController {

    public AnchorPane root;
    public TextField txtSearch;
    public TableView<Order> tblOrders;
    public ImageView imgBack;

    public void initialize() throws IOException {
        String[] colNames = {"orderId", "orderDate", "customerId", "customerName", "orderTotal"};
        for (int i = 0; i < colNames.length; i++) {
            tblOrders.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(colNames[i]));
        }

        try {
            tblOrders.getItems().addAll(OrderDataAccess.findOrders(""));
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Failed to load orders, try again").show();
            e.printStackTrace();
        }

        txtSearch.textProperty().addListener((ov, prev, cur) -> {
            tblOrders.getItems().clear();
            try {
                tblOrders.getItems().addAll(OrderDataAccess.findOrders(cur))
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    public void navigateOnMouseClicked(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainView.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.root.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000),root);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
        Platform.runLater(primaryStage::sizeToScene);
    }

    public void playAnimationOnMouseEnter(MouseEvent event) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.DARKGREEN);
        dropShadow.setWidth(20);
        dropShadow.setHeight(20);
        dropShadow.setRadius(20);
        imgBack.setEffect(dropShadow);
    }

    public void playAnimationOnMouseExit(MouseEvent event) {
        imgBack.setEffect(null);
    }

    public void focusOnMouseEnter(MouseEvent event) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.LIGHTGRAY);
        dropShadow.setWidth(20);
        dropShadow.setHeight(20);
        dropShadow.setRadius(20);
        txtSearch.setEffect(dropShadow);
    }

    public void focusOnMouseExit(MouseEvent event) {
        txtSearch.setEffect(null);

    }
}
