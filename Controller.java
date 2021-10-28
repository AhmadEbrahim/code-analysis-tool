package AnalyzerPackage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    TextArea outputTextArea;
    @FXML
    TextArea slicerTextArea;
    @FXML
    TextField variableNameTextField;
    @FXML
    TextField endLineTextField;
    @FXML
    TextField variableValueTextField;
    @FXML
    Label statusLabel;

    static String folderPath;
    static ArrayList<String> filePaths = new ArrayList<>();
    static Analyzer analyzer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        folderPath = "";
    }

    public void browseFiles() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        List<File> list = fileChooser.showOpenMultipleDialog(stage);
        filePaths.clear();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPath().indexOf(".java") > 0 || list.get(i).getPath().indexOf(".txt") > 0) {
                filePaths.add(list.get(i).getPath());
            } else {
                AlertClass alert = new AlertClass();
                alert.display("Invalid Input!", "You can only open .java or .txt files!");
            }
        }
        if (filePaths.size() > 0) {
            statusLabel.setText("Status: " + filePaths.size() + " Files Loaded");
        }
    }

    public void browseFolder() {
        Stage stage = new Stage();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Resource Folder");
        File folder = directoryChooser.showDialog(stage);
        folderPath = folder.getPath();
    }

    public void showAnalyzer() {
        try {
            if (filePaths.size() > 0) {
                analyzer = new Analyzer(filePaths);
                AnalyzerMenu pl = new AnalyzerMenu();
                var root = new StackPane();
                root.setPadding(new Insets(20));
                Stage stage = new Stage();
                pl.pane = new Pane();
                root.getChildren().add(pl.pane);
                Scene sc = new Scene(root, 1200, 700);
                stage.setScene(sc);
                stage.setTitle("Analyzer");
                pl.start(stage);
            } else {
                AlertClass alert = new AlertClass();
                alert.display("Invalid Input!", "You need to browse files first!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showVisualizer() {
        try {
            if (filePaths.size() > 0) {
                analyzer = new Analyzer(filePaths);
                VisualizerMenu pl = new VisualizerMenu();
                var root = new StackPane();
                root.setPadding(new Insets(20));
                Stage stage = new Stage();
                pl.pane = new Pane();
                root.getChildren().add(pl.pane);
                Scene sc = new Scene(root, 1200, 700);
                stage.setScene(sc);
                stage.setTitle("Visualizer");
                pl.start(stage);
            } else {
                AlertClass alert = new AlertClass();
                alert.display("Invalid Input!", "You need to browse files first!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNumberOfLines() {
        String numberOfLines = String.valueOf(analyzer.getNumberOfLines());
        outputTextArea.setText("Number of Lines:\n\t" + numberOfLines);
    }

    public void showNumberOfClasses() {
        String numberOfClasses = String.valueOf(analyzer.getNumberOfClasses());
        outputTextArea.setText("Number of Classes:\n\t" + numberOfClasses);
    }

    public void showNumberOfFunctions() {
        String numberOfFunctions = String.valueOf(analyzer.getNumberOfFunctions());
        outputTextArea.setText("Number of Functions:\n\t" + numberOfFunctions);
    }

    public void showNamesOfClasses() {
        ArrayList<String> classesNames = analyzer.getClassesNames();
        String classes = "";
        for (String s : classesNames) {
            classes += s + "\n\t";
        }
        outputTextArea.setText("Classes Names:\n\t" + classes);
    }

    public void showNamesOfFunctions() {
        ArrayList<String> functionsNames = analyzer.getFunctionNames();
        String functions = "";
        for (String s : functionsNames) {
            functions += s + "\n\t";
        }
        outputTextArea.setText("Functions Names:\n\t" + functions);
    }

    public void showCyclomaticComplexity() {
        String cyclomaticComplexity = String.valueOf(analyzer.getCyclomaticComplexity());
        outputTextArea.setText("Cyclomatic Complexity:\n\t" + cyclomaticComplexity);
    }

    public void showMaintainabilityIndex() {
        String maintainabilityIndex = String.valueOf(analyzer.getMaintainabilityIndex());
        outputTextArea.setText("Maintainability Index:\n\t" + maintainabilityIndex);
    }

    public void showSequenceDiagram() {
        try {
//            boolean mainFound = false;
//            for (int i = 0; i < filePaths.size(); i++) {
//                if (filePaths.get(i).contains("Main.java") || filePaths.get(i).contains("main.java") || filePaths.get(i).contains("main.txt") || filePaths.get(i).contains("Main.txt")) {
//                    mainFound = true;
//                    break;
//                }
//            }
//            if (mainFound) {
                SequenceDiagram pl = new SequenceDiagram();
                pl.analyzer = analyzer;
                StackPane root = new StackPane();
                root.setPadding(new Insets(20));
                Stage stage = new Stage();
                pl.pane = new Pane();
                root.getChildren().add(pl.pane);
                Scene sc = new Scene(root, 1200, 700);
                stage.setScene(sc);
                stage.setTitle("Sequence Diagram");
                pl.start(stage);
//            } else {
//                AlertClass alert = new AlertClass();
//                alert.display("Invalid Input!", "The sequence diagram works only on main file!");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showClassDiagram() {
        try {
            ClassDiagram pl = new ClassDiagram();
            pl.analyzer = analyzer;
            StackPane root = new StackPane();
            root.setPadding(new Insets(20));
            Stage stage = new Stage();
            pl.pane = new Pane();
            root.getChildren().add(pl.pane);
            Scene sc = new Scene(root, 1200, 700);
            stage.setScene(sc);
            stage.setTitle("Class Diagram");
            pl.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showFlowChart() {
        try {
//            boolean mainFound = false;
//            for (int i = 0; i < filePaths.size(); i++) {
//                if (filePaths.get(i).contains("Main.java") || filePaths.get(i).contains("main.java") || filePaths.get(i).contains("main.txt") || filePaths.get(i).contains("Main.txt")) {
//                    mainFound = true;
//                    break;
//                }
//            }
//            if (mainFound) {
                FlowChart pl = new FlowChart();
                pl.analyzer = analyzer;
                StackPane root = new StackPane();
                root.setPadding(new Insets(20));
                Stage stage = new Stage();
                pl.pane = new Pane();
                root.getChildren().add(pl.pane);
                Scene sc = new Scene(root, 1200, 700);
                stage.setScene(sc);
                stage.setTitle("Flow Chart");
                pl.start(stage);
//            } else {
//               AlertClass alert = new AlertClass();
//                alert.display("Invalid Input!", "The flow chart works only on main file!");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSlicer() {
        try {
            if (filePaths.size() > 0) {
                if (filePaths.size() == 1) {
                    analyzer = new Analyzer(filePaths);
                    SlicerMenu pl = new SlicerMenu();
                    var root = new StackPane();
                    root.setPadding(new Insets(20));
                    Stage stage = new Stage();
                    pl.pane = new Pane();
                    root.getChildren().add(pl.pane);
                    Scene sc = new Scene(root, 1200, 700);
                    stage.setScene(sc);
                    stage.setTitle("Slicer");
                    pl.start(stage);
                } else {
                    AlertClass alert = new AlertClass();
                    alert.display("Invalid Input!", "You can only open ONE .java or .txt FILE ONLY!");
                }
            } else {
                AlertClass alert = new AlertClass();
                alert.display("Invalid Input!", "You need to browse files first!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showStaticSlicer() {
        Slicer slicer;
        if (filePaths.size() == 1) {
            slicer = new Slicer(filePaths);
            String variableName = variableNameTextField.getText();
            try {
                int endLine;
                if (variableName.length() > 0 && endLineTextField.getText().length() > 0) {
                    endLine = Integer.parseInt(endLineTextField.getText());
                    if (endLine > slicer.getLinesCount()) {
                        AlertClass alert = new AlertClass();
                        alert.display("Invalid Input!", "The number of lines of the file is less than the end line you entered!");
                    } else if (endLine <= 0) {
                        AlertClass alert = new AlertClass();
                        alert.display("Invalid Input!", "End line value must be positive number!");
                    } else if (!slicer.staticSlicing(endLine, variableName).contains(variableName)) {
                        AlertClass alert = new AlertClass();
                        alert.display("Invalid Input!", "The variable you entered was not found in the code!");
                    } else {
                        slicerTextArea.setText(slicer.staticSlicing(endLine, variableName));
                    }
                } else {
                    AlertClass alert = new AlertClass();
                    alert.display("Invalid Input!", "Please fill in the variable name and end line fields!");
                }
            } catch (NumberFormatException e) {
                AlertClass alert = new AlertClass();
                alert.display("Invalid Input!", "The field end line only accept integer values!");
            }
        } else {
            AlertClass alert = new AlertClass();
            alert.display("Invalid Input!", "You can only open ONE .java or .txt FILE ONLY!");
        }

    }

    public void showDynamicSlicer() {
        Slicer slicer;
        if (filePaths.size() == 1) {
            slicer = new Slicer(filePaths);
            String variableName = variableNameTextField.getText();
            try {
                int variableValue;
                int endLine;
                if (variableName.length() > 0 && variableValueTextField.getText().length() > 0 && endLineTextField.getText().length() > 0) {
                    endLine = Integer.parseInt(endLineTextField.getText());
                    variableValue = Integer.parseInt(variableValueTextField.getText());
                    if (endLine > slicer.getLinesCount()) {
                        AlertClass alert = new AlertClass();
                        alert.display("Invalid Input!", "The number of lines of the file is less than the end line you entered!");
                    } else if (endLine < 0) {
                        AlertClass alert = new AlertClass();
                        alert.display("Invalid Input!", "End line value must be positive number!");
                    } else if (!slicer.dynamicSlicing(endLine, variableName, variableValue).contains(variableName)) {
                        AlertClass alert = new AlertClass();
                        alert.display("Invalid Input!", "The variable you entered was not found in the code!");
                    } else {
                        slicerTextArea.setText(slicer.dynamicSlicing(endLine, variableName, variableValue));
                    }
                } else {
                    AlertClass alert = new AlertClass();
                    alert.display("Invalid Input!", "Please fill in the variable name, end line and variable value fields!");
                }
            } catch (NumberFormatException e) {
                AlertClass alert = new AlertClass();
                alert.display("Invalid Input!", "The fields variable value and end line only accept integer values!");
            }
        } else {
            AlertClass alert = new AlertClass();
            alert.display("Invalid Input!", "You can only open ONE .java or .txt FILE ONLY!");
        }
    }
}