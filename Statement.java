package AnalyzerPackage;

public class Statement {
    String Statement;
    boolean isCondition;
    boolean isConditionLeft;
    int level;
    boolean isLoop;
    int loopStart;
    int loopEnd;

    public Statement(String statement, boolean isCondition, boolean isConditionLeft, int level, boolean isLoop, int loopStart, int loopEnd) {
        Statement = statement;
        this.isCondition = isCondition;
        this.isConditionLeft = isConditionLeft;
        this.level = level;
        this.isLoop = isLoop;
        this.loopStart = loopStart;
        this.loopEnd = loopEnd;
    }

    public int getLoopStart() {
        return loopStart;
    }

    public void setLoopStart(int loopStart) {
        this.loopStart = loopStart;
    }

    public int getLoopEnd() {
        return loopEnd;
    }

    public void setLoopEnd(int loopEnd) {
        this.loopEnd = loopEnd;
    }

    public String getStatement() {
        return Statement;
    }

    public void setStatement(String statement) {
        Statement = statement;
    }

    public boolean isCondition() {
        return isCondition;
    }

    public void setCondition(boolean condition) {
        isCondition = condition;
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
    }

    public boolean isConditionLeft() {
        return isConditionLeft;
    }

    public int getLevel() {
        return level;
    }
}
