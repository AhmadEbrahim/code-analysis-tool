package AnalyzerPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class SlicerMenu extends Application {

    public static Pane pane;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("slicer.fxml"));
        primaryStage.setTitle("EVAS Framework - Slicer");
        primaryStage.setScene(new Scene(root, 1080, 650));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
