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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;

//This class draws the flowchart by using the logic handed over by the analyzer class after the using the function get flowchart.
public class FlowChart extends Application {
    int pos = 0;
    Analyzer analyzer;

    @Override
    public void start(Stage primaryStage) throws Exception {

        analyzer.generateClassesArray();
        analyzer.getTimelines().add("Main");
        analyzer.getFlowChart(analyzer.getMainFunction());
        showDiagram(analyzer.getStatements());
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Pane pane;

    private static StackPane buildNormalStatement(String color, Statement input, int position, double centerX) {
        double paneSize = input.Statement.length() * 6;
        StackPane dotPane = new StackPane();
        if (input.level == 0) {
            dotPane.setTranslateX(centerX * 6 - paneSize / 2 + 20);
        } else {
            if (input.isConditionLeft) {
                dotPane.setTranslateX((centerX * 6 - paneSize) / Math.pow(2, input.level));
            } else {
                dotPane.setTranslateX(centerX * 6 - paneSize / 2 + 70 + (centerX * 6 - paneSize / 2 + 40) / Math.pow(2, input.level));
            }
        }
        dotPane.setTranslateY(40 * position);
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(paneSize);
        rectangle.setHeight(20);
        rectangle.setStyle("-fx-fill:" + color + ";-fx-stroke-width:2px;-fx-stroke:black;");
        dotPane.getChildren().add(rectangle);

        Label txt = new Label(input.Statement);
        dotPane.getChildren().add(txt);
        dotPane.setPrefSize(paneSize, 20);
        dotPane.setMaxSize(paneSize, 20);
        dotPane.setMinSize(paneSize, 20);
        return dotPane;
    }

    private static StackPane buildStartEndStatement(String color, Statement input, int position, double centerX) {
        double paneSize = input.Statement.length() * 10;
        StackPane dotPane = new StackPane();
        if (input.level == 0) {
            dotPane.setTranslateX(centerX * 6 - paneSize / 2 + 20);
        } else {
            if (input.isConditionLeft) {
                dotPane.setTranslateX((centerX * 6 - paneSize / 2 + 20) / Math.pow(2, input.level));
            } else {
                dotPane.setTranslateX(centerX * 6 - paneSize / 2 + 20 + (centerX * 6 - paneSize / 2 + 20) / Math.pow(2, input.level));
            }
        }
        dotPane.setTranslateY(40 * position);
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(paneSize);
        rectangle.setHeight(20);
        rectangle.setStyle("-fx-fill:" + color + ";-fx-stroke-width:2px;-fx-stroke:black;");
        dotPane.getChildren().add(rectangle);

        Label txt = new Label(input.Statement);
        dotPane.getChildren().add(txt);
        dotPane.setPrefSize(paneSize, 20);
        dotPane.setMaxSize(paneSize, 20);
        dotPane.setMinSize(paneSize, 20);
        return dotPane;
    }

    private static StackPane buildConditionStatement(String color, Statement input, int position, double centerX) {
        double paneSize = input.Statement.length() * 6;
        StackPane dotPane = new StackPane();
        //dotPane.setTranslateX(centerX * 6 - paneSize / 2 + 20);
        dotPane.setTranslateX((centerX * 6 - paneSize / 2 + 20) / Math.pow(2, input.level));
        dotPane.setTranslateY(40 * position);
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(paneSize / Math.sqrt(2) + 5);
        rectangle.setHeight(paneSize / Math.sqrt(2) + 5);
        rectangle.setRotate(45);
        rectangle.setStyle("-fx-fill:" + color + ";-fx-stroke-width:2px;-fx-stroke:black;");
        dotPane.getChildren().add(rectangle);

        Label txt = new Label(input.Statement);
        dotPane.getChildren().add(txt);

        txt = new Label("True");
        txt.setTranslateX(position - paneSize - 15);
        txt.setTranslateY(- 15);
        dotPane.getChildren().add(txt);
         txt = new Label("False");
        txt.setTranslateX(position + paneSize + 15);
        txt.setTranslateY(- 15);
        if (!input.isLoop) {
            dotPane.getChildren().add(txt);
        }

        dotPane.setPrefSize(paneSize, paneSize);
        dotPane.setMaxSize(paneSize, paneSize);
        dotPane.setMinSize(paneSize, paneSize);
        return dotPane;
    }

    private static Line buildTimeline(Pane parent, int position, String className, int numOfFunctions) {
        Line line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);
        line.setStartX(position);
        line.setStartY(20);
        line.setEndX(position);
        line.setEndY(20 + 20 * numOfFunctions);
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

    private static void buildLoopLine(StackPane startStatement, StackPane endStatement, Pane parent) {
        Line line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);
        line.setStartX(startStatement.getTranslateX());
        line.setStartY(startStatement.getTranslateY());
        line.setEndX(startStatement.getTranslateX() + startStatement.getMaxWidth() + endStatement.getMaxWidth() * 4);
        line.setEndY(startStatement.getTranslateY());

        Line line2 = new Line();
        line2.setStroke(Color.BLACK);
        line2.setStrokeWidth(2);
        line2.setStartX(startStatement.getTranslateX() + startStatement.getMaxWidth() + endStatement.getMaxWidth() * 4);
        line2.setStartY(startStatement.getTranslateY());
        line2.setEndX(startStatement.getTranslateX() + startStatement.getMaxWidth() + endStatement.getMaxWidth() * 4);
        line2.setEndY(endStatement.getTranslateY() + endStatement.getMaxHeight() / 2);

        Line line3 = new Line();
        line3.setStroke(Color.BLACK);
        line3.setStrokeWidth(2);
        line3.setStartX(startStatement.getTranslateX() + startStatement.getMaxWidth() + endStatement.getMaxWidth() * 4);
        line3.setStartY(endStatement.getTranslateY() + endStatement.getMaxHeight() / 2);
        line3.setEndX(endStatement.getTranslateX() + endStatement.getMaxWidth());
        line3.setEndY(endStatement.getTranslateY() + endStatement.getMaxHeight() / 2);
        StackPane arrowBA = getArrow(line3, true);
        parent.getChildren().addAll(line, line2, line3, arrowBA);
    }

