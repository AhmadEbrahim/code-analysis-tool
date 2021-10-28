package AnalyzerPackage;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        ArrayList<String> filesList = new ArrayList<>();
        filesList.add("REPLACETHISWITHPATH\\src\\AnalyzerPackage\\Analyzer.java");
//        filesList.add("REPLACETHISWITHPATH\\src\\AnalyzerPackage\\Class.java");
////        filesList.add("REPLACETHISWITHPATH\\src\\AnalyzerPackage\\Function.java");
////        filesList.add("REPLACETHISWITHPATH\\src\\AnalyzerPackage\\Variable.java");
////        filesList.add("REPLACETHISWITHPATH\\src\\AnalyzerPackage\\Main.java");
//        //Analyzer analyze = new Analyzer("REPLACETHISWITHPATH\\src\\AnalyzerPackage");
        Analyzer analyze = new Analyzer(filesList);

//        System.out.println(analyze.getMaintainabilityIndex());
//        Slicer slicer = new Slicer(filesList);
//        System.out.println(slicer.dynamicSlicing(28, "a", 5));
//        System.out.println(slicer.staticSlicing(28, "a"));
        analyze.generateClassesArray();
        System.out.println(analyze.getNumberOfClasses());
        System.out.println(analyze.getNumberOfLines());
//
////        for (int i = 0; i < 1; i++) {
////            analyze.getSequence(analyze.getMainFunction(), "Main");
////        }
        if (5 > 6) {
            System.out.println("test1");
            System.out.println("test1");
            System.out.println("test1");
            if (5 > 6) {
                System.out.println("test1");
                System.out.println("test1");
                System.out.println("test1");
            } else {
                System.out.println("test2");
                System.out.println("test2");
                System.out.println("test2");
            }
        } else {
            System.out.println("test3");
            System.out.println("test3");
            System.out.println("test3");
        }
//        ArrayList<String> classNames = analyze.getClassesNames();
//
//        for (int i = 0; i < 10; i++) {
//            System.out.println("ForTest");
//            System.out.println("ForTest");
//            System.out.println("ForTest");
//            System.out.println("ForTest");
//            System.out.println("ForTest");
//        }
//        //analyze.printClassesNames();
//        ArrayList<String> classNames2 = analyze.getClassesNames();

//        analyze.getNumberOfFunctions();
//        System.out.println(analyze.getCyclomaticComplexity());
//        for (int i = 0 ; i < classNames.size(); i++)
//        {
//            System.out.println("Number of functions is in " + classNames.get(i)  + " is " + analyze.getNumberOfFunctions(classNames.get(i)));
//        }


//        System.out.println("Number of functions is " + analyze.getNumberOfFunctions());
//        analyze.printFunctionNames();


        //analyze.generateClassesArray();
        //System.out.println(analyze.classes.get(0).getVariables().get(1).getDataType() + analyze.classes.get(0).getVariables().get(1).getVarName());
    }
}