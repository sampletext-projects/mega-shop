import javafx.application.Application;
import javafx.stage.Stage;
import utils.FXMLHelper;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.show();
        primaryStage.setWidth(0);
        primaryStage.setHeight(0);
        FXMLHelper.setPrimaryStage(primaryStage);
        FXMLHelper.loadScreen("LoginScreen");
    }
}
