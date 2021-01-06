package controllers;

import database.Database;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.ShopEntity;
import models.ShopStorage;
import models.User;
import utils.FXMLHelper;

public class AdminScreenController implements FXMLHelper.PreloadableController, FXMLHelper.NotifiableController {

    private ShopStorage mainStorage;

    @FXML
    private Label greetingLabel;
    @FXML
    private TableView<ShopEntity> shopEntitiesTableView;
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
        if (User.activeUser != null) {
            greetingLabel.setText(String.format("Hello, %s %s", User.activeUser.getPosition(), User.activeUser.getName()));
        }

        columnBrand.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getShopItem().getBrand()));
        columnModel.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getShopItem().getModel()));
        columnType.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getShopItem().getType()));
        columnPrice.setCellValueFactory(param -> new SimpleStringProperty(Float.toString(param.getValue().getShopItem().getPrice())));
        columnAmount.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().getAmount())));

        mainStorage = Database.getShopStoragesDb().getMainStorage();

        fillTable();
    }

    @SafeVarargs
    @Override
    public final <T> void onNotify(T... objects) {
        String command = (String) objects[0];
        if ("Update".equals(command)) {
            mainStorage = Database.getShopStoragesDb().getMainStorage();
            fillTable();
        }
    }

    private void fillTable() {
        shopEntitiesTableView.setItems(FXCollections.observableArrayList(mainStorage.getEntities()));
        shopEntitiesTableView.refresh();
    }

    public void onButtonAddClick(ActionEvent actionEvent) {
        ShopEntityScreenController shopEntityScreenController = FXMLHelper.loadScreenReturnController("ShopEntityScreen");
        shopEntityScreenController.preload();
    }

    public void onButtonEditClick(ActionEvent actionEvent) {
        int selectedIndex = shopEntitiesTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            FXMLHelper.alertAndWait("Error", "Entity is not selected", "Please select an entity to edit!");
            return;
        }
        ShopEntity entity = shopEntitiesTableView.getItems().get(selectedIndex);

        ShopEntityScreenController shopEntityScreenController = FXMLHelper.loadScreenReturnController("ShopEntityScreen");
        shopEntityScreenController.preload(entity);
    }

    public void onButtonDeleteClick(ActionEvent actionEvent) {
        int selectedIndex = shopEntitiesTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            FXMLHelper.alertAndWait("Error", "Entity is not selected", "Please select an entity to delete!");
            return;
        }
        ShopEntity entity = shopEntitiesTableView.getItems().get(selectedIndex);
        mainStorage.getEntities_ids().remove((Integer) entity.getId());//remove entity from shop storage
        mainStorage.push().pull();//commit mainStorage and reload shop storage
        entity.erase();//erase shop entity
        fillTable();

        FXMLHelper.alertAndWait("Success", "Operation succeeded", "Entity was deleted!");
    }

    public void onButtonLogOutClick(ActionEvent actionEvent) {
        User.activeUser = null;
        FXMLHelper.backScreen();
    }
}
