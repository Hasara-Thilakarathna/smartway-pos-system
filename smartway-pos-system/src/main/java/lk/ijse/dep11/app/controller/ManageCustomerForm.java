package lk.ijse.dep11.app.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
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
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000),root);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
        Platform.runLater(primaryStage::sizeToScene);
    }

    public void initialize() {
        ImageView imageView = new ImageView(new Image("asset.img/addNewCustomer.png"));
        imageView.setFitHeight(50);
        imageView.setPreserveRatio(true);
        btnNewCustomer.setGraphic(imageView);
        btnNewCustomer.setTooltip(new Tooltip("Add New Customer"));


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
        loadAllCustomers();
        Platform.runLater(txtCustomerName::requestFocus);
        Platform.runLater(()-> btnNewCustomer.fire());
    }

    private void loadAllCustomers() {
        try {
            tblCustomers.getItems().addAll(CustomerDataAccess.getAllCustomers());
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Failed to load customers, Please try again").show();
            throw new RuntimeException(e);
        }
    }

    public void btnNewCustomerOnAction(ActionEvent actionEvent) {

        for (TextField textField : new TextField[]{txtCustomerName, txtCustomerId, txtCustomerAddress}) {
            textField.clear();
        }
        txtCustomerName.requestFocus();
        tblCustomers.getSelectionModel().clearSelection();
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
        if(event.getSource() instanceof Node) {
            Node node = (Node) event.getSource();

            ScaleTransition scaleTrans = new ScaleTransition(Duration.millis(350), node);
            scaleTrans.setToX(1.2);
            scaleTrans.setToY(1.2);
            scaleTrans.play();

            DropShadow ds = new DropShadow();
            ds.setColor(Color.DARKGREEN);
            ds.setWidth(20);
            ds.setHeight(20);
            ds.setRadius(20);
            node.setEffect(ds);
        }

    }

    public void playAnimationOnMouseExit(MouseEvent event) {
        if ((event.getSource() instanceof Node)) {
            Node node = (Node) event.getSource();
            ScaleTransition scaleTrans = new ScaleTransition(Duration.millis(500), node);
            scaleTrans.setToX(1);
            scaleTrans.setToY(1);
            scaleTrans.play();
            node.setEffect(null);
        }

    }
}
