package lk.ijse.dep11.app.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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
import lk.ijse.dep11.app.db.CustomerDataAccess;
import lk.ijse.dep11.app.db.ItemDataAccess;
import lk.ijse.dep11.app.db.OrderDataAccess;
import lk.ijse.dep11.app.tm.Customer;
import lk.ijse.dep11.app.tm.Item;
import lk.ijse.dep11.app.tm.OrderItem;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlaceOrderFormController {
    public JFXButton btnAddOrder;
    public AnchorPane root;
    public ImageView imgBack;
    public JFXButton btnPlaceOrder;
    public JFXComboBox<Customer> cmbCustomerId;
    public JFXTextField txtCustomerName;
    public JFXComboBox<Item> cmbItemCode;
    public JFXTextField txtItemDescription;
    public JFXTextField txtQtyOnHand;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQty;
    public TableView<OrderItem> tblOrderDetails;
    public Label lblOrderId;
    public Label lblDate;
    public Label lblTotal;

    public void initialize() throws IOException {
        ImageView imageView = new ImageView(new Image("/asset.img/addOrder.png"));
        imageView.setFitWidth(30);
        imageView.setPreserveRatio(true);
        btnAddOrder.setGraphic(imageView);


        String[] cols = {"code", "description", "qty", "unitPrice", "total", "btnDelete"};
        for (int i = 0; i < cols.length; i++) {
            tblOrderDetails.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(cols[i]));
        }

        lblDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        newOrder();
        cmbCustomerId.getSelectionModel().selectedItemProperty().addListener((ov, prev, cur) -> {
            enablePlaceOrderButton();
            if (cur != null) {
                txtCustomerName.setText(cur.getName());
                txtCustomerName.setDisable(false);
                txtCustomerName.setEditable(false);
            } else {
                txtCustomerName.clear();
                txtCustomerName.setDisable(true);
            }
        });
        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((ov, prev, cur) -> {
            if (cur != null) {
                txtItemDescription.setText(cur.getDescription());
                txtQtyOnHand.setText(cur.getQty() + "");
                txtUnitPrice.setText(cur.getUnitPrice().toString());

                for (TextField txt : new TextField[]{txtItemDescription, txtQtyOnHand, txtUnitPrice}) {
                    txt.setDisable(false);
                    txt.setEditable(false);
                }
                txtQty.setDisable(cur.getQty() == 0);
            } else {
                for (TextField txt : new TextField[]{txtItemDescription, txtQtyOnHand, txtUnitPrice, txtQty}) {
                    txt.setDisable(true);
                    txt.clear();
                }
            }
        });
        txtQty.textProperty().addListener((ov, prevQty, curQty) -> {
            Item selectedItem = cmbItemCode.getSelectionModel().getSelectedItem();
            btnAddOrder.setDisable(!(curQty.matches("\\d+") && Integer.parseInt(curQty) <= selectedItem.getQty()
                    && Integer.parseInt(curQty) > 0));
        });
    }

    private void newOrder() throws IOException {
        for (TextField txt : new TextField[]{txtCustomerName, txtItemDescription, txtQty, txtQtyOnHand, txtUnitPrice}) {
            txt.clear();
            txt.setDisable(true);
        }
        tblOrderDetails.getItems().clear();
        lblTotal.setText("TOTAL: Rs. 0.00");
        btnAddOrder.setDisable(true);
        btnPlaceOrder.setDisable(true);
        cmbCustomerId.getSelectionModel().clearSelection();
        cmbItemCode.getSelectionModel().clearSelection();
        try {
            cmbCustomerId.getItems().clear();
            cmbCustomerId.getItems().addAll(CustomerDataAccess.getAllCustomers());
            cmbItemCode.getItems().clear();
            cmbItemCode.getItems().addAll(ItemDataAccess.getAllItems());
            String lastOrderId = OrderDataAccess.getLastOrderId();
            if (lastOrderId == null){
                lblOrderId.setText("Order ID: OD001");
            }else{
                int newOrderId = Integer.parseInt(lastOrderId.substring(2)) + 1;
                lblOrderId.setText(String.format("Order ID: OD%03d", newOrderId));
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to establish database connection, try later").show();
            e.printStackTrace();
        }
        Platform.runLater(cmbCustomerId::requestFocus);
    }

    public void btnAddOrderOnAction(ActionEvent actionEvent) {
        Item selectedItem = cmbItemCode.getSelectionModel().getSelectedItem();
        Optional<OrderItem> optOrderItem = tblOrderDetails.getItems().stream()
                .filter(item -> selectedItem.getCode().equals(item.getCode())).findFirst();

        if (optOrderItem.isEmpty()) {
            JFXButton btnDelete = new JFXButton("Delete");
            OrderItem newOrderItem = new OrderItem(selectedItem.getCode(), selectedItem.getDescription(),
                    Integer.parseInt(txtQty.getText()), selectedItem.getUnitPrice(),btnDelete);
            tblOrderDetails.getItems().add(newOrderItem);
            btnDelete.setOnAction(e -> {
                tblOrderDetails.getItems().remove(newOrderItem);
                selectedItem.setQty(selectedItem.getQty() + newOrderItem.getQty());
                calculateOrderTotal();
                enablePlaceOrderButton();
            });
            selectedItem.setQty(selectedItem.getQty() - newOrderItem.getQty());
        } else {
            OrderItem orderItem = optOrderItem.get();
            orderItem.setQty(orderItem.getQty() + Integer.parseInt(txtQty.getText()));
            tblOrderDetails.refresh();
            selectedItem.setQty(selectedItem.getQty() - Integer.parseInt(txtQty.getText()));
        }
        cmbItemCode.getSelectionModel().clearSelection();
        cmbItemCode.requestFocus();
        calculateOrderTotal();
        enablePlaceOrderButton();

    }

    private void calculateOrderTotal() {
        Optional<BigDecimal> orderTotal = tblOrderDetails.getItems().stream()
                .map(oi -> oi.getTotal())
                .reduce((prev, cur) -> prev.add(cur));
        lblTotal.setText("Total: Rs. " + orderTotal.orElseGet(()->BigDecimal.ZERO).setScale(2));
    }

    public void txtQty_OnAction(ActionEvent actionEvent) {
    }

    private void enablePlaceOrderButton(){
        Customer selectedCustomer = cmbCustomerId.getSelectionModel().getSelectedItem();
        btnPlaceOrder.setDisable(!(selectedCustomer != null && !tblOrderDetails.getItems().isEmpty()));
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

    public void navigateOnMouseClicked(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainView.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) this.root.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000),root);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
        Platform.runLater(stage::sizeToScene);
    }

    public void btnPlaceOrderOnAction(ActionEvent actionEvent) throws IOException {
        try {
            OrderDataAccess.saveOrder(lblOrderId.getText().replace("Order ID: ", "").strip(),
                    Date.valueOf(lblDate.getText()),
                    cmbCustomerId.getValue().getId(),
                    tblOrderDetails.getItems());
            printBill();
            newOrder();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save the order, try again").show();
        }
    }


    private void printBill(){
        try {
            JasperDesign jasperDesign = JRXmlLoader
                    .load(getClass().getResourceAsStream("/print/posBill.jrxml"));

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            Map<String, Object> reportParams = new HashMap<>();
            reportParams.put("id", lblOrderId.getText().replace("Order ID: ", "").strip());
            reportParams.put("date", lblDate.getText());
            reportParams.put("customer-id", cmbCustomerId.getValue().getId());
            reportParams.put("customer-name", cmbCustomerId.getValue().getName());
            reportParams.put("total", lblTotal.getText());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, reportParams,
                    new JRBeanCollectionDataSource(tblOrderDetails.getItems()));

            JasperViewer.viewReport(jasperPrint, false);
            // JasperPrintManager.printReport(jasperPrint, false);
        } catch (JRException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to print the bill").show();
        }
    }
   
}