    private static Line getLine(StackPane startStatement, StackPane endStatement) {
        Line line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);
        line.startXProperty().bind(startStatement.layoutXProperty().add(startStatement.translateXProperty()).add(startStatement.widthProperty().divide(2)));
        line.startYProperty().bind(startStatement.layoutYProperty().add(startStatement.translateYProperty()).add(startStatement.heightProperty().divide(2)));
        line.endXProperty().bind(endStatement.layoutXProperty().add(endStatement.translateXProperty()).add(endStatement.widthProperty().divide(2)));
        line.endYProperty().bind(endStatement.layoutYProperty().add(endStatement.translateYProperty()).add(endStatement.heightProperty().divide(2)));
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
                dt = lineLength - 5 - (arrow.getWidth() / 2) - 5;
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
                dt = lineLength - 5 - (arrow.getWidth() / 2) - 5;
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

    public static void showDiagram(ArrayList<Statement> statements) {
        try {
            pane = new Pane();
            ArrayList<Line> lines = new ArrayList<>();
            int max = 0;
            for (int i = 0; i < statements.size(); i++) {
                if (max < statements.get(i).Statement.length()) {
                    max = statements.get(i).Statement.length();
                }
            }
            max += 20;
            ArrayList<StackPane> stackPanes = new ArrayList<>();
            int leftPosition = 0;
            int rightPosition = 0;
            int rightLessLevelPosition = 0;
            int conditionPositionCommulative = 0;
            int x = 0;
            Deque<Integer> positions = new ArrayDeque<>();
            for (int i = 0; i < statements.size(); i++) {
                if (i == 0 || i == statements.size() - 1) {
                    StackPane stackPane = buildStartEndStatement("White", statements.get(i), i - conditionPositionCommulative, max / 1.5);
                    stackPanes.add(stackPane);
                    continue;
                }
                if (statements.get(i).isCondition || statements.get(i).isLoop) {
                    StackPane stackPane = buildConditionStatement("White", statements.get(i), i - conditionPositionCommulative, max / 1.5);
                    stackPanes.add(stackPane);
                    //pane.getChildren().add(stackPane);
                    if (i > 0 && statements.get(i).level < statements.get(i + 1).level) {
                        positions.add(leftPosition + 1);
                    }
                    leftPosition = 0;
                } else {
                    if (statements.get(i).level > 0 && statements.get(i).isConditionLeft) {
                        StackPane stackPane = buildNormalStatement("White", statements.get(i), i - conditionPositionCommulative, max / 1.5);
                        stackPanes.add(stackPane);
                        //pane.getChildren().add(stackPane);
                        leftPosition++;
                    } else if (statements.get(i).level > 0 && !statements.get(i).isConditionLeft) {
                        boolean isLessLevel = false;
                        if (statements.get(i).level < statements.get(i - 1).level) {
                            isLessLevel = true;
                            while (!positions.isEmpty()) {
                                x += positions.getFirst();
                                positions.pop();
                            }
                            x += rightPosition - 1;
                            Statement test = statements.get(i);
                            int index = i - 1;
                            while (statements.get(index).level >= test.level) {
                                index--;
                            }
                            rightLessLevelPosition = index + 1;
                        }
                        if (!isLessLevel) {
                            StackPane stackPane = buildNormalStatement("White", statements.get(i), i - leftPosition - x - conditionPositionCommulative, max / 1.5);
                            stackPanes.add(stackPane);
                            //pane.getChildren().add(stackPane);
                            rightPosition++;
                        } else {
                            StackPane stackPane = buildNormalStatement("White", statements.get(i), rightLessLevelPosition, max / 1.5);
                            stackPanes.add(stackPane);
                            //pane.getChildren().add(stackPane);
                            rightLessLevelPosition++;
                        }
                    } else {
                        if (i > 0 && !statements.get(i - 1).isConditionLeft) {
                            conditionPositionCommulative += leftPosition > rightPosition ? leftPosition : rightPosition;
                        }
                        StackPane stackPane = buildNormalStatement("White", statements.get(i), i - conditionPositionCommulative, max / 1.5);
                        stackPanes.add(stackPane);
                        //pane.getChildren().add(stackPane);
                    }
                }
            }

            ArrayList<Integer> prevConditions = new ArrayList<>();
            for (int i = 1; i < statements.size(); i++) {
                if (statements.get(i - 1).level == statements.get(i).level && !statements.get(i - 1).isCondition && statements.get(i - 1).isConditionLeft == statements.get(i).isConditionLeft) {
                    if (statements.get(i - 1).isCondition) {
                        prevConditions.add(i - 1);
                    }
                    Line line = getLine(stackPanes.get(i - 1), stackPanes.get(i));
                    StackPane arrow = getArrow(line, true);
                    pane.getChildren().addAll(line, arrow);
                } else {
                    if ((statements.get(i - 1).isConditionLeft && statements.get(i).level > statements.get(i - 1).level)) {
                        if (statements.get(i - 1).isCondition) {
                            prevConditions.add(i - 1);
                        }
                        Line line = getLine(stackPanes.get(i - 1), stackPanes.get(i));
                        StackPane arrow = getArrow(line, true);
                        pane.getChildren().addAll(line, arrow);
                    } else if (statements.get(i - 1).isCondition) {
                        prevConditions.add(i - 1);
                        Line line = getLine(stackPanes.get(i - 1), stackPanes.get(i));
                        StackPane arrow = getArrow(line, true);
                        pane.getChildren().addAll(line, arrow);
                    } else if (statements.get(i - 1).isConditionLeft && !statements.get(i).isConditionLeft && statements.get(i).level > 0 && statements.get(i - 1).level == statements.get(i).level) {
                        Line line = getLine(stackPanes.get(prevConditions.get(prevConditions.size() - 1)), stackPanes.get(i));
                        StackPane arrow = getArrow(line, true);
                        pane.getChildren().addAll(line, arrow);
                    } else if (!statements.get(i - 1).isConditionLeft && !statements.get(i).isConditionLeft && statements.get(i).level > 0 && statements.get(i - 1).level > statements.get(i).level) {
                        prevConditions.remove(prevConditions.size() - 1);
                        Line line = getLine(stackPanes.get(prevConditions.get(prevConditions.size() - 1)), stackPanes.get(i));
                        StackPane arrow = getArrow(line, true);
                        pane.getChildren().addAll(line, arrow);
                    } else if (statements.get(i).level < statements.get(i - 1).level && statements.get(i - 1).getLoopStart() > 0) {
                        buildLoopLine(stackPanes.get(i - 1), stackPanes.get(statements.get(i - 1).getLoopStart()), pane);
                        Line line = getLine(stackPanes.get(statements.get(i - 1).getLoopStart()), stackPanes.get(i));
                        StackPane arrow = getArrow(line, true);
                        pane.getChildren().addAll(line, arrow);
                    } else if (!statements.get(i).isConditionLeft && !statements.get(i - 1).isConditionLeft && statements.get(i).level < statements.get(i - 1).level) {
                        Line line = getLine(stackPanes.get(i - 1), stackPanes.get(i));
                        StackPane arrow = getArrow(line, true);
                        pane.getChildren().addAll(line, arrow);
                        int conditionEnd = i;
                        int currentLevel = statements.get(i - 1).level;
                        for (int j = i - 1; j > 1; j--) {
                            if (statements.get(j).level > currentLevel) {
                                Line line2 = getLine(stackPanes.get(j), stackPanes.get(conditionEnd));
                                StackPane arrow2 = getArrow(line2, true);
                                pane.getChildren().addAll(line2, arrow2);
                                currentLevel = statements.get(j).level;
                            }
                            if (!statements.get(j).isConditionLeft && statements.get(j - 1).isConditionLeft && statements.get(j).level == statements.get(j - 1).level) {
                                Line line2 = getLine(stackPanes.get(j - 1), stackPanes.get(conditionEnd));
                                StackPane arrow2 = getArrow(line2, true);
                                pane.getChildren().addAll(line2, arrow2);
                                currentLevel = statements.get(j - 1).level;
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < stackPanes.size(); i++) {
                pane.getChildren().add(stackPanes.get(i));
            }

            pane.setPrefSize(max * 8, statements.size() * 40);
//            int pos = 0;
//            for (int i = 0; i < statements.size(); i++) {
//                if (statements.get(i).indexInSequenceFrom > statements.get(i).indexInSequenceTo) {
//                        buildBackwardLine(lines.get(statements.get(i).indexInSequenceTo), lines.get(statements.get(i).indexInSequenceFrom), pane, 40 + pos++ * 25, statements.get(i).functionName);
//                } else if (statements.get(i).indexInSequenceFrom == statements.get(i).indexInSequenceTo) {
//                    buidLoopLine(lines.get(statements.get(i).indexInSequenceFrom), pane, 40 + pos * 25, statements.get(i).functionName);
//                    pos += 2;
//                } else {
//                    buildForwardLine(lines.get(statements.get(i).indexInSequenceFrom), lines.get(statements.get(i).indexInSequenceTo), pane, 40 + pos++ * 25, statements.get(i).functionName);
//                }
//                statements.pop();
//            }

            ScrollPane root = new ScrollPane();
            root.setPadding(new Insets(20));
            Stage stage = new Stage();
            root.setContent(pane);
            Scene sc = new Scene(root, 1200, 700);
            stage.setScene(sc);
            stage.setTitle("EVAS Framework - Flow Chart");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}