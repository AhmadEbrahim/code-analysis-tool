package AnalyzerPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class MainMenu extends Application {

    public static Popup popup = new Popup();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("mainMenu.fxml"));
        primaryStage.setTitle("EVAS Framework");
        primaryStage.setScene(new Scene(root, 780, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
