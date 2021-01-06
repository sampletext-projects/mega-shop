package controllers;

import database.Database;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.FXMLHelper;

import java.util.List;

public class UserScreenController implements FXMLHelper.PreloadableController, FXMLHelper.NotifiableController {

    private ShopStorage mainStorage;
    private Cart userCart;

    @FXML
    private Label greetingLabel;

    @FXML
    private Label labelCost;

    @FXML
    private TableView<ShopEntity> shopStorageTable;

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


    @FXML
    private TableView<ShopEntity> userCartTable;

    @FXML
    private TableColumn<ShopEntity, String> columnCartBrand;

    @FXML
    private TableColumn<ShopEntity, String> columnCartModel;

    @FXML
    private TableColumn<ShopEntity, String> columnCartType;

    @FXML
    private TableColumn<ShopEntity, String> columnCartPrice;

    @FXML
    private TableColumn<ShopEntity, String> columnCartAmount;

    @SafeVarargs
    @Override
    public final <T> void preload(T... object) {
        if (User.activeUser != null) {
            greetingLabel.setText(String.format("Hello, %s %s", User.activeUser.getPosition(), User.activeUser.getName()));
        }

        columnBrand.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getShopItem().getBrand()));
        columnModel.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getShopItem().getModel()));
        columnType.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getShopItem().getType()));
        columnPrice.setCellValueFactory(param -> new SimpleStringProperty(Float.toString(param.getValue().getShopItem().getPrice())));
        columnAmount.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().getAmount())));

        columnCartBrand.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getShopItem().getBrand()));
        columnCartModel.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getShopItem().getModel()));
        columnCartType.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getShopItem().getType()));
        columnCartPrice.setCellValueFactory(param -> new SimpleStringProperty(Float.toString(param.getValue().getShopItem().getPrice())));
        columnCartAmount.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().getAmount())));

        mainStorage = Database.getShopStoragesDb().getMainStorage();
        userCart = Database.getCartsDb().getByUserId(User.activeUser.getId());
        labelCost.setText(String.format("Total: %.2f c.u.", userCart.getStorage().getCost()));

        fillTables();
    }

    @SafeVarargs
    @Override
    public final <T> void onNotify(T... objects) {
        String command = (String) objects[0];
        if ("Update".equals(command)) {
            mainStorage = Database.getShopStoragesDb().getMainStorage();
            userCart = Database.getCartsDb().getByUserId(User.activeUser.getId());

            fillTables();
        }
    }

    private void fillTables() {
        shopStorageTable.setItems(FXCollections.observableArrayList(mainStorage.getEntities()));
        userCartTable.setItems(FXCollections.observableArrayList(userCart.getStorage().getEntities()));
        shopStorageTable.refresh();
        userCartTable.refresh();
    }

    public void onButtonAddToCartClick(ActionEvent actionEvent) {
        int selectedIndex = shopStorageTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            FXMLHelper.alertAndWait("Error", "Entity is not selected", "Please select an entity to add to cart!");
            return;
        }

        // Decrease amount in shop
        ShopEntity selectedEntity = shopStorageTable.getItems().get(selectedIndex);

        if (selectedEntity.getAmount() == 0) {
            FXMLHelper.alertAndWait("Error", "Entity is not available", "Amount of this entity is 0!");
            return;
        }

        selectedEntity.setAmount(selectedEntity.getAmount() - 1);
        selectedEntity.push();

        // Add to user cart


        //Find entity in user cart
        int userCartItemIndex = -1;
        List<ShopEntity> entities = userCart.getStorage().getEntities();
        for (int i = 0; i < entities.size(); i++) {
            ShopEntity cartEntity = entities.get(i);
            if (cartEntity.getShopItem_id() == selectedEntity.getShopItem_id()) {
                userCartItemIndex = i;
                break;
            }
        }

        if (userCartItemIndex == -1) {
            // Cart doesn't contain this item
            ShopEntity userEntity = new ShopEntity(-1, selectedEntity.getShopItem_id(), 1).push();//create new item
            userCart.getStorage().getEntities_ids().add(userEntity.getId());//add this entity to user cart storage
            userCart.getStorage().push().pull();//update user cart storage
        } else {
            // Cart already contains this item
            ShopEntity entity = userCart.getStorage().getEntities().get(userCartItemIndex);// get entity in user cart
            entity.setAmount(entity.getAmount() + 1);//update amount
            entity.push();//update this item in db
            userCart.getStorage().pull();
        }

        FXMLHelper.alertAndWait("Success", "Operation succeeded", "Entity was added to your cart!");

        labelCost.setText(String.format("Total: %.2f c.u.", userCart.getStorage().getCost()));

        fillTables();
    }

    public void onButtonRemoveOneClick(ActionEvent actionEvent) {
        int selectedIndex = userCartTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            FXMLHelper.alertAndWait("Error", "Entity is not selected", "Please select an entity to remove from cart!");
            return;
        }

        // Decrease amount in cart
        ShopEntity selectedEntity = userCartTable.getItems().get(selectedIndex);
        selectedEntity.setAmount(selectedEntity.getAmount() - 1);
        selectedEntity.push();

        if (selectedEntity.getAmount() == 0) {
            // This entity is now not present in cart
            userCart.getStorage().getEntities_ids().remove((Integer) selectedEntity.getId());
            userCart.getStorage().push().pull();
            selectedEntity.erase();//Remove this entity from DB
        }

        //Find entity in main storage
        int mainStorageEntityIndex = -1;
        for (int i = 0; i < mainStorage.getEntities().size(); i++) {
            ShopEntity shopEntity = mainStorage.getEntities().get(i);
            if (shopEntity.getShopItem_id() == selectedEntity.getShopItem_id()) {
                mainStorageEntityIndex = i;
                break;
            }
        }

        //Increase amount in main storage
        ShopEntity shopEntity = mainStorage.getEntities().get(mainStorageEntityIndex);
        shopEntity.setAmount(shopEntity.getAmount() + 1);
        shopEntity.push();

        FXMLHelper.alertAndWait("Success", "Operation succeeded", "Entity was removed from your cart!");

        labelCost.setText(String.format("Total: %.2f c.u.", userCart.getStorage().getCost()));

        fillTables();
    }

    public void onButtonOrderClick(ActionEvent actionEvent) {
        if (userCart.getStorage().getEntities().isEmpty()) {
            FXMLHelper.alertAndWait("Error", "Cart is empty", "Please add something to cart to make an order!");
            return;
        }

        //Duplicate storage
        ShopStorage orderStorage = new ShopStorage(-1, userCart.getStorage().getEntities_ids()).push().pull();//insert storage into Db
        //pull is necessary because of non-copy list pass

        Order order = new Order(-1, User.activeUser.getId(), orderStorage.getId(), false).push();//insert order into Db

        //We don't clear cart entities because they are transfered into order storage

        userCart.getStorage().getEntities_ids().clear();//reset the list of entities in cart
        userCart.getStorage().getEntities().clear();//We don't push entities, because they are automatically transfered to Cart Storage
        userCart.getStorage().push();//update cart storage in DB

        fillTables();

        labelCost.setText(String.format("Total: %.2f c.u.", userCart.getStorage().getCost()));

        FXMLHelper.alertAndWait("Success", "Operation succeeded", "Order was created!");
    }

    public void onButtonMyOrdersClick(ActionEvent actionEvent) {
        UserOrdersScreenController userOrdersScreenController = FXMLHelper.loadScreenReturnController("UserOrdersScreen");
        userOrdersScreenController.preload();
    }

    public void onButtonLogOutClick(ActionEvent actionEvent) {
        User.activeUser = null;
        FXMLHelper.backScreen();
    }
}
