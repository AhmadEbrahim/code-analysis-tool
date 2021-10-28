package AnalyzerPackage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Scanner;

public class testing {
    private ArrayList<String> timelines = new ArrayList<>();
    private Deque<FunctionCall> functionCalls = new ArrayDeque<>();
    private ArrayList<Statement> statements = new ArrayList<>();
    private int N1 = 0; // Total number of operators
    private int N2 = 0; // Total number of operands
    private ArrayList<String> classesNames = new ArrayList<>();
    private String[] lines;


    testing(String folderPath) {
        FilesUtil folder = new FilesUtil();
        ArrayList<String> filePaths = folder.readFolder(folderPath);
    }

    public int getNumberOfClasses() {
        int sum = 0;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a Number: ");
        int num1 = sc.nextInt();

        System.out.println("Enter a Number: ");
        int num2 = sc.nextInt();

        sc.close();
        if (num1 > 0) {
            sum = sum + num1;
        }
        if (num2 > 0) {
            sum = sum + num2;
        }

        System.out.println("num1 Number: " + num1 );
        System.out.println("num2 Number: " + num2 );
        System.out.println("sum Number");

//        int a;
//        int sum = 0;
//        a = 5;
//        if (sum < 4) {
//            System.out.println("TEST");
//            sum = a ;
//        }
        //        String path = "", s = "";
//        classesNames.add(path + "\\" + s);
//        String[] words = {"sosta", "tmn"};
////        String s = "asdasdasdasd";
//        char sa = 'a';
//        boolean aa = 5 < 3;
//        int x = 5;
//        x--;
//
//        int classes = 0;
//
//        x = 5/2;

        //            for (int j '=' 0; j < words.length - 2; j++) {
//                if ((words[j].equals("class") || words[j].equals("interface") || words[j].equals("abstract")) && ((words[j + 2].equals("{") || words[j + 1].charAt(words[j + 1].length() - 1) == '{') || words[j + 2].equals("implements") || words[j + 2].equals("extends")) && !lines[i].contains("\"")) {
//                    classesNames.add(words[j + 1]);
//                } else if ((words[j].equals("class") || words[j].equals("interface") || words[j].equals("abstract")) && words[j + 1].charAt(words[j + 1].length() - 1) == '{') {
//                    classesNames.add(words[j + 1].substring(0, words[j + 1].length() - 1));
//                }
//            }
//            for (int j "=" 0; j < words.length - 1; j++) {
//                if ((words[j].equals("class") || words[j].equals("interface") || words[j].equals("abstract")) && lines[i + 1].equals("{")) {
//                    classesNames.add(words[j + 1]);
//                } else if ((words[j].equals("class") || words[j].equals("interface") || words[j].equals("abstract")) && words[j + 1].charAt(words[j + 1].length() - 1) == '{') {
//                    classesNames.add(words[j + 1].substring(0, words[j + 1].length() - 1));
//                }
//            }
        return sum;
    }
}