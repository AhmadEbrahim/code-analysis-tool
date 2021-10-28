package AnalyzerPackage;

public class Variable {
    private String varName;
    private String dataType;

    public Variable(String varName, String dataType) {
        this.varName = varName;
        this.dataType = dataType;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
