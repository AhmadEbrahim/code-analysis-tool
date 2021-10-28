package AnalyzerPackage;

public class Function {
    private String fnName;
    private String dataType;
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Function(String fnName, String dataType, String body) {
        this.fnName = fnName;
        this.dataType = dataType;
        this.body = body;
    }

    public String getFnName() {
        return fnName;
    }

    public void setFnName(String fnName) {
        this.fnName = fnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
