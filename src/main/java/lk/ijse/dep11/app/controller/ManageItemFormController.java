package lk.ijse.dep11.app.controller;

import com.jfoenix.controls.JFXButton;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.dep11.app.db.ItemDataAccess;
import lk.ijse.dep11.app.db.OrderDataAccess;
import lk.ijse.dep11.app.tm.Item;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

public class ManageItemFormController {
    public AnchorPane root;
    public ImageView imgBack;
    public TableView<Item> tblItems;
    public JFXButton btnAddNewItem;
    public JFXTextField txtItemCode;
    public JFXTextField txtItemDescription;
    public JFXTextField txtItemQty;
    public JFXTextField txtUnitPrice;
    public JFXButton btnSave;
    public JFXButton btnDelete;

    public void initialize() {
        btnDelete.setDisable(true);
        btnSave.setDefaultButton(true);

        ImageView imageView = new ImageView(new Image("asset.img/addItem.png"));
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        btnAddNewItem.setGraphic(imageView);
        btnAddNewItem.setTooltip(new Tooltip("Add New Item"));

        String[] cols = {"code", "description", "qty", "unitPrice"};
        for (int i = 0; i < cols.length; i++) {
            tblItems.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(cols[i]));
        }

        tblItems.getSelectionModel().selectedItemProperty().addListener((ov, prev, curr) -> {
            if (curr != null) {
                btnDelete.setDisable(false);
                btnSave.setText("UPDATE");
                txtItemCode.setText(curr.getCode());
                txtItemDescription.setText(curr.getDescription());
                txtItemQty.setText(curr.getQty() + "");
                txtUnitPrice.setText(curr.getUnitPrice()+"");
            } else {
                btnSave.setText("SAVE");
                btnDelete.setDisable(true);
            }
        });
        loadAllItems();
    }

    public void loadAllItems() {
        try {
            tblItems.getItems().addAll(ItemDataAccess.getAllItems());
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Failed to load the items, Pleas try again").show();
            throw new RuntimeException(e);
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
    public void btnSaveOnAction(ActionEvent actionEvent) {
        if(!isDataValid()) return;

        Item item = new Item(txtItemCode.getText().strip(), txtItemDescription.getText().strip()
                , Integer.parseInt(txtItemQty.getText()), new BigDecimal(txtUnitPrice.getText()));

        try {
            if(btnSave.getText().equals("SAVE")){
                ItemDataAccess.saveItem(item);
                tblItems.getItems().add(item);
                new Alert(Alert.AlertType.CONFIRMATION,"Item saved successfully").show();
            }else {
                ItemDataAccess.updateItem(item);
                Item selectedItem = tblItems.getSelectionModel().getSelectedItem();
                tblItems.getItems().set(tblItems.getItems().indexOf(selectedItem),item);
                new Alert(Alert.AlertType.CONFIRMATION,"Item updated successfully").show();
            }
            tblItems.refresh();
            btnAddNewItem.fire();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Failed to save the item, Please try again").show();
            throw new RuntimeException(e);
        }
    }

    private boolean isDataValid() {
        String code = txtItemCode.getText().strip();
        String description = txtItemDescription.getText().strip();
        String qty = txtItemQty.getText();
        String unitPrice = txtUnitPrice.getText();

        if(!code.matches("\\d{3,}")){
            txtItemCode.requestFocus();
            txtItemCode.selectAll();
            return false;
        } else if (!description.matches("[A-Za-z0-9 ]{4,}")) {
            txtItemDescription.requestFocus();
            txtItemDescription.selectAll();
            return false;
        } else if (!qty.matches("\\d+") || Integer.parseInt(qty) <= 0) {
            txtItemQty.requestFocus();
            txtItemQty.selectAll();
            return false;
        } else if (Double.parseDouble(unitPrice) < 0) {
            txtUnitPrice.requestFocus();
            txtUnitPrice.selectAll();
            return false;
        }
        return true;
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Item selectedItem = tblItems.getSelectionModel().getSelectedItem();
        try {
            if (OrderDataAccess.existsOrderByItemCode(selectedItem.getCode())){
                new Alert(Alert.AlertType.ERROR, "Failed to delete, the item already associated with an order").show();
            }else{
                ItemDataAccess.deleteItem(selectedItem.getCode());
                tblItems.getItems().remove(selectedItem);
                if (tblItems.getItems().isEmpty()) btnAddNewItem.fire();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to delete the item, try again").show();
        }
    }

    public void btnAddNewItem(ActionEvent actionEvent) {
        for (TextField textField : new TextField[]{txtItemCode,txtItemDescription, txtItemQty, txtUnitPrice}) {
            textField.clear();
        }
        txtItemCode.requestFocus();
        tblItems.getSelectionModel().clearSelection();
    }



}
