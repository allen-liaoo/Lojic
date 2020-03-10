package lojic.argument.inference_rule;

import lojic.argument.Line;

public abstract class DischargeIR extends InferenceRule {

    private final int assumpCount;

    public DischargeIR(String abbreviation, int assumpCount) {
        super(abbreviation);

        if (assumpCount < 1)
            throw new IllegalArgumentException("A DischargeIR must be expecting at least 1 assumption line!");

        this.assumpCount = assumpCount;
    }

    public Block getBlock(Line line) {
        return null;
    }

    public int getAssumptionCount() {
        return assumpCount;
    }

}
