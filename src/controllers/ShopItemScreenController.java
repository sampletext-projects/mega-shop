package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import models.ShopItem;
import utils.FXMLHelper;

public class ShopItemScreenController implements FXMLHelper.PreloadableController {

    private ShopItem activeItem;
    @FXML
    private Label labelInfo;
    @FXML
    private TextField textFieldBrand;
    @FXML
    private TextField textFieldModel;
    @FXML
    private TextField textFieldType;
    @FXML
    private Spinner<Double> spinnerPrice;

    @SafeVarargs
    @Override
    public final <T> void preload(T... objects) {
        spinnerPrice.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                spinnerPrice.getValueFactory().setValue(Double.parseDouble(newValue));
            } catch (NumberFormatException ignored) {
                if (!oldValue.isEmpty()) {
                    spinnerPrice.getEditor().setText(oldValue);
                }
            }
        });

        if (objects.length == 0) {
            labelInfo.setText("Создание элемента");
        } else {
            activeItem = (ShopItem) objects[0];
            labelInfo.setText("Редактирование элемента " + activeItem.getId());
            textFieldBrand.setText(activeItem.getBrand());
            textFieldModel.setText(activeItem.getModel());
            textFieldType.setText(activeItem.getType());
            spinnerPrice.getValueFactory().setValue((double) activeItem.getPrice());
        }
    }

    public void onButtonSaveClick(ActionEvent actionEvent) {
        String brand = textFieldBrand.getText();
        String model = textFieldModel.getText();
        String type = textFieldType.getText();
        float price = spinnerPrice.getValue().floatValue();

        if (brand.trim().length() == 0 || model.trim().length() == 0 || type.trim().length() == 0) {
            FXMLHelper.alertAndWait("Ошибка", "Бренд, модель или тип не заполнены", "Пожалуйста, заполните все поля!");
            return;
        }

        if (activeItem == null) {
            ShopItem item = new ShopItem(-1, brand, model, type, price).push();
        } else {
            activeItem.setBrand(brand);
            activeItem.setModel(model);
            activeItem.setType(type);
            activeItem.setPrice(price);
            activeItem.push();
        }

        FXMLHelper.alertAndWait("Успех", "Операция выполнена", "Товар сохранён!");

        FXMLHelper.backScreen();
    }

    public void onButtonBackClick(ActionEvent actionEvent) {
        FXMLHelper.backScreen();
    }
}
