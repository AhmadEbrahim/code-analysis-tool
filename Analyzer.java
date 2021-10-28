package AnalyzerPackage;

import java.util.*;
import java.lang.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analyzer {
    private String code;
    private String[] lines;
    private ArrayList<String> classesNames = new ArrayList<>();
    private ArrayList<String> functionNames = new ArrayList<>();
    public ArrayList<AnalyzerPackage.Class> classes = new ArrayList<>();
    private ArrayList<String> timelines = new ArrayList<>();
    private Deque<FunctionCall> functionCalls = new ArrayDeque<>();
    private ArrayList<Statement> statements = new ArrayList<>();
    private int N1 = 0; // Total number of operators
    private int N2 = 0; // Total number of operands

    //In here the file that was selected by the user becomes a set of strings in the array "lines"
    Analyzer(ArrayList<String> filePaths) {
        FilesUtil file = new FilesUtil();
        code = file.readFiles(filePaths) + "\n";
        lines = code.split("\n");
        getNumberOfClasses();
    }

    //In here the files in a folder that were selected by the user become a set of strings in the array "lines"
    Analyzer(String folderPath) {
        FilesUtil folder = new FilesUtil();
        ArrayList<String> filePaths = folder.readFolder(folderPath);
        code = folder.readFiles(filePaths) + "\n";
        lines = code.split("\n");
        getNumberOfClasses();
    }

    public ArrayList<Statement> getStatements() {
        return statements;
    }

    public ArrayList<String> getTimelines() {
        return timelines;
    }

    public Deque<FunctionCall> getFunctionCalls() {
        return functionCalls;
    }

    public ArrayList<String> getClassesNames() {
        getNumberOfClasses();
        return this.classesNames;
    }
    //That function returns the number of classes by searching for the words mentioned below as (class, interface, etc.)
    public int getNumberOfClasses() {
        classesNames.clear();
        for (String line : lines) {
            String words[] = line.trim().split(" ");

            for (int j = 0; j < words.length && j < 2; j++) {
                if ((words[j].equals("class") || words[j].equals("interface") || words[j].equals("abstract"))) {
                    if (words[j + 1].contains("{")) {
                        classesNames.add(words[j + 1].substring(0, words[j + 1].length() - 1));
                    } else {
                        classesNames.add(words[j + 1]);
                    }
                }
            }
        }
        return classesNames.size();
    }
    //That function returns the names of classes by looping on the array classesNames.
    public void printClassesNames() {
        for (String classesName : classesNames) {
            System.out.println(classesName);
        }
    }

    //That function counts the lines of code by returning the size of the array lines.
    public int getNumberOfLines() {
        return lines.length;
    }

    //That function counts the number of functions in the code by searching for a specific words using the regular expressions.
    public int getNumberOfFunctions() {
        functionNames.clear();
        String line = "";
        StringBuilder reverser;
        for (String s : lines) {
            line = s.trim();
            if (findStringToRegex(line, "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\) *(\\{?|[^;])")) {
                String fnName = "";
                if (line.contains("\"")) {
                    continue;
                }
                for (int j = line.indexOf('(') - 1; j > 0; j--) {
                    fnName += line.charAt(j);
                    if (line.charAt(j - 1) == ' ') {
                        break;
                    }
                }
                reverser = new StringBuilder();
                reverser.append(fnName);
                reverser.reverse();

                functionNames.add(reverser.toString());
            }
        }
        return functionNames.size();
    }

    //That function returns the names of the functions in the code by looping on the array functionNames.
    public ArrayList<String> getFunctionNames() {
        getNumberOfFunctions();
        return functionNames;
    }

    //That function gets the functions of a single class by using a stack to push the curly braces into it to determine the starting and the ending of a specific class.
    public void getFunctionsOfSpecificClass(int startLine, AnalyzerPackage.Class temp) {
        Deque<Character> stack = new ArrayDeque<Character>();
        stack.push('{');
        for (int i = startLine + 1; i < lines.length && !stack.isEmpty(); i++) {
            lines[i] = lines[i].trim();
            String words[] = lines[i].trim().split(" ");
            for (int j = 0; j < words.length && j < 2; j++) {
                if ((words[j].equals("class") || words[j].equals("interface") || words[j].equals("abstract"))) {
                    stack.push('C');
                }
            }
            if (lines[i].contains("{")) {
                stack.push('{');
            }
            if (lines[i].contains("}")) {
                stack.pop();
                if (!stack.isEmpty() && stack.getFirst() == 'C') {
                    stack.pop();
                }
            }
            if (stack.contains('C')) {
                continue;
            }
            if (findStringToRegex(lines[i], "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\) *(\\{?|[^;])") && !(lines[i].charAt(0) == '/') && !(lines[i].charAt(1) == '/')) {
                if (lines[i].contains("\"")) {
                    continue;
                }
                String fnName = "";
                String dataType = "";
                boolean isType = false;
                for (int j = lines[i].indexOf('(') - 1; j > 0; j--) {
                    if (!isType) {
                        fnName += lines[i].charAt(j);
                    } else {
                        dataType += lines[i].charAt(j);
                    }
                    if (lines[i].charAt(j - 1) == ' ' && !isType) {
                        isType = true;
                        j--;
                        continue;
                    }
                    if (lines[i].charAt(j - 1) == ' ' && isType) {
                        break;
                    }
                }
                StringBuilder reverser = new StringBuilder();
                reverser.append(fnName);
                reverser.reverse();
                StringBuilder reverser2 = new StringBuilder();
                reverser2.append(dataType);
                reverser2.reverse();
                String functionBody = "";
                Deque<Character> bodyStack = new ArrayDeque<Character>();
                for (int j = i; j < lines.length; j++) {
                    if (lines[j].contains("{")) {
                        bodyStack.push('{');
                    }
                    if (lines[j].contains("}")) {
                        bodyStack.pop();
                    }
                    functionBody += lines[j] + "\n";
                    if (bodyStack.isEmpty()) {
                        break;
                    }
                }
                Function fn = new Function(reverser.toString(), reverser2.toString(), functionBody);
                temp.getFunctions().add(fn);
            }
        }
    }

    //This function gets all the variables of a specific class using the same method of the stack mentioned above.
    public void getVariablesOfSpecificClass(int startLine, AnalyzerPackage.Class temp) {
        Deque<Character> stack = new ArrayDeque<Character>();
        stack.push('{');
        for (int i = startLine + 1; i < lines.length && !stack.isEmpty(); i++) {
            lines[i] = lines[i].trim();
            String words[] = lines[i].trim().split(" ");
            for (int j = 0; j < words.length && j < 2; j++) {
                if ((words[j].equals("class") || words[j].equals("interface") || words[j].equals("abstract"))) {
                    stack.push('C');
                }
            }
            if ((findStringToRegex(lines[i], "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\) *(\\{?|[^;])") || lookingAtStringToRegex(lines[i], "\\w+\\(.+\\)")) && !(lines[i].charAt(0) == '/') && !(lines[i].charAt(1) == '/')) {
                stack.push('F');
            }
            if (lines[i].contains("{")) {
                stack.push('{');
            }
            if (lines[i].contains("}")) {
                stack.pop();
                if (!stack.isEmpty() && (stack.getFirst() == 'C' || stack.getFirst() == 'F')) {
                    stack.pop();
                }
            }
            if (stack.contains('C') || stack.contains('F')) {
                continue;
            }
            if (findStringToRegex(lines[i], "(\\w+|.)(\\[\\])? \\w+\\s?(=|;)\\s?") && !lines[i].contains("{") && !lines[i].contains("}") && !(lines[i].charAt(0) == '/') && !(lines[i].charAt(1) == '/')) {
                if (words[1].equals("static")) {
                    if (words[3].contains(";")) {
                        words[3] = words[3].substring(0, words[3].indexOf(';'));
                    }
                    Variable var = new Variable(words[3], words[2]);
                    temp.getVariables().add(var);
                } else if (words.length == 3 && !words[1].equals("=")) {
                    if (words[2].contains(";")) {
                        words[2] = words[2].substring(0, words[2].indexOf(';'));
                    }
                    Variable var = new Variable(words[2], words[1]);
                    temp.getVariables().add(var);
                } else if (!words[1].equals("=")) {
                    if (words[0].equals("private") || words[0].equals("public")) {
                        if (words[2].contains(";")) {
                            words[2] = words[2].substring(0, words[2].indexOf(';'));
                        }
                        Variable var = new Variable(words[2], words[1]);
                        temp.getVariables().add(var);
                    } else {
                        if (words[1].contains(";")) {
                            words[1] = words[1].substring(0, words[1].indexOf(';'));
                        }
                        Variable var = new Variable(words[1], words[0]);
                        temp.getVariables().add(var);
                    }
                }
            }
        }
    }

    //This function returns an array list with the variables of a specific function using regex and the stack method mentioned above.
    public ArrayList<Variable> getVariablesOfSpecificFunction(Function temp) {
        ArrayList<Variable> result = new ArrayList<>();
        Deque<Character> stack = new ArrayDeque<Character>();
        String[] functionLines = temp.getBody().split("\n");
        for (int i = 0; i < functionLines.length; i++) {
            functionLines[i] = functionLines[i].trim();
            String words[] = functionLines[i].trim().split(" ");
            for (int j = 0; j < words.length && j < 2; j++) {
                if ((words[j].equals("class") || words[j].equals("interface") || words[j].equals("abstract"))) {
                    stack.push('C');
                }
            }
            if ((findStringToRegex(lines[i], "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\) *(\\{?|[^;])") || lookingAtStringToRegex(lines[i], "\\w+\\(.+\\)")) && !(lines[i].charAt(0) == '/') && !(lines[i].charAt(1) == '/')) {
                stack.push('F');
            }
            if (lines[i].contains("{")) {
                stack.push('{');
            }
            if (lines[i].contains("}")) {
                stack.pop();
                if (!stack.isEmpty() && (stack.getFirst() == 'C' || stack.getFirst() == 'F')) {
                    stack.pop();
                }
            }
            if (stack.contains('C') || stack.contains('F')) {
                continue;
            }
            if (findStringToRegex(functionLines[i], "(\\w+|.)(\\[\\])? \\w+\\s?(=|;)\\s?") && !functionLines[i].contains("{") && !functionLines[i].contains("}") && !(functionLines[i].charAt(0) == '/') && !(functionLines[i].charAt(1) == '/')) {
                if (words[1].equals("static")) {
                    if (words[3].contains(";")) {
                        words[3] = words[3].substring(0, words[3].indexOf(';'));
                    }
                    Variable var = new Variable(words[3], words[2]);
                    result.add(var);
                } else if (words.length == 3 && !words[1].equals("=")) {
                    if (words[2].contains(";")) {
                        words[2] = words[2].substring(0, words[2].indexOf(';'));
                    }
                    Variable var = new Variable(words[2], words[1]);
                    result.add(var);
                } else if (!words[1].equals("=")) {
                    if (words[0].equals("private") || words[0].equals("public")) {
                        if (words[2].contains(";")) {
                            words[2] = words[2].substring(0, words[2].indexOf(';'));
                        }
                        Variable var = new Variable(words[2], words[1]);
                        result.add(var);
                    } else {
                        if (words[1].contains(";")) {
                            words[1] = words[1].substring(0, words[1].indexOf(';'));
                        }
                        Variable var = new Variable(words[1], words[0]);
                        result.add(var);
                    }
                }
            }
        }
        return result;
    }

    //This function generates the classes names by searching for a specific words such as (class, interface, etc)
    //This function goes through the generic code and calls 2 sub functions to get the functions' and variables' names
    public void generateClassesArray() {
        //AnalyzerPackage.Class temp = null;
        Class temp = null;
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
            String[] words = lines[i].trim().split(" ");
            for (int j = 0; j < words.length && j < 2; j++) {
                if ((words[j].equals("class") || words[j].equals("interface") || words[j].equals("abstract"))) {
                    if (temp != null)
                        classes.add(temp);
                    temp = new AnalyzerPackage.Class();
                    if (words[j + 1].contains("{")) {
                        temp.setName(words[j + 1].substring(0, words[j + 1].length() - 1));
                    } else {
                        temp.setName(words[j + 1]);
                    }
                    if (lines[i].contains("implements")) {
                        for (int k = 0; k < words.length; k++) {
                            if (words[k].equals("implements")) {
                                if (words[k + 1].contains("{")) {
                                    temp.setImplements_(words[k + 1].substring(0, words[k + 1].length() - 1));
                                } else {
                                    temp.setImplements_(words[k + 1]);
                                }
                            }
                        }
                    }
                    if (lines[i].contains("extends")) {
                        for (int k = 0; k < words.length; k++) {
                            if (words[k].equals("extends")) {
                                if (words[k + 1].contains("{")) {
                                    temp.setExtends_(words[k + 1].substring(0, words[k + 1].length() - 1));
                                } else {
                                    temp.setExtends_(words[k + 1]);
                                }
                            }
                        }
                    }
                    getFunctionsOfSpecificClass(i, temp);
                    getVariablesOfSpecificClass(i, temp);
                }
            }
        }
        if (temp != null) {
            classes.add(temp);
        }
    }

    //This is a function build to run regex and match if there was any hits
    public boolean lookingAtStringToRegex(String stringToBeMatched, String regex) {
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(stringToBeMatched);

        return matcher.lookingAt();
    }

    //This is a function build to run regex and match if there was any hits
    public boolean findStringToRegex(String stringToBeMatched, String regex) {
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(stringToBeMatched);

        return matcher.find();
    }

    //This function returns an object of the type Function holding only the "main" function body.
    public Function getMainFunction() {
        String mainFunction = "";
        int startLine = 0;
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
            if (lines[i].contains("public static void main(String[] args)") && lines[i].indexOf("public") == 0) {
                startLine = i;
            }
        }
        Deque<Character> stack = new ArrayDeque<>();
        for (int i = startLine; i < lines.length; i++) {
            if (lines[i].contains("{")) {
                stack.push('{');
            }
            if (lines[i].contains("}")) {
                stack.pop();
            }
            mainFunction += lines[i] + "\n";
            if (stack.isEmpty()) {
                return new Function("main", "void", mainFunction);
            }
        }
        return new Function("main", "void", mainFunction);
    }

    //This function gets the sequence of the calls of the objects and variables created in the main function by using recursion and passes that logic to the sequenceDiagram class to draw it.
    public void getSequence(Function fn, String prevClass) {
        ArrayList<Variable> vars = getVariablesOfSpecificFunction(fn);
        AnalyzerPackage.Class temp;
        for (int i = 0; i < vars.size(); i++) {
            temp = null;
            for (int j = 0; j < classesNames.size(); j++) {
                if (vars.get(i).getDataType().equals(classesNames.get(j))) {
                    if (!timelines.contains(classesNames.get(j))) {
                        timelines.add(classesNames.get(j));
                    }
                    for (int k = 0; k < classes.size(); k++) {
                        if (classes.get(k).getName().equals(classesNames.get(j))) {
                            temp = classes.get(k);
                            break;
                        }
                    }
                }
            }
            String[] functionLines = fn.getBody().split("\n");
            for (int j = 0; j < functionLines.length; j++) {
                functionLines[j] = functionLines[j].trim();
                AnalyzerPackage.Class current = null;
                for (int k = 0; k < classes.size(); k++) {
                    if (classes.get(k).getName().equals(prevClass)) {
                        current = classes.get(k);
                        break;
                    }
                }
                for (int k = 0; k < current.getFunctions().size(); k++) {
                    if (functionLines[j].contains(current.getFunctions().get(k).getFnName() + "(") && !functionLines[j].contains(current.getFunctions().get(k).getDataType())) {
                        FunctionCall fnCallObj = new FunctionCall(current.getFunctions().get(k).getFnName(), timelines.indexOf(current.getName()), timelines.indexOf(current.getName()));
                        functionCalls.add(fnCallObj);
                    }
                }
                if (functionLines[j].contains(vars.get(i).getVarName() + ".")) {
                    functionLines[j] = functionLines[j].substring(functionLines[j].indexOf(vars.get(i).getVarName() + "."));
                    if (temp == null) {
                        break;
                    }
                    String fnCall = functionLines[j].substring(vars.get(i).getVarName().length() + 1, functionLines[j].indexOf("("));
                    for (int k = 0; k < temp.getFunctions().size(); k++) {
                        if (temp.getFunctions().get(k).getFnName().equals(fnCall)) {
                            FunctionCall fnCallObj = new FunctionCall(fnCall, timelines.indexOf(prevClass), timelines.indexOf(temp.getName()));
                            functionCalls.add(fnCallObj);
                            getSequence(temp.getFunctions().get(k), temp.getName());
                            String[] fnBody = temp.getFunctions().get(k).getBody().split("\n");
                            String returnVariable = "";
                            for (int l = 0; l < fnBody.length; l++) {
                                fnBody[l] = fnBody[l].trim();
                                if (fnBody[l].contains("return ") && fnBody[l].contains(";")) {
                                    returnVariable = fnBody[l].substring(7, fnBody[l].indexOf(";"));
                                    break;
                                }
                            }
                            fnCallObj = new FunctionCall(returnVariable, timelines.indexOf(temp.getName()), timelines.indexOf(prevClass));
                            if (!temp.getFunctions().get(k).getDataType().equals("void")) {
                                functionCalls.add(fnCallObj);
                            }
                        }
                    }
                }
            }
        }
    }

    //This function gets the logic of the flowchart and passes it to the flowchart class to draw it
    public void getFlowChart(Function fn) {
        String[] functionLines = fn.getBody().split("\n");
        int level = 0;
        boolean isInsideCondition = false;
        boolean isLeft = false;
        Deque<Character> stack = new ArrayDeque<Character>();
        statements.add(new Statement("START", false, false, 0, false, 0, 0));
        for (int j = 1; j < functionLines.length; j++) {
            functionLines[j] = functionLines[j].trim();
            if ((functionLines[j].length() >= 2 && (functionLines[j].charAt(0) == '/') && (functionLines[j].charAt(1) == '/')) || (functionLines[j].length() == 0)) {
                continue;
            }
            if (lookingAtStringToRegex(functionLines[j], "(if)(\\s)?\\(")) {
                Statement statement = new Statement(functionLines[j].substring(functionLines[j].indexOf("(") + 1, functionLines[j].lastIndexOf(")")), true, isLeft, level, false, 0, 0);
                statements.add(statement);
                stack.push('{');
                level++;
                isLeft = true;
                isInsideCondition = true;
            } else if (lookingAtStringToRegex(functionLines[j], "(for|while)(\\s)?\\(")) {
                Statement statement = new Statement(functionLines[j].substring(functionLines[j].indexOf(";") + 1, functionLines[j].lastIndexOf(";")), true, isLeft, level, true, statements.size(), -1);
                statements.add(statement);
                stack.push('{');
                level++;
                isLeft = true;
                isInsideCondition = true;
            } else {
                if (!functionLines[j].equals("}") && !functionLines[j].contains("else")) {
                    Statement statement = new Statement(functionLines[j], false, isLeft, level, false, 0, 0);
                    statements.add(statement);
                }
            }
            if (functionLines[j].contains("}") && !stack.isEmpty()) {
                stack.pop();
                for (int i = statements.size() - 1; i > 0; i--) {
                    if (statements.get(i).loopEnd == -1) {
                        statements.get(i).loopEnd = statements.size();
                        statements.get(statements.size() - 1).setLoopStart(i);
                    }
                }

                isLeft = false;
                isInsideCondition = false;
                level--;
            }
            if (functionLines[j].contains("else")) {
                stack.push('{');
                isInsideCondition = true;
                isLeft = false;
                level++;
            }
        }
        statements.add(new Statement("END", false, false, 0, false, 0, 0));
    }

    //This function returns the cyclomatic complexity of the code as shown below.
    public int getCyclomaticComplexity() {
        int complexity = 1;
        for (String line : lines) {
            line = line.trim();
            if (line.length() > 1 && line.charAt(0) == '/' && line.charAt(1) == '/') {
                continue;
            }
            if (lookingAtStringToRegex(line, "(if|for|while)(\\s)?\\(") || lookingAtStringToRegex(line, "(}?)(else)((\\s)?\\{)") || lookingAtStringToRegex(line, "(case)(\\s\\w+|\\s\"\\w+\")(:)") || lookingAtStringToRegex(line, "(default)((\\s)?:)") || lookingAtStringToRegex(line, "(return)(((\\s)?.+)?;)") || lookingAtStringToRegex(line, "(break|continue)(\\s+?;)") || (lookingAtStringToRegex(line, "(.+)\\?") && !lookingAtStringToRegex(line, "(\"(.+)?)\\?(.+)?\"")) || lookingAtStringToRegex(line, "(finally)(\\s?\\{)") || lookingAtStringToRegex(line, "(catch)(\\s?\\{|\\s?\\()") || lookingAtStringToRegex(line, "(throw(s)?)(\\s?\\(?)")) {
                complexity++;
            }
            for (String word : line.split(" ")) {
                if (word.contains("||") || word.contains("&&")) {
                    complexity++;
                }
            }
            if (lookingAtStringToRegex(line, "(.+):((.+)?;)")) {
                complexity++;
            }
        }
        return complexity;
    }

    //This function uses the two functions below it to compute the maintainability index of a code.
    public double getMaintainabilityIndex() {
        String[] operators = {"\\|\\|", "\\+=", "-=", ">=", "<=", "\\*=", "\\/=", "==", "\\+\\+", "--", "&&", "!", "~"};
        String[] singleOperators = {"\\+", "-", "=", "\\*", "\\/", ">", "<", "%"};
        Set<String> uniqueOperators = new HashSet<>();
        Set<String> uniqueOperands = new HashSet<>();
        N1 = N2 = 0;

        for (String operator : operators) {
            getOperands(operator, uniqueOperators, uniqueOperands);
        }

        for (String singleOperator : singleOperators) {
            getSingleOperands(singleOperator, uniqueOperators, uniqueOperands);
        }

        double totalNumberOfUniques = uniqueOperands.size() + uniqueOperators.size();
        double totalNumber = N1 + N2;
        double volume = totalNumber * (Math.log(totalNumberOfUniques) / Math.log(2));
        double maintainabilityIndex = 171 - (5.2 * Math.log(volume)) - (0.23 * getCyclomaticComplexity()) - (16.2 * Math.log(getNumberOfLines()));

        return maintainabilityIndex > 0 ? maintainabilityIndex > 100 ? 100 : maintainabilityIndex : 0;
    }

    //This function gets the operands and the number of the operators in the code. double operators only like (++, ==, etc.).
    private void getOperands(String operator, Set<String> uniqueOperators, Set<String> uniqueOperands) {
        String[] split, split2;

        for (String line : lines) {
            line = line.replaceAll("\"([^\"]*)\"", " ");
            line = line.replaceAll("\'([^\']*)\'", " ");
            line = line.replaceAll("<([^< >]*)>", " ");
            line = line.trim();

            split = line.split(operator);
            if (split.length > 1 && line.charAt(0) != '/' && line.charAt(1) != '/') {
                uniqueOperators.add(operator);
                N1 += split.length - 1;
                N2 += split.length;

                for (int i = 0; i < split.length - 1; i++) {
                    split2 = split[i].split(" ");
                    if (split2.length == 0)
                        System.out.println(line);
                    if (split2.length > 1) {
                        uniqueOperands.add(split2[split2.length - 1]);
                    }
                    split2 = split[i + 1].split(" ");
                    if (split2.length > 1) {
                        uniqueOperands.add(split2[1]);
                    }
                }
            }
        }
    }

    //This function gets the operands and the number of the operators in the code. single operators only like (+, =, etc.).
    private void getSingleOperands(String operator, Set<String> uniqueOperators, Set<String> uniqueOperands) {
        String[] split, split2;

        for (String line : lines) {
            line = line.replaceAll("\"([^\"]*)\"", " ");
            line = line.replaceAll("\'([^\']*)\'", " ");
            line = line.replaceAll("<([^< >]*)>", " ");
            line = line.trim();

            split = line.split(operator);

            if (split.length > 1 && line.charAt(0) != '/' && line.charAt(1) != '/') {
                uniqueOperators.add(operator.length() > 1 ? String.valueOf(operator.charAt(1)) : String.valueOf(operator.charAt(0)));

                for (int index = line.indexOf(operator.length() > 1 ? operator.charAt(1) : operator.charAt(0)); index >= 0; index = line.indexOf(operator.length() > 1 ? operator.charAt(1) : operator.charAt(0), index + 1)) {
                    if (line.charAt(index - 1) == '!' || line.charAt(index + 1) == '=' || line.charAt(index - 1) == '=' || line.charAt(index + 1) == '+' || line.charAt(index - 1) == '+' || line.charAt(index + 1) == '-' || line.charAt(index - 1) == '-' || line.charAt(index + 1) == '/' || line.charAt(index - 1) == '/' || line.charAt(index + 1) == '*' || line.charAt(index - 1) == '*' || line.charAt(index + 1) == '>' || line.charAt(index - 1) == '>' || line.charAt(index + 1) == '<' || line.charAt(index - 1) == '<') {
                        continue;
                    }

                    N1 += split.length - 1;
                    N2 += split.length;

                    for (int i = 0; i < split.length - 1; i++) {
                        split2 = split[i].split(" ");
                        if (split2.length > 1) {
                            uniqueOperands.add(split2[split2.length - 1]);
                        }

                        split2 = split[i + 1].split(" ");
                        if (split2.length > 1) {
                            uniqueOperands.add(split2[1]);
                        }
                    }
                    break;
                }
            }
        }
    }
}