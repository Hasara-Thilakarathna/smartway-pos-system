package lk.ijse.dep11.app.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.dep11.app.tm.Customer;

import java.io.IOException;
import java.net.URL;

public class ManageCustomerForm {
    public AnchorPane root;
    public TableView<Customer> tblCustomers;
    public ImageView imgBack;
    public JFXButton btnNewCustomer;
    public JFXTextField txtCustomerId;
    public JFXTextField txtCustomerName;
    public JFXTextField txtCustomerAddress;
    public JFXButton btnSave;
    public JFXButton btnDelete;

    public void navigateOnMouseClicked(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainView.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.root.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(primaryStage::sizeToScene);
    }

    public void initialize() {
        txtCustomerId.setEditable(false);
        btnDelete.setDisable(true);
        btnNewCustomer.fire();

        int i=0;
        for (String col : new String[]{"id", "name", "address"}) {
            tblCustomers.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(col));
            i++;
        }

    }

    public void btnNewCustomerOnAction(ActionEvent actionEvent) {
        for (TextField textField : new TextField[]{txtCustomerName, txtCustomerId, txtCustomerAddress}) {
            textField.clear();
        }
        txtCustomerName.requestFocus();

    }

    public void btnSaveOnAction(ActionEvent actionEvent) {

    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
    }

    public void playAnimationOnMouseEnter(MouseEvent event) {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100),(ImageView)event.getSource());
            scaleTransition.setToX(1.3);
            scaleTransition.setToY(1.3);
            scaleTransition.play();

    }

    public void playAnimationOnMouseExit(MouseEvent event) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100),(ImageView)event.getSource());
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.play();

    }
}
