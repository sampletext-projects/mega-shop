package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class FXMLHelper {
    private static final String pathToScreens = "../FXML/";
    private static final Stack<Scene> chronologicalScenes = new Stack<>();
    private static Stage primaryStage;

    public static void alertAndWait(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    private static void createScreen(Parent root) {
        chronologicalScenes.push(primaryStage.getScene());
        Scene scene = new Scene(root);
        setScene(scene);
    }

    public static void loadScreen(String screen) {
        try {
            Parent root = FXMLLoader.load(FXMLHelper.class.getResource(pathToScreens + screen + ".fxml"));
            createScreen(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T loadScreenReturnController(String screen) {
        T controller = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FXMLHelper.class.getResource(pathToScreens + screen + ".fxml"));
            loader.load();
            Parent root = loader.getRoot();
            controller = loader.getController();
            root.setUserData(controller);
            createScreen(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

    public static void backScreen() {
        if (chronologicalScenes.size() > 1) {
            Scene pop = chronologicalScenes.pop();
            Object userData = pop.getRoot().getUserData();
            if (userData instanceof NotifiableController) {
                NotifiableController controller = (NotifiableController) userData;
                controller.onNotify("Update");
            }
            setScene(pop);
        }
    }

    private static void setScene(Scene scene) {
        primaryStage.setWidth(0);
        primaryStage.setHeight(0);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setX(Screen.getPrimary().getBounds().getWidth() / 2 - scene.getWidth() / 2);
        primaryStage.setY(Screen.getPrimary().getBounds().getHeight() / 2 - scene.getHeight() / 2);
    }

    public interface PreloadableController {
        <T> void preload(T... objects);
    }

    public interface NotifiableController {
        <T> void onNotify(T... objects);
    }
}
