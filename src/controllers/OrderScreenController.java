package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import models.Order;
import models.ShopEntity;
import utils.FXMLHelper;

public class OrderScreenController implements FXMLHelper.PreloadableController {

    private Order activeOrder;

    @FXML
    private Label labelInfo;
    @FXML
    private TextField textFieldUser;

    @FXML
    private TableView<ShopEntity> orderEntitiesTable;
    @FXML
    private TableColumn<ShopEntity, String> columnBrand;
    @FXML
    private TableColumn<ShopEntity, String> columnModel;
    @FXML
    private TableColumn<ShopEntity, String> columnType;
    @FXML
    private TableColumn<ShopEntity, String> columnPrice;
    @FXML
    private TableColumn<ShopEntity, String> columnAmount;

    @SafeVarargs
    @Override
    public final <T> void preload(T... objects) {
        if (objects.length == 0) {
            throw new IllegalArgumentException("Не передан заказ");
        }

        activeOrder = (Order) objects[0];
        columnBrand.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getShopItem().getBrand()));
        columnModel.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getShopItem().getModel()));
        columnType.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getShopItem().getType()));
        columnPrice.setCellValueFactory(param -> new SimpleStringProperty(Float.toString(param.getValue().getShopItem().getPrice())));
        columnAmount.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().getAmount())));
        textFieldUser.setText(activeOrder.getUser().getId() + ". " + activeOrder.getUser().getName());
        fillTable();
    }

    private void fillTable() {
        orderEntitiesTable.setItems(FXCollections.observableArrayList(activeOrder.getStorage().getEntities()));
        orderEntitiesTable.refresh();
    }

    public void onButtonBackClick(ActionEvent actionEvent) {
        FXMLHelper.backScreen();
    }
}
