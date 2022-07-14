package KnapsackClasses;

import java.util.List;

public class ValueContentPair {
    private int value;
    private List<Boolean> content;

    public ValueContentPair(int value, List<Boolean> content) {
        this.value = value;
        this.content = content;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void addValue(int value) {
        this.value += value;
    }

    public List<Boolean> getContent() {
        return content;
    }

    public void setContent(List<Boolean> content) {
        this.content = content;
    }
}
