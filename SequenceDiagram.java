package AnalyzerPackage;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Deque;

//This class draws the sequence diagram by using the logic handed over by the analyzer class after using the function get sequence.
public class SequenceDiagram extends Application {
    int pos = 0;
    Analyzer analyzer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        analyzer.generateClassesArray();
        analyzer.getTimelines().add("Main");
        analyzer.getSequence(analyzer.getMainFunction(), "Main");

        showDiagram(analyzer.classes, analyzer.getTimelines(), analyzer.getFunctionCalls());
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Pane pane;

    private static Line buildTimeline(Pane parent, int position, String className, int numOfFunctions) {
        Line line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);
        line.setStartX(position);
        line.setStartY(20);
        line.setEndX(position);
        line.setEndY(20 + 20 * numOfFunctions);

        // ADD RECTANGLE HERE!!

        Label txt = new Label(className);
        txt.setLayoutX(position);
        txt.setLayoutY(0);
        txt.setStyle("-fx-font-size:18px;-fx-font-weight:bold;");
        parent.getChildren().addAll(line, txt);
        return line;
    }

    private static void buildForwardLine(Line startTimeline, Line endTimeline, Pane parent, int position, String fnName) {
        Line line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);
        line.setStartX(startTimeline.getStartX());
        line.setStartY(position);
        line.setEndX(endTimeline.getStartX());
        line.setEndY(position);
        StackPane arrowAB = getArrow(line, true);
        Label txt = new Label(fnName);
        txt.setLayoutX(startTimeline.getStartX() + 5);
        txt.setLayoutY(position - 20);
        parent.getChildren().addAll(line, arrowAB, txt);
    }

    private static void buildBackwardLine(Line startTimeline, Line endTimeline, Pane parent, int position, String fnName) {
        Line line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);
        line.setStartX(startTimeline.getStartX());
        line.setStartY(position);
        line.setEndX(endTimeline.getStartX());
        line.setEndY(position);
        line.getStrokeDashArray().add(10D);
        StackPane arrowBA = getArrow(line, false);
        Label txt = new Label(fnName);
        txt.setLayoutX(startTimeline.getStartX() + 5);
        txt.setLayoutY(position - 20);
        parent.getChildren().addAll(line, arrowBA, txt);
    }

    private static void buidLoopLine(Line startTimeline, Pane parent, int position, String fnName) {
        Line line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);
        line.setStartX(startTimeline.getStartX());
        line.setStartY(position);
        line.setEndX(startTimeline.getStartX() + 80);
        line.setEndY(position);

        Line line2 = new Line();
        line2.setStroke(Color.BLACK);
        line2.setStrokeWidth(2);
        line2.setStartX(startTimeline.getStartX() + 80);
        line2.setStartY(position);
        line2.setEndX(startTimeline.getStartX() + 80);
        line2.setEndY(position + 10);

        Line line3 = new Line();
        line3.setStroke(Color.BLACK);
        line3.setStrokeWidth(2);
        line3.setStartX(startTimeline.getStartX() + 80);
        line3.setStartY(position + 10);
        line3.setEndX(startTimeline.getStartX());
        line3.setEndY(position + 10);
        StackPane arrowBA = getArrow(line3, true);
        Label txt = new Label(fnName);
        txt.setLayoutX(startTimeline.getStartX() + 5);
        txt.setLayoutY(position - 20);
        parent.getChildren().addAll(line, line2, line3, arrowBA, txt);
    }

    private static Line getLine(StackPane startTimeline, StackPane endTimeline) {
        Line line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);
        line.startXProperty().bind(startTimeline.layoutXProperty().add(startTimeline.translateXProperty()).add(startTimeline.widthProperty().divide(2)));
        line.startYProperty().bind(startTimeline.layoutYProperty().add(startTimeline.translateYProperty()).add(startTimeline.heightProperty().divide(2)));
        line.endXProperty().bind(endTimeline.layoutXProperty().add(endTimeline.translateXProperty()).add(endTimeline.widthProperty().divide(2)));
        line.endYProperty().bind(endTimeline.layoutYProperty().add(endTimeline.translateYProperty()).add(endTimeline.heightProperty().divide(2)));
        return line;
    }

    private static StackPane getArrow(Line line, boolean toLineEnd) {
        double size = 12; // Arrow size
        StackPane arrow = new StackPane();
        arrow.setStyle("-fx-background-color:Black;-fx-border-width:1px;-fx-border-color:black;-fx-shape: \"M0,-4L4,0L0,4Z\"");
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
                dt = lineLength - 5 - (arrow.getWidth() / 2);
            } else {
                // When determining the point towards start, the required distance is just (radius + arrow half width)
                dt = (arrow.getWidth() / 2) + 5;
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
                // When determining the point towards end, the required distance is total length minus (radius + arrow half width)
                dt = lineLength - 5 - (arrow.getWidth() / 2);
            } else {
                // When determining the point towards start, the required distance is just (radius + arrow half width)
                dt = (arrow.getWidth() / 2) + 5;
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

    public static void showDiagram(ArrayList<Class> input, ArrayList<String> timelines, Deque<FunctionCall> functionCalls) {
        try {
            pane = new Pane();
            ArrayList<Line> lines = new ArrayList<>();
            int max = 0;
            for (int i = 0; i < functionCalls.size(); i++) {
                if (max < functionCalls.getFirst().functionName.length()) {
                    max = functionCalls.getFirst().functionName.length();
                }
            }
            for (int i = 0; i < timelines.size(); i++) {
                Line line = buildTimeline(pane, 20 + 10 * max * i, timelines.get(i), functionCalls.size());
                lines.add(line);
            }
            int pos = 0;
            for (int i = 0; i < functionCalls.size(); i++) {
                if (functionCalls.getFirst().indexInSequenceFrom > functionCalls.getFirst().indexInSequenceTo) {
                    buildBackwardLine(lines.get(functionCalls.getFirst().indexInSequenceTo), lines.get(functionCalls.getFirst().indexInSequenceFrom), pane, 40 + pos++ * 25, functionCalls.getFirst().functionName);
                } else if (functionCalls.getFirst().indexInSequenceFrom == functionCalls.getFirst().indexInSequenceTo) {
                    buidLoopLine(lines.get(functionCalls.getFirst().indexInSequenceFrom), pane, 40 + pos * 25, functionCalls.getFirst().functionName);
                    pos += 2;
                } else {
                    buildForwardLine(lines.get(functionCalls.getFirst().indexInSequenceFrom), lines.get(functionCalls.getFirst().indexInSequenceTo), pane, 40 + pos++ * 25, functionCalls.getFirst().functionName);
                }
                functionCalls.pop();
            }

            ScrollPane root = new ScrollPane();
            root.setPadding(new Insets(20));
            Stage stage = new Stage();
            root.setContent(pane);
            Scene sc = new Scene(root, 1200, 700);
            stage.setScene(sc);
            stage.setTitle("EVAS Framework - Sequence Diagram");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}