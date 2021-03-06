package controllers;

import database.Database;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.Cart;
import models.ShopStorage;
import models.User;
import utils.FXMLHelper;

import java.util.ArrayList;

public class RegistrationScreenController {
    public TextField nameTextField;
    public TextField loginTextField;
    public PasswordField passwordTextField;
    public ComboBox<String> positionComboBox;

    public void onButtonRegistrationClick(ActionEvent actionEvent) {
        String name = nameTextField.getText();
        String login = loginTextField.getText();
        String password = passwordTextField.getText();
        String position;
        if (!positionComboBox.getSelectionModel().isEmpty()) {
            position = positionComboBox.getItems().get(positionComboBox.getSelectionModel().getSelectedIndex());
        } else {
            FXMLHelper.alertAndWait("Ошибка", "Позиция не выбрана", "Пожалуйста, выберите позицию!");
            return;
        }

        if (login.trim().length() == 0 || password.trim().length() == 0 || name.trim().length() == 0) {
            FXMLHelper.alertAndWait("Ошибка", "Логин, пароль или имя не заполнены", "Пожалуйста, заполните все поля!");
            return;
        }

        User user = new User(-1, login, password, position, name).push();
        if ("Пользователь".equals(user.getPosition())) {
            ShopStorage cartStorage = new ShopStorage(-1, new ArrayList<>()).push();
            Cart cart = new Cart(-1, user.getId(), cartStorage.getId()).push();
        }

        FXMLHelper.loadScreen("LoginScreen");
    }
}
