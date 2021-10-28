package AnalyzerPackage;

import java.io.*;
import java.util.ArrayList;

//This class reads all the selected files by the user.
public class FilesUtil {

    public String readFiles(ArrayList<String> filePaths) {
        String fileAsString = "";
        try {
            for (int i = 0; i < filePaths.size(); i++) {
                BufferedReader reader = new BufferedReader(new FileReader(filePaths.get(i)));
                while (true) {
                    String s = reader.readLine();
                    if (s == null) {
                        break;
                    }
                    fileAsString += (s + "\n");
                }
                reader.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileAsString;
    }

    public ArrayList<String> readFolder(String path) {
        File file = new File(path);
        String[] files = file.list();
        if (files == null) {
            System.out.println("there is no files or directories in current path");
            return null;
        }
        ArrayList<String> filesList = new ArrayList<>();
        for (String s : files) {
            if (s.indexOf(".java") > 0 || s.indexOf(".txt") > 0) {
                filesList.add(path + "\\" + s);
            }
        }
        return filesList;
    }
}
