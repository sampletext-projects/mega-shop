package controllers;

import database.Database;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.User;
import utils.FXMLHelper;

public class LoginScreenController {
    public TextField loginTextField;
    public PasswordField passwordTextField;

    public void onButtonLogInClick(ActionEvent actionEvent) {
        String login = loginTextField.getText();
        String password = passwordTextField.getText();

        if (login.trim().length() == 0 || password.trim().length() == 0) {
            FXMLHelper.alertAndWait("Ошибка", "Логин или пароль не заполнены", "Пожалуйста, заполните все поля!");
            return;
        }

        User loginedUser = Database.getUsersDb().selectByLoginAndPassword(login, password);

        if (loginedUser == null) {
            FXMLHelper.alertAndWait("Ошибка", "Пользователь не найден", "Пожалуйста, проверьте все поля!");
            return;
        }

        User.activeUser = loginedUser;
        switch (loginedUser.getPosition()) {
            case "Админ":
                AdminScreenController adminScreenController = FXMLHelper.loadScreenReturnController("AdminScreen");
                adminScreenController.preload();
                break;
            case "Пользователь":
                UserScreenController userScreenController = FXMLHelper.loadScreenReturnController("UserScreen");
                userScreenController.preload();
                break;
            case "Продавец":
                StorekeeperScreenController storeKeeperScreenController = FXMLHelper.loadScreenReturnController("StorekeeperScreen");
                storeKeeperScreenController.preload();
                break;
        }
    }

    public void onButtonRegistrationCLick(ActionEvent actionEvent) {
        FXMLHelper.loadScreen("RegistrationScreen");
    }
}
