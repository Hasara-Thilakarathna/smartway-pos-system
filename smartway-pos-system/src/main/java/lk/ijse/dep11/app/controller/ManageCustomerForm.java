package lk.ijse.dep11.app.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.dep11.app.db.CustomerDataAccess;
import lk.ijse.dep11.app.tm.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

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
    public HBox hbox;

    public void navigateOnMouseClicked(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainView.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.root.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(primaryStage::sizeToScene);
    }

    public void initialize() {
        root.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            hbox.setLayoutX(newValue.doubleValue() / 2 - (btnSave.widthProperty().getValue() / 2));
        });

        txtCustomerId.setEditable(false);
        btnDelete.setDisable(true);
        btnSave.setDefaultButton(true);

        int i = 0;
        for (String col : new String[]{"id", "name", "address"}) {
            tblCustomers.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(col));
            i++;
        }

        tblCustomers.getSelectionModel().selectedItemProperty().addListener((ov, prev, curr) -> {
            if (curr != null) {
                btnDelete.setDisable(false);
                btnSave.setText("UPDATE");
                txtCustomerId.setText(curr.getId());
                txtCustomerName.setText(curr.getName());
                txtCustomerAddress.setText(curr.getAddress());
            } else {
                btnSave.setText("SAVE");
                btnDelete.setDisable(true);
            }
        });

    }

    public void btnNewCustomerOnAction(ActionEvent actionEvent) {
        for (TextField textField : new TextField[]{txtCustomerName, txtCustomerId, txtCustomerAddress}) {
            textField.clear();
        }
        txtCustomerName.requestFocus();
        try {
            if (CustomerDataAccess.getAllCustomers().isEmpty()) {
                txtCustomerId.setText("C001");
            } else {
                int newId = Integer.parseInt(CustomerDataAccess.getLastId().substring(1)) + 1;
                txtCustomerId.setText(String.format("C%03d", newId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        if(!isDataValid()) return;

        Customer customer = new Customer(txtCustomerId.getText()
                , txtCustomerName.getText().strip(), txtCustomerAddress.getText().strip());
        try {
            if(btnSave.getText().equals("SAVE")){
                CustomerDataAccess.saveCustomer(customer);
                tblCustomers.getItems().add(customer);
                new Alert(Alert.AlertType.CONFIRMATION,"Customer saved successfully").show();
            }else {
                CustomerDataAccess.updateCustomer(customer);
                Customer selectedItem = tblCustomers.getSelectionModel().getSelectedItem();
                tblCustomers.getItems().set(tblCustomers.getItems().indexOf(selectedItem),customer);
                new Alert(Alert.AlertType.CONFIRMATION,"Customer updated successfully").show();
            }
            tblCustomers.refresh();
            btnNewCustomer.fire();

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Failed to save the customer, Please try again").show();
            throw new RuntimeException(e);
        }
    }

    private boolean isDataValid() {
        String name = txtCustomerName.getText().strip();
        String address = txtCustomerAddress.getText().strip();
        if (!name.matches("[A-Za-z ]{3,}")) {
            txtCustomerName.requestFocus();
            txtCustomerName.selectAll();
            return false;
        } else if (address.length() < 3) {
            txtCustomerAddress.requestFocus();
            txtCustomerAddress.selectAll();
            return false;
        }
        return true;
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) throws SQLException {

    }

    public void playAnimationOnMouseEnter(MouseEvent event) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), (ImageView) event.getSource());
        scaleTransition.setToX(1.3);
        scaleTransition.setToY(1.3);
        scaleTransition.play();

    }

    public void playAnimationOnMouseExit(MouseEvent event) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), (ImageView) event.getSource());
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.play();

    }
}
