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
import models.User;
import utils.FXMLHelper;

import java.util.List;

public class StorekeeperScreenController implements FXMLHelper.PreloadableController {

    private List<Order> allOrders;

    @FXML
    private Label greetingLabel;

    @FXML
    private TableView<Order> allOrdersTable;
    @FXML
    private TableColumn<Order, String> userNameColumn;
    @FXML
    private TableColumn<Order, String> itemsCountColumn;
    @FXML
    private TableColumn<Order, String> costColumn;
    @FXML
    private TableColumn<Order, Boolean> finishedColumn;

    @SafeVarargs
    @Override
    public final <T> void preload(T... objects) {
        if (User.activeUser != null) {
            greetingLabel.setText(String.format("Здравствуйте, %s %s", User.activeUser.getPosition(), User.activeUser.getName()));
        }

        allOrders = Database.getOrdersDb().select(null);

        userNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getUser().getName()));
        itemsCountColumn.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().getStorage().getEntities_ids().size())));
        costColumn.setCellValueFactory(param -> new SimpleStringProperty(Double.toString(param.getValue().getStorage().getCost())));
        finishedColumn.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue().isFinished()));

        fillTable();
    }

    private void fillTable() {
        allOrdersTable.setItems(FXCollections.observableArrayList(allOrders));
        allOrdersTable.refresh();//force table update
    }

    public void onButtonFinishClick(ActionEvent actionEvent) {
        int selectedIndex = allOrdersTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            FXMLHelper.alertAndWait("Ошибка", "Заказ не выбран", "Выберите заказ для завершения!");
            return;
        }

        Order order = allOrdersTable.getItems().get(selectedIndex);

        if (order.isFinished()) {
            FXMLHelper.alertAndWait("Внимание", "Неподдерживаемое действие", "Вы не можете завершить уже завершённый заказ");
            return;
        }

        order.setFinished(true);
        order.push();
        fillTable();

        FXMLHelper.alertAndWait("Успех", "Операция выполнена", "Заказ завершён!");
    }

    public void onButtonShowClick(ActionEvent actionEvent) {
        int selectedIndex = allOrdersTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            FXMLHelper.alertAndWait("Ошибка", "Заказ не выбран", "Выберите заказ для показа!");
            return;
        }
        Order order = allOrdersTable.getItems().get(selectedIndex);
        OrderScreenController orderScreenController = FXMLHelper.loadScreenReturnController("OrderScreen");
        orderScreenController.preload(order);
    }

    public void onButtonLogOutClick(ActionEvent actionEvent) {
        User.activeUser = null;
        FXMLHelper.backScreen();
    }
}
