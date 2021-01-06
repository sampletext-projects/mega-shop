package controllers;

import database.Database;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.util.StringConverter;
import models.ShopEntity;
import models.ShopItem;
import models.ShopStorage;
import utils.FXMLHelper;

import java.util.List;

public class ShopEntityScreenController implements FXMLHelper.PreloadableController, FXMLHelper.NotifiableController {

    private ShopEntity activeEntity;

    private List<ShopItem> shopItems;

    @FXML
    private Label labelInfo;
    @FXML
    private ComboBox<ShopItem> comboBoxItems;
    @FXML
    private Spinner<Integer> spinnerAmount;

    @SafeVarargs
    @Override
    public final <T> void preload(T... objects) {
        shopItems = Database.getShopItemsDb().select(null);
        comboBoxItems.setConverter(new StringConverter<ShopItem>() {
            @Override
            public String toString(ShopItem item) {
                return String.format("%s %s %s: %.2f р", item.getType(), item.getBrand(), item.getModel(), item.getPrice());
            }

            @Override
            public ShopItem fromString(String string) {
                return null;
            }
        });
        spinnerAmount.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                spinnerAmount.getValueFactory().setValue(Integer.parseInt(newValue));
            } catch (NumberFormatException ignored) {
                if (!oldValue.isEmpty()) {
                    spinnerAmount.getEditor().setText(oldValue);
                }
            }
        });
        comboBoxItems.setItems(FXCollections.observableArrayList(shopItems));
        if (objects.length == 0) {
            labelInfo.setText("Создание сущности");
        } else {
            activeEntity = (ShopEntity) objects[0];
            labelInfo.setText("Редактирование сущности " + activeEntity.getId());
            comboBoxItems.getSelectionModel().select(shopItems.indexOf(activeEntity.getShopItem()));
            spinnerAmount.getValueFactory().setValue(activeEntity.getAmount());
        }
    }

    @SafeVarargs
    @Override
    public final <T> void onNotify(T... objects) {
        String command = (String) objects[0];
        if ("Update".equals(command)) {
            shopItems = Database.getShopItemsDb().select(null);
            comboBoxItems.setItems(FXCollections.observableArrayList(shopItems));
            if(activeEntity != null) {
                comboBoxItems.getSelectionModel().select(shopItems.indexOf(activeEntity.getShopItem()));
            }
        }
    }

    public void onButtonEditItemClick(ActionEvent actionEvent) {
        int selectedIndex = comboBoxItems.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            FXMLHelper.alertAndWait("Error", "Item is not selected", "Please select an item to edit!");
            return;
        }
        ShopItemScreenController shopItemScreenController = FXMLHelper.loadScreenReturnController("ShopItemScreen");
        ShopItem item = comboBoxItems.getItems().get(selectedIndex);
        shopItemScreenController.preload(item);
    }

    public void onButtonCreateItemClick(ActionEvent actionEvent) {
        ShopItemScreenController shopItemScreenController = FXMLHelper.loadScreenReturnController("ShopItemScreen");
        shopItemScreenController.preload();
    }

    public void onButtonSaveClick(ActionEvent actionEvent) {
        int selectedIndex = comboBoxItems.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            FXMLHelper.alertAndWait("Error", "Item is not selected", "Please select an item to save an entity!");
            return;
        }
        ShopItem item = comboBoxItems.getItems().get(selectedIndex);
        int amount = spinnerAmount.getValue();

        if (activeEntity == null) {
            ShopEntity entity = new ShopEntity(-1, item.getId(), amount).push();
            ShopStorage mainStorage = Database.getShopStoragesDb().getMainStorage();
            mainStorage.getEntities_ids().add(entity.getId());
            mainStorage.push();
        } else {
            activeEntity.setShopItem_id(item.getId());
            activeEntity.setAmount(amount);
            activeEntity.push();
        }

        FXMLHelper.alertAndWait("Success", "Operation succeeded", "Entity was saved!");

        FXMLHelper.backScreen();
    }

    public void onButtonBackClick(ActionEvent actionEvent) {
        FXMLHelper.backScreen();
    }
}
