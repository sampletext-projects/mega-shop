package controllers;

import database.Database;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.Order;
import models.ShopEntity;
import models.ShopStorage;
import models.User;
import utils.FXMLHelper;

import java.util.List;

public class UserOrdersScreenController implements FXMLHelper.PreloadableController {

    public Label labelInfo;
    private List<Order> userOrders;

    @FXML
    private TableView<Order> userOrdersTable;
    @FXML
    private TableColumn<Order, String> itemsCountColumn;
    @FXML
    private TableColumn<Order, String> costColumn;
    @FXML
    private TableColumn<Order, Boolean> finishedColumn;

    @SafeVarargs
    @Override
    public final <T> void preload(T... objects) {
        userOrders = Database.getOrdersDb().select(t -> t.getUser_id() == User.activeUser.getId());

        itemsCountColumn.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().getStorage().getEntities_ids().size())));
        costColumn.setCellValueFactory(param -> new SimpleStringProperty(Double.toString(param.getValue().getStorage().getCost())));
        finishedColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue().isFinished()));

        fillTable();
    }

    private void fillTable() {
        userOrdersTable.setItems(FXCollections.observableArrayList(userOrders));
        userOrdersTable.refresh();//force table update
    }

    public void onButtonCancelClick(ActionEvent actionEvent) {
        int selectedIndex = userOrdersTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            FXMLHelper.alertAndWait("Error", "Order is not selected", "Please select an order to cancel!");
            return;
        }

        Order order = userOrdersTable.getItems().get(selectedIndex);

        if (order.isFinished()) {
            FXMLHelper.alertAndWait("Notice", "Unsupported action", "You can't cancel a finished order");
            return;
        }

        ShopStorage mainStorage = Database.getShopStoragesDb().getMainStorage();//Get Shop Storage

        //foreach entity in order
        for (ShopEntity orderEntity : order.getStorage().getEntities()) {

            //Find this entity in shop
            int mainStorageIndex = -1;
            List<ShopEntity> entities = mainStorage.getEntities();
            for (int i = 0; i < entities.size(); i++) {
                ShopEntity mainStorageEntity = entities.get(i);
                if (mainStorageEntity.getShopItem_id() == orderEntity.getShopItem_id()) {
                    mainStorageIndex = i;
                    break;
                }
            }

            if (mainStorageIndex != -1) {
                ShopEntity mainStorageEntity = mainStorage.getEntities().get(mainStorageIndex);
                //increase amount of shop entities
                mainStorageEntity.setAmount(mainStorageEntity.getAmount() + orderEntity.getAmount());
                mainStorageEntity.push();//update shop entity in DB
            }
            orderEntity.erase();//remove order storage entity
        }
        userOrders.remove(order);//remove order locally (IMPORTANT before erase because of comparation)
        order.getStorage().erase();//remove order storage
        order.erase();//remove order itself

        FXMLHelper.alertAndWait("Success", "Operation succeeded", "Order was canceled!");

        fillTable();
    }

    public void onButtonShowClick(ActionEvent actionEvent) {
        int selectedIndex = userOrdersTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            FXMLHelper.alertAndWait("Error", "Order is not selected", "Please select an order to show!");
            return;
        }
        Order order = userOrdersTable.getItems().get(selectedIndex);
        OrderScreenController orderScreenController = FXMLHelper.loadScreenReturnController("OrderScreen");
        orderScreenController.preload(order);
    }

    public void onButtonBackClick(ActionEvent actionEvent) {
        FXMLHelper.backScreen();
    }
}
