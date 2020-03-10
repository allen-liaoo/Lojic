package lojic.argument.inference_rule;

import lojic.argument.Line;

public class Block {

    private final Line start;
    private final Line end;

    private final Line dischargeLine;

    public Block(Line start, Line end, Line dischargeLine) {
        this.start = start;
        this.end = end;
        this.dischargeLine = dischargeLine;
    }


    public Line getStart() {
        return start;
    }

    public Line getEnd() {
        return end;
    }

    public Line getDischargeLine() {
        return dischargeLine;
    }

    @Override
    public String toString() {
        return "Block{" +
                "start=" + start +
                ", end=" + end +
                ", dischargeLine=" + dischargeLine +
                '}';
    }
}
