package AnalyzerPackage;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

//This class draws the class diagram.
public class ClassDiagram extends Application {
    int pos = 0;
    Analyzer analyzer;

    @Override
    public void start(Stage primaryStage) throws Exception {

        analyzer.generateClassesArray();
        showDiagram(analyzer.classes);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static double sceneX, sceneY, layoutX, layoutY;
    public static Pane pane;

    private static StackPane buildClass(String color, Class input) {
        ArrayList<Function> functions = input.getFunctions();
        ArrayList<Variable> variables = input.getVariables();
        int max = 0;
        for (int i = 0; i < functions.size(); i++) {
            if (max < functions.get(i).getFnName().length() + functions.get(i).getDataType().length()) {
                max = functions.get(i).getFnName().length() + functions.get(i).getDataType().length();
            }
        }
        for (int i = 0; i < variables.size(); i++) {
            if (max < variables.get(i).getVarName().length() + variables.get(i).getDataType().length()) {
                max = variables.get(i).getVarName().length() + variables.get(i).getDataType().length();
            }
        }
        double paneSize = max * 7;
        StackPane dotPane = new StackPane();

        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(paneSize);
        rectangle.setHeight((functions.size() + variables.size() + 1) * 20);
        rectangle.setStyle("-fx-fill:" + color + ";-fx-stroke-width:2px;-fx-stroke:black;");
        rectangle.setTranslateY((functions.size() + variables.size() - 2) * 10);
        dotPane.getChildren().add(rectangle);

        rectangle = new Rectangle();
        rectangle.setWidth(paneSize - 1);
        rectangle.setHeight((variables.size()) * 19);
        rectangle.setStyle("-fx-fill:" + color + ";-fx-stroke-width:1px;-fx-stroke:black;");
        rectangle.setTranslateY((variables.size() - 1) * 10);
        dotPane.getChildren().add(rectangle);

        Label txt = new Label(input.getName());
        txt.setTranslateY(-20);
        txt.setStyle("-fx-font-size:18px;-fx-font-weight:bold;");
        dotPane.getChildren().add(txt);

        for (int i = 0; i < variables.size(); i++) {
            txt = new Label(variables.get(i).getVarName() + " : " + variables.get(i).getDataType());
            txt.setTranslateY(19 * i);
            dotPane.getChildren().add(txt);
        }
        for (int i = 0; i < functions.size(); i++) {
            txt = new Label(functions.get(i).getFnName() + " : " + functions.get(i).getDataType());
            txt.setTranslateY(19 * (i + variables.size()));
            dotPane.getChildren().add(txt);
        }
        dotPane.setPrefSize(paneSize, paneSize);
        dotPane.setMaxSize(paneSize, paneSize);
        dotPane.setMinSize(paneSize, paneSize);
        dotPane.setOnMousePressed(e -> {
            sceneX = e.getSceneX();
            sceneY = e.getSceneY();
            layoutX = dotPane.getLayoutX();
            layoutY = dotPane.getLayoutY();
        });

        EventHandler<MouseEvent> dotOnMouseDraggedEventHandler = e -> {
            // Offset of drag
            double offsetX = e.getSceneX() - sceneX;
            double offsetY = e.getSceneY() - sceneY;

            // Taking parent bounds
            Bounds parentBounds = dotPane.getParent().getLayoutBounds();

            // Drag node bounds
            double currPaneLayoutX = dotPane.getLayoutX();
            double currPaneWidth = dotPane.getWidth();
            double currPaneLayoutY = dotPane.getLayoutY();
            double currPaneHeight = dotPane.getHeight();

            if ((currPaneLayoutX + offsetX < parentBounds.getWidth() - currPaneWidth) && (currPaneLayoutX + offsetX > -1)) {
                // If the dragNode bounds is within the parent bounds, then you can set the offset value.
                dotPane.setTranslateX(offsetX);
            } else if (currPaneLayoutX + offsetX < 0) {
                // If the sum of your offset and current layout position is negative, then you ALWAYS update your translate to negative layout value
                // which makes the final layout position to 0 in mouse released event.
                dotPane.setTranslateX(-currPaneLayoutX);
            } else {
                // If your dragNode bounds are outside parent bounds,ALWAYS setting the translate value that fits your node at end.
                dotPane.setTranslateX(parentBounds.getWidth() - currPaneLayoutX - currPaneWidth);
            }

            if ((currPaneLayoutY + offsetY < parentBounds.getHeight() - currPaneHeight) && (currPaneLayoutY + offsetY > -1)) {
                dotPane.setTranslateY(offsetY);
            } else if (currPaneLayoutY + offsetY < 0) {
                dotPane.setTranslateY(-currPaneLayoutY);
            } else {
                dotPane.setTranslateY(parentBounds.getHeight() - currPaneLayoutY - currPaneHeight);
            }
        };
        dotPane.setOnMouseDragged(dotOnMouseDraggedEventHandler);
        dotPane.setOnMouseReleased(e -> {
            // Updating the new layout positions
            dotPane.setLayoutX(layoutX + dotPane.getTranslateX());
            dotPane.setLayoutY(layoutY + dotPane.getTranslateY());

            // Resetting the translate positions
            dotPane.setTranslateX(0);
            dotPane.setTranslateY(0);
        });
        return dotPane;
    }

    //This function builds the implemented classes line which is dashed.
    private static void buildImplementsLine(StackPane startClass, StackPane endClass, Pane parent, boolean hasEndArrow, boolean hasStartArrow) {
        Line line = getLine(startClass, endClass);
        line.getStrokeDashArray().add(10D);
        StackPane arrowAB = getArrow(true, line, startClass, endClass, false);
        if (!hasEndArrow) {
            arrowAB.setOpacity(0);
        }
        StackPane arrowBA = getArrow(false, line, startClass, endClass, false);
        if (!hasStartArrow) {
            arrowBA.setOpacity(0);
        }
        parent.getChildren().addAll(line, arrowBA, arrowAB);
    }

    //This function builds the extended classes line which is solid.
    private static void buildExtendsLine(StackPane startClass, StackPane endClass, Pane parent, boolean hasEndArrow, boolean hasStartArrow) {
        Line line = getLine(startClass, endClass);
        StackPane arrowAB = getArrow(true, line, startClass, endClass, false);
        if (!hasEndArrow) {
            arrowAB.setOpacity(0);
        }
        StackPane arrowBA = getArrow(false, line, startClass, endClass, false);
        if (!hasStartArrow) {
            arrowBA.setOpacity(0);
        }
        parent.getChildren().addAll(line, arrowBA, arrowAB);
    }

    //This function builds the associated classes lines which has a different arrow.
    private static void buildAssociateLine(StackPane startClass, StackPane endClass, Pane parent, boolean hasEndArrow, boolean hasStartArrow) {
        Line line = getLine(startClass, endClass);
        StackPane arrowAB = getArrow(true, line, startClass, endClass, true);
        if (!hasEndArrow) {
            arrowAB.setOpacity(0);
        }
        StackPane arrowBA = getArrow(false, line, startClass, endClass, true);
        if (!hasStartArrow) {
            arrowBA.setOpacity(0);
        }
        parent.getChildren().addAll(line, arrowBA, arrowAB);
    }

    private static Line getLine(StackPane startClass, StackPane endClass) {
        Line line = new Line();
        line.setStroke(Color.LIGHTBLUE);
        line.setStrokeWidth(2);
        line.startXProperty().bind(startClass.layoutXProperty().add(startClass.translateXProperty()).add(startClass.widthProperty().divide(2)));
        line.startYProperty().bind(startClass.layoutYProperty().add(startClass.translateYProperty()).add(startClass.heightProperty().divide(2)));
        line.endXProperty().bind(endClass.layoutXProperty().add(endClass.translateXProperty()).add(endClass.widthProperty().divide(2)));
        line.endYProperty().bind(endClass.layoutYProperty().add(endClass.translateYProperty()).add(endClass.heightProperty().divide(2)));
        return line;
    }

    private static StackPane getArrow(boolean toLineEnd, Line line, StackPane startClass, StackPane endClass, boolean isFilled) {
        double size = 12; // Arrow size
        StackPane arrow = new StackPane();
        if (isFilled) {
            arrow.setStyle("-fx-background-color:Black;-fx-border-width:1px;-fx-border-color:black;-fx-shape: \"M0,-4L4,0L0,4Z\"");//
        } else {
            arrow.setStyle("-fx-background-color:Transparent;-fx-border-width:1px;-fx-border-color:black;-fx-shape: \"M0,-4L4,0L0,4Z\"");//
        }
        arrow.setPrefSize(size, size);
        arrow.setMaxSize(size, size);
        arrow.setMinSize(size, size);

        // Determining the arrow visibility unless there is enough space between dots.
        DoubleBinding xDiff = line.endXProperty().subtract(line.startXProperty());
        DoubleBinding yDiff = line.endYProperty().subtract(line.startYProperty());
        BooleanBinding visible = (xDiff.lessThanOrEqualTo(size).and(xDiff.greaterThanOrEqualTo(-size)).and(yDiff.greaterThanOrEqualTo(-size)).and(yDiff.lessThanOrEqualTo(size))).not();
        arrow.visibleProperty().bind(visible);

        // Determining the x point on the line which is at a certain distance.
        DoubleBinding tX = Bindings.createDoubleBinding(() -> {
            double xDiffSqu = (line.getEndX() - line.getStartX()) * (line.getEndX() - line.getStartX());
            double yDiffSqu = (line.getEndY() - line.getStartY()) * (line.getEndY() - line.getStartY());
            double lineLength = Math.sqrt(xDiffSqu + yDiffSqu);
            double dt;
            if (toLineEnd) {
                // When determining the point towards end, the required distance is total length minus (radius + arrow half width)
                dt = lineLength - (endClass.getWidth() / 2) - (arrow.getWidth() / 2);
            } else {
                // When determining the point towards start, the required distance is just (radius + arrow half width)
                dt = (startClass.getWidth() / 2) + (arrow.getWidth() / 2);
            }

            double t = dt / lineLength;
            double dx = ((1 - t) * line.getStartX()) + (t * line.getEndX());
            return dx;
        }, line.startXProperty(), line.endXProperty(), line.startYProperty(), line.endYProperty());

        // Determining the y point on the line which is at a certain distance.
        DoubleBinding tY = Bindings.createDoubleBinding(() -> {
            double xDiffSqu = (line.getEndX() - line.getStartX()) * (line.getEndX() - line.getStartX());
            double yDiffSqu = (line.getEndY() - line.getStartY()) * (line.getEndY() - line.getStartY());
            double lineLength = Math.sqrt(xDiffSqu + yDiffSqu);
            double dt;
            if (toLineEnd) {
                dt = lineLength - (endClass.getHeight() / 2) - (arrow.getHeight() / 2);
            } else {
                dt = (startClass.getHeight() / 2) + (arrow.getHeight() / 2);
            }
            double t = dt / lineLength;
            double dy = ((1 - t) * line.getStartY()) + (t * line.getEndY());
            return dy;
        }, line.startXProperty(), line.endXProperty(), line.startYProperty(), line.endYProperty());

        arrow.layoutXProperty().bind(tX.subtract(arrow.widthProperty().divide(2)));
        arrow.layoutYProperty().bind(tY.subtract(arrow.heightProperty().divide(2)));

        DoubleBinding endArrowAngle = Bindings.createDoubleBinding(() -> {
            double stX = toLineEnd ? line.getStartX() : line.getEndX();
            double stY = toLineEnd ? line.getStartY() : line.getEndY();
            double enX = toLineEnd ? line.getEndX() : line.getStartX();
            double enY = toLineEnd ? line.getEndY() : line.getStartY();
            double angle = Math.toDegrees(Math.atan2(enY - stY, enX - stX));
            if (angle < 0) {
                angle += 360;
            }
            return angle;
        }, line.startXProperty(), line.endXProperty(), line.startYProperty(), line.endYProperty());
        arrow.rotateProperty().bind(endArrowAngle);

        return arrow;
    }

    public static void showDiagram(ArrayList<Class> input) {
        try {
            ArrayList<StackPane> dots = new ArrayList<>();
            pane = new Pane();


            for (int i = 0; i < input.size(); i++) {
                StackPane x = buildClass("White", input.get(i));
                dots.add(x);

            }
            for (int i = 0; i < input.size(); i++) {
                for (int j = 0; j < input.size(); j++) {
                    for (int k = 0; k < input.get(i).getVariables().size(); k++) {
                        String temp = input.get(i).getVariables().get(k).getDataType();
                        if (temp.equals(input.get(j).getName())) {
                            buildAssociateLine(dots.get(i), dots.get(j), pane, true, false);
                        }
                    }
                }
                if (input.get(i).getImplements_() != null) {
                    for (int j = 0; j < input.size(); j++) {
                        if (input.get(j).getName().equals(input.get(i).getImplements_())) {
                            buildImplementsLine(dots.get(i), dots.get(j), pane, true, false);
                        }
                    }
                }
                if (input.get(i).getExtends_() != null) {
                    for (int j = 0; j < input.size(); j++) {
                        if (input.get(j).getName().equals(input.get(i).getExtends_())) {
                            buildExtendsLine(dots.get(i), dots.get(j), pane, true, false);
                        }
                    }
                }
            }
            //buildDashedLine(dots.get(0), dots.get(1), pane, true, false);
            for (int i = 0; i < dots.size(); i++) {
                pane.getChildren().add(dots.get(i));
            }
            StackPane root = new StackPane();
            root.setPadding(new Insets(20));
            Stage stage = new Stage();
            root.getChildren().add(pane);
            Scene sc = new Scene(root, 1200, 700);
            stage.setScene(sc);
            stage.setTitle("EVAS Framework - Class Diagram");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}