package AnalyzerPackage;

import javafx.scene.shape.Line;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Slicer {
    private String code;
    private String[] lines;

    Slicer(ArrayList<String> filePaths) {
        FilesUtil file = new FilesUtil();
        code = file.readFiles(filePaths) + "\n";
        lines = code.split("\n");
    }

    public int getLinesCount() {
        return lines.length;
    }

    public boolean findStringToRegex(String stringToBeMatched, String regex) {
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(stringToBeMatched);

        return matcher.find();
    }

    public boolean lookingAtStringToRegex(String stringToBeMatched, String regex) {
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(stringToBeMatched);

        return matcher.lookingAt();
    }

    //a simple class that holds every line with its index and the comparing between to objects of that class is a compare between the indexes only.
    private class LineOfCode implements Comparable {
        public int index;
        public String line;

        public LineOfCode(int index, String line) {
            this.index = index;
            this.line = line;
        }

        @Override
        public int compareTo(Object o) {
            return index - ((LineOfCode) o).index;
        }
    }

    //a simple class that holds every variable with its line of code
    private class VarLine {
        public LineOfCode line;
        public String variableName;

        public VarLine(LineOfCode line, String variableName) {
            this.line = line;
            this.variableName = variableName;
        }
    }

    //This function checks if a specific variable already existed in the array "variablesAttachedTo" or not.
    public boolean isFoundInVarlineSet(Set<VarLine> variablesAttachedTo, String varName) {
        for (VarLine varLine : variablesAttachedTo) {
            if (varLine.variableName.equals(varName)) {
                return true;
            }
        }
        return false;
    }

    public String staticSlicing(int endLine, String variableName) {
        //array list holding teh final result of the slice
        ArrayList<LineOfCode> result = new ArrayList<>();
        //array list holding the bodies of the if conditions found in the specified slice
        ArrayList<LineOfCode> ifConditions = new ArrayList<>();
        //array list holding the variables that needs a double check to get any variables attached to the main variable that was selected by the user
        ArrayList<String> revisit = new ArrayList<>();
        //a set holding the variables attached to the variable selected by the user
        Set<VarLine> variablesAttachedTo = new HashSet<>();
        for (int i = 0; i < endLine; i++) {
            lines[i] = lines[i].trim();
            if ((lines[i].length() > 1 && lines[i].charAt(0) == '/' && lines[i].charAt(1) == '/')) {
                continue;
            }
            String words[] = lines[i].trim().split(" ");
            //searching for any if or while or for using regex and adding the bodies to if conditions
            if (lookingAtStringToRegex(lines[i], "(if|for|while)(\\s)?\\(")) {
                Deque<Character> stack = new ArrayDeque<Character>();
                int temp = i;
                String ifCondition = "";
                stack.push('F');
                while (!stack.isEmpty() && i < endLine) {
                    if (lines[i].contains("{")) {
                        stack.push('{');
                        ifCondition += lines[i] + "\n";
                    } else if (lines[i].contains(variableName)) {
                        if (!ifCondition.equals("")) {
                            ifCondition += String.valueOf(i + 1) + ": " + lines[i] + "\n";
                        } else {
                            ifCondition += lines[i] + "\n";
                        }
                    }
                    if (lines[i].contains("}")) {
                        stack.pop();
                        if (!stack.isEmpty() && (stack.getFirst() == 'F')) {
                            stack.pop();
                        }
                        if (!lines[i].contains(variableName)) {
                            ifCondition += "\n}";
                        }
                    }
                    i++;
                }
                ifConditions.add(new LineOfCode(i, String.valueOf(temp) + ' ' + ifCondition));
                i = temp + 1;
            }
            //searching for the variable selected by the user here in all the cases
            words = lines[i].trim().split(" ");
            if (findStringToRegex(lines[i], "(\\w+|.)(\\[\\])? \\w+\\s?(=|;)\\s?") && !lines[i].contains("{") && !lines[i].contains("}") && !(lines[i].charAt(0) == '/') && !(lines[i].charAt(1) == '/')) {
                if (words[1].equals("static")) {
                    if (words[3].contains(";")) {
                        words[3] = words[3].substring(0, words[3].indexOf(';'));
                    }
                    if (!isFoundInVarlineSet(variablesAttachedTo, words[3])) {
                        variablesAttachedTo.add(new VarLine(new LineOfCode(i, lines[i]), words[3]));
                    }
                } else if (words.length == 3) {
                    if (words[2].contains(";")) {
                        words[2] = words[2].substring(0, words[2].indexOf(';'));
                    }
                    if (!isFoundInVarlineSet(variablesAttachedTo, words[2])) {
                        variablesAttachedTo.add(new VarLine(new LineOfCode(i, lines[i]), words[2]));
                    }
                } else if (words.length == 2) {
                    if (words[1].contains(";")) {
                        words[1] = words[1].substring(0, words[1].indexOf(';'));
                    }
                    if (!isFoundInVarlineSet(variablesAttachedTo, words[1])) {
                        variablesAttachedTo.add(new VarLine(new LineOfCode(i, lines[i]), words[1]));
                    }
                } else if (!words[1].equals("=")) {
                    if (words[0].equals("private") || words[0].equals("public")) {
                        if (words[2].contains(";")) {
                            words[2] = words[2].substring(0, words[2].indexOf(';'));
                        }
                        if (!isFoundInVarlineSet(variablesAttachedTo, words[2])) {
                            variablesAttachedTo.add(new VarLine(new LineOfCode(i, lines[i]), words[2]));
                        }
                    } else {
                        if (words[1].contains(";")) {
                            words[1] = words[1].substring(0, words[1].indexOf(';'));
                        }
                        if (!isFoundInVarlineSet(variablesAttachedTo, words[1])) {
                            variablesAttachedTo.add(new VarLine(new LineOfCode(i, lines[i]), words[1]));
                        }
                    }
                }
            }
            if (!lines[i].contains(variableName)) {
                continue;
            }
            if (words[0].equals(variableName) || (words.length > 1 && words[1].equals(variableName)) || (words.length > 2 && words[2].equals(variableName))) {
                //result.add(new LineOfCode(i, lines[i]));
                boolean foundCondition = false;
                for (int j = 0; j < ifConditions.size(); j++) {
                    if (i < ifConditions.get(j).index && i > Integer.parseInt(ifConditions.get(j).line.split(" ")[0])) {
                        result.add(new LineOfCode(Integer.parseInt(ifConditions.get(j).line.split(" ")[0]), ifConditions.get(j).line.substring(2)));
                        foundCondition = true;
                    }
                }
                if (!foundCondition) {
                    result.add(new LineOfCode(i, lines[i]));
                }
            } else if (lines[i].contains(variableName)) {
                for (int j = 0; j < words.length; j++) {
                    if ((words[j].contains(variableName) && (words[j].contains(variableName + "[") || words[j].contains(variableName + ".") || words[j].contains(variableName + ";") || words[j].equals(variableName)) && words[j].substring(0, variableName.length()).equals(variableName)) || words[j].equals("(" + variableName)) {
                        //result.add(new LineOfCode(i, lines[i]));
                        boolean foundCondition = false;
                        if (!lookingAtStringToRegex(lines[i], "(if|for|while)(\\s)?\\(")) {
                            revisit.add(lines[i]);
                        }
                        for (int k = 0; k < ifConditions.size(); k++) {
                            if (i < ifConditions.get(k).index && i > Integer.parseInt(ifConditions.get(k).line.split(" ")[0])) {
                                result.add(new LineOfCode(Integer.parseInt(ifConditions.get(k).line.split(" ")[0]), ifConditions.get(k).line.substring(2)));
                                foundCondition = true;
                            }
                        }

                        if (!foundCondition) {
                            result.add(new LineOfCode(i, lines[i]));
                        }
                    }
                }
            }
        }
        //that for loop searches for the initialization of the variables attached to the selected variable.
        for (VarLine varLine : variablesAttachedTo) {
            for (int i = 0; i < revisit.size(); i++) {
                if (revisit.get(i).contains(varLine.variableName) && revisit.get(i).contains(variableName) && !varLine.variableName.equals(variableName)) {
                    result.add(varLine.line);
                    break;
                }
            }
        }
        // Hard coded values for the example given.
        //result.add(new LineOfCode(0,"int n;"));
        //result.add(new LineOfCode(1,"int i = 1;"));

        Collections.sort(result);
        String resultString = "";
        for (int i = 0; i < result.size(); i++) {
            if (i > 0 && result.get(i - 1).index == result.get(i).index) {
                continue;
            }
            resultString += (result.get(i).index + 1) + ": " + result.get(i).line + "\n";
        }
        return resultString;
    }

    public String dynamicSlicing(int endLine, String variableName, int varValue) {
        ArrayList<LineOfCode> result = new ArrayList<>();
        ArrayList<LineOfCode> revisit = new ArrayList<>();
        ArrayList<String> revisitVariables = new ArrayList<>();
        ArrayList<String> ifConditions = new ArrayList<>();
        ArrayList<LineOfCode> ifConditions2 = new ArrayList<>();
        Deque<Character> stack = new ArrayDeque<Character>();
        Set<VarLine> variablesAttachedTo = new HashSet<>();
        for (int i = 0; i < endLine; i++) {
            lines[i] = lines[i].trim();
            if ((lines[i].length() > 1 && lines[i].charAt(0) == '/' && lines[i].charAt(1) == '/')) {
                continue;
            }

            String words[] = lines[i].trim().split(" ");

            if (lookingAtStringToRegex(lines[i], "(if|for|while)(\\s)?\\(") && !lines[i].contains(variableName)) {
                int temp = i;
                String ifCondition = "";
                stack.push('F');
                while (!stack.isEmpty() && i < endLine) {
                    if (lines[i].contains("{")) {
                        stack.push('{');
                        ifCondition += lines[i] + "\n";
                    }
                    if (lines[i].contains("}")) {
                        stack.pop();
                        if (!stack.isEmpty() && (stack.getFirst() == 'F')) {
                            stack.pop();
                        }
                        if (!lines[i].contains(variableName)) {
                            ifCondition += "\n}";
                        }
                    }
                    if (lines[i].contains(variableName)) {
                        if (!ifCondition.equals("")) {
                            ifCondition += String.valueOf(i + 1) + ": " + lines[i] + "\n";
                        } else {
                            ifCondition += lines[i] + "\n";
                        }
                    }
                    i++;
                }
                ifConditions2.add(new LineOfCode(i, String.valueOf(temp) + ' ' + ifCondition));
                i = temp;
            }

            if (findStringToRegex(lines[i], "(\\w+|.)(\\[\\])? \\w+\\s?(=|;)\\s?") && !lines[i].contains("{") && !lines[i].contains("}") && !(lines[i].charAt(0) == '/') && !(lines[i].charAt(1) == '/')) {
                if (words[1].equals("static")) {
                    if (words[3].contains(";")) {
                        words[3] = words[3].substring(0, words[3].indexOf(';'));
                    }
                    if (words[3] == variableName) {
                        result.add(new LineOfCode(i, lines[i]));
                    } else {
                        if (!isFoundInVarlineSet(variablesAttachedTo, words[3])) {
                            variablesAttachedTo.add(new VarLine(new LineOfCode(i, lines[i]), words[3]));
                        }
                    }
                } else if (words.length == 3 && !words[1].equals("=")) {
                    if (words[2].contains(";")) {
                        words[2] = words[2].substring(0, words[2].indexOf(';'));
                    }
                    if (words[2] == variableName) {
                        result.add(new LineOfCode(i, lines[i]));
                    } else {
                        if (!isFoundInVarlineSet(variablesAttachedTo, words[2])) {
                            variablesAttachedTo.add(new VarLine(new LineOfCode(i, lines[i]), words[2]));
                        }
                    }
                } else if (!words[1].equals("=")) {
                    if (words[0].equals("private") || words[0].equals("public")) {
                        if (words[2].contains(";")) {
                            words[2] = words[2].substring(0, words[2].indexOf(';'));
                        }
                        if (words[2] == variableName) {
                            result.add(new LineOfCode(i, lines[i]));
                        } else {
                            if (!isFoundInVarlineSet(variablesAttachedTo, words[2])) {
                                variablesAttachedTo.add(new VarLine(new LineOfCode(i, lines[i]), words[2]));
                            }
                        }
                    } else {
                        if (words[1].contains(";")) {
                            words[1] = words[1].substring(0, words[1].indexOf(';'));
                        }
                        if (words[1] == variableName) {
                            result.add(new LineOfCode(i, lines[i]));
                        } else {
                            if (!isFoundInVarlineSet(variablesAttachedTo, words[1])) {
                                variablesAttachedTo.add(new VarLine(new LineOfCode(i, lines[i]), words[1]));
                            }
                        }
                    }
                }
            }

            if (!lines[i].contains(variableName)) {
                continue;
            }
            if (words[0].equals(variableName) || (words.length > 1 && words[1].equals(variableName)) || (words.length > 2 && words[2].equals(variableName))) {
                //result.add(new LineOfCode(i, lines[i]));
                boolean foundCondition = false;
                //if (lookingAtStringToRegex(lines[i], "(if|for|while)(\\s)?\\(")) {
                for (int k = 0; k < ifConditions2.size(); k++) {
                    if (i < ifConditions2.get(k).index && i > Integer.parseInt(ifConditions2.get(k).line.split(" ")[0]) && !ifConditions2.get(k).line.split("\n")[0].contains(variableName)) {
                        result.add(new LineOfCode(Integer.parseInt(ifConditions2.get(k).line.split(" ")[0]), ifConditions2.get(k).line.substring(2)));
                        foundCondition = true;
                    }
                    else if (ifConditions2.get(k).line.split("\n")[0].contains(variableName))
                    {
                        foundCondition = true;
                    }
                }
                //}
                if (!foundCondition) {
                    result.add(new LineOfCode(i, lines[i]));
                }
            } else if (lines[i].contains(variableName)) {
                if (lookingAtStringToRegex(lines[i], "(if|for|while)(\\s)?\\(") || lookingAtStringToRegex(lines[i], "(else if)(\\s)?\\(")) {
                    revisit.add(new LineOfCode(i, lines[i]));
                    String ifCondition = "";
                    stack.push('F');
                    while (!stack.isEmpty() && i < endLine) {
                        if (lines[i].contains("{")) {
                            stack.push('{');
                        }
                        if (lines[i].contains("}")) {
                            stack.pop();
                            if (!stack.isEmpty() && (stack.getFirst() == 'F')) {
                                stack.pop();
                            }
                            if (!lines[i].contains(variableName)) {
                                ifCondition += "\n}";
                            }
                        }
                        if (lines[i].contains(variableName)) {
                            if (!ifCondition.equals("")) {
                                ifCondition += String.valueOf(i + 1) + ": " + lines[i] + "\n";
                            } else {
                                ifCondition += lines[i] + "\n";
                            }
                            revisitVariables.add(String.valueOf(i + 1) + ": " + lines[i]);
                        }
                        i++;
                    }
                    ifConditions.add(ifCondition);
                    i--;
                } else {
                    for (int j = 0; j < words.length; j++) {
                        if ((words[j].contains(variableName) && (words[j].contains(variableName + "[") || words[j].contains(variableName + ".") || words[j].contains(variableName + ";") || words[j].equals(variableName)) && words[j].substring(0, variableName.length()).equals(variableName)) || words[j].equals("(" + variableName)) {
                            //result.add(new LineOfCode(i, lines[i]));
                            boolean foundCondition = false;
                            //if (lookingAtStringToRegex(lines[i], "(if|for|while)(\\s)?\\(")) {
                                for (int k = 0; k < ifConditions2.size(); k++) {
                                    if (i < ifConditions2.get(k).index && i > Integer.parseInt(ifConditions2.get(k).line.split(" ")[0])) {
                                        result.add(new LineOfCode(Integer.parseInt(ifConditions2.get(k).line.split(" ")[0]), ifConditions2.get(k).line.substring(2)));
                                        foundCondition = true;
                                    }
                                }
                            //}
                            if (!foundCondition) {
                                result.add(new LineOfCode(i, lines[i]));
                            }
                        }
                    }
                }
            }
        }

        //the for loop below checks if the given value passes the conditions that includes the selected variable.
        for (int i = 0; i < revisit.size(); i++) {
            String[] split, split2;

            String condition = revisit.get(i).line;

            condition = condition.trim();
            ArrayList<String> operators = new ArrayList<>();
            operators.add(">");
            operators.add("<");
            operators.add(">=");
            operators.add("<=");
            operators.add("==");
            operators.add("!=");
            for (String operator : operators) {
                condition = condition.replace('(', ' ');
                condition = condition.replace(')', ' ');
                condition = condition.trim();
                split = condition.split(operator);
                if (split.length > 1 && condition.charAt(0) != '/' && condition.charAt(1) != '/') {
                    for (int j = 0; j < split.length - 1; j++) {
                        split2 = split[j].split(" ");
                        if (split2.length > 1) {
                            if (!split2[split2.length - 1].equals(variableName)) {
                                if (operator.equals(">")) {
                                    if (varValue < Integer.parseInt(split2[split2.length - 1])) {
                                        revisit.get(i).line = ifConditions.get(i);
                                        result.add(revisit.get(i));
                                    }
                                } else if (operator.equals("<")) {
                                    if (varValue > Integer.parseInt(split2[split2.length - 1])) {
                                        revisit.get(i).line = ifConditions.get(i);
                                        result.add(revisit.get(i));
                                    }
                                } else if (operator.equals("==") || operator.equals("<=") || operator.equals(">=")) {
                                    if (varValue == Integer.parseInt(split2[split2.length - 1])) {
                                        revisit.get(i).line = ifConditions.get(i);
                                        result.add(revisit.get(i));
                                    }
                                } else if (operator.equals("!=")) {
                                    if (varValue != Integer.parseInt(split2[split2.length - 1])) {
                                        revisit.get(i).line = ifConditions.get(i);
                                        result.add(revisit.get(i));
                                    }
                                }
                            }
                        }
                        split2 = split[j + 1].split(" ");
                        if (split2.length > 1) {
                            if (!split2[1].equals(variableName)) {
                                if (operator.equals(">")) {
                                    if (varValue > Integer.parseInt(split2[1])) {
                                        revisit.get(i).line = ifConditions.get(i);
                                        result.add(revisit.get(i));
                                    }
                                } else if (operator.equals("<")) {
                                    if (varValue < Integer.parseInt(split2[1])) {
                                        revisit.get(i).line = ifConditions.get(i);
                                        result.add(revisit.get(i));
                                    }
                                } else if (operator.equals("==") || operator.equals("<=") || operator.equals(">=")) {
                                    if (varValue == Integer.parseInt(split2[1])) {
                                        revisit.get(i).line = ifConditions.get(i);
                                        result.add(revisit.get(i));
                                    }
                                } else if (operator.equals("!=")) {
                                    if (varValue != Integer.parseInt(split2[1])) {
                                        revisit.get(i).line = ifConditions.get(i);
                                        result.add(revisit.get(i));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for (VarLine varLine : variablesAttachedTo) {
            for (int i = 0; i < revisitVariables.size(); i++) {
                if (revisitVariables.get(i).contains(varLine.variableName) && revisitVariables.get(i).contains(variableName) && !varLine.variableName.equals(variableName)) {
                    result.add(varLine.line);
                    break;
                }
            }
        }
        // Hard coded values for the example given.
//        result.add(new LineOfCode(0,"int n;"));
//        result.add(new LineOfCode(1,"int i = 1;"));
        Collections.sort(result);
        Collections.sort(result);
        String resultString = "";
        for (int i = 0; i < result.size(); i++) {
            if (i > 0 && result.get(i - 1).index == result.get(i).index) {
                continue;
            }
            resultString += (result.get(i).index + 1) + ": " + result.get(i).line + "\n";
        }
        return resultString;
    }
}
