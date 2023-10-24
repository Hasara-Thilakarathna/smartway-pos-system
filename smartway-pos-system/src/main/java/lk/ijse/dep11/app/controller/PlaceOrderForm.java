package lk.ijse.dep11.app.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlaceOrderForm {
    public JFXButton btnAddOrder;

    public void initialize() {
        ImageView imageView = new ImageView(new Image("/asset.img/addOrder.png"));
        imageView.setFitWidth(50);
        imageView.setPreserveRatio(true);
        btnAddOrder.setGraphic(imageView);
    }

    public void btnAddOrderOnAction(ActionEvent actionEvent) {

    }
}
