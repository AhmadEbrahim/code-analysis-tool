package AnalyzerPackage;

import java.util.ArrayList;

public class Class
{
    private String Name;
    private ArrayList<Function> functions;
    private ArrayList<Variable> variables;
    private String implements_;
    private String extends_;

    public String getExtends_() {
        return extends_;
    }

    public void setExtends_(String extends_) {
        this.extends_ = extends_;
    }

    public String getImplements_() {
        return implements_;
    }

    public void setImplements_(String implements_) {
        this.implements_ = implements_;
    }

    public Class() {
        this.functions = new ArrayList<>();
        this.variables = new ArrayList<>();
    }

    public Class(String name, ArrayList<Function> functions, ArrayList<Variable> variables) {
        Name = name;
        this.functions = functions;
        this.variables = variables;
        implements_ = null;
        extends_ = null;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(ArrayList<Function> functions) {
        this.functions = functions;
    }

    public ArrayList<Variable> getVariables() {
        return variables;
    }

    public void setVariables(ArrayList<Variable> variables) {
        this.variables = variables;
    }
}