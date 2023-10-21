package lk.ijse.dep11.app.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainViewController {
    public AnchorPane root;
    public ImageView imgCustomer;
    public ImageView imgItem;
    public ImageView imgOrder;
    public ImageView imgSearch;
    public Label lblTitle;
    public Label lblDescription;

    public void initialize() {
        for (Label label : new Label[]{lblTitle, lblDescription}) {
            label.setVisible(false);
        }
    }

    public void playOnMouseEnter(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView img = (ImageView) event.getSource();
            for (Label label : new Label[]{lblTitle, lblDescription}) {
                label.setVisible(true);
            }

            ScaleTransition scaleTrans = new ScaleTransition(Duration.millis(500), img);
            scaleTrans.setToX(1.3);
            scaleTrans.setToY(1.3);
            scaleTrans.play();

            DropShadow ds = new DropShadow();
            ds.setColor(Color.DEEPSKYBLUE);
            ds.setWidth(20);
            ds.setHeight(20);
            ds.setRadius(20);
            img.setEffect(ds);

            switch (img.getId()){
                case "imgCustomer":
                    lblTitle.setText("Manage Customers");
                    lblDescription.setText("Click to add, edit, delete, search or view customers");
                    break;
                case "imgItem":
                    lblTitle.setText("Manage Items");
                    lblDescription.setText("Click to add, edit, delete, search or view items");
                    break;
                case "imgOrder":
                    lblTitle.setText("Place Orders");
                    lblDescription.setText("Click here if you want to place a new order");
                    break;
                case "imgSearch":
                    lblTitle.setText("Search Orders");
                    lblDescription.setText("Click if you want to search orders");
                    break;
            }
        }
    }

    public void playOnMouseExit(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView img = (ImageView) event.getSource();
            for (Label label : new Label[]{lblTitle, lblDescription}) {
                label.setVisible(false);
            }

            ScaleTransition scaleTrans = new ScaleTransition(Duration.millis(500), img);
            scaleTrans.setToX(1);
            scaleTrans.setToY(1);
            scaleTrans.play();

            img.setEffect(null);
        }
    }


    public void navigateOnMouseClicked(MouseEvent event) throws IOException {
        if (event.getSource() instanceof ImageView) {
            ImageView img= (ImageView) event.getSource();

            Parent root = null;

            switch (img.getId()) {
                case "imgCustomer":
                    root = FXMLLoader.load(this.getClass().getResource("/view/ManageCustomerForm.fxml"));
                    break;
                case "imgItem":
                    root = FXMLLoader.load(this.getClass().getResource("/view/ManageItemForm.fxml"));
                    break;
                case "imgOrder":
                    root = FXMLLoader.load(this.getClass().getResource("/view/PlaceOrderForm.fxml"));
                    break;
                case "imgSearch":
                    root = FXMLLoader.load(this.getClass().getResource("/view/SearchOrdersForm.fxml"));
                    break;
            }

            if (root != null) {
                Scene subScene = new Scene(root);
                Stage primaryStage = (Stage) this.root.getScene().getWindow();
                primaryStage.setScene(subScene);
                primaryStage.sizeToScene();
                primaryStage.centerOnScreen();

//                TranslateTransition tt = new TranslateTransition(Duration.millis(350), subScene.getRoot());
//                tt.setFromX(-subScene.getWidth());
//                tt.setToX(0);
//                tt.play();
            }
        }
    }
}
