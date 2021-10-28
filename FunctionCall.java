package AnalyzerPackage;

public class FunctionCall {
    String functionName;
    int indexInSequenceFrom;
    int indexInSequenceTo;

    public FunctionCall(String functionName, int indexInSequenceFrom, int indexInSequenceTo) {
        this.functionName = functionName;
        this.indexInSequenceFrom = indexInSequenceFrom;
        this.indexInSequenceTo = indexInSequenceTo;
    }
}
