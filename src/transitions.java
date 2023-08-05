import java.util.Objects;

public class transitions {
    private String startState;
    private String input;
    private String direction;
    private String endState;

    public transitions(String startState, String input, String direction, String endState) {
        this.startState = startState;
        this.input = input;
        this.direction = direction;
        this.endState = endState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        transitions that = (transitions) o;
        return Objects.equals(startState, that.startState) &&
                Objects.equals(input, that.input) &&
                Objects.equals(direction, that.direction) &&
                Objects.equals(endState, that.endState);
    }

    public String toString(){
        return startState + " " + input + " " + direction + " " + endState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startState, input, direction, endState);
    }

    public String getStartState() {
        return startState;
    }

    public String getInput() {
        return input;
    }

    public String getDirection() {
        return direction;
    }

    public String getEndState() {
        return endState;
    }
}