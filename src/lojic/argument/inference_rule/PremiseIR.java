package lojic.argument.inference_rule;

import lojic.argument.InferenceException;
import lojic.argument.Line;

public final class PremiseIR extends InferenceRule {

    private static final String ABBREVIATION = "P";

    public PremiseIR() {
        super(ABBREVIATION);
    }

    @Override
    public void validity(Line line) throws InferenceException {
        // Cannot have dependency lines
        if (line.hasDependencyNumbers())
            throw new InferenceException(line, this, InferenceException.Type.MISSING_DEPENDENCY_NUM, "A premise");

        // Cannot be in the scope of any assumptions
        // TODO: Debug premise scope

        // counter of number of assumptions met, including this line
        int asmCount = 0;
        int count = 1;

        while (count < line.getNumber()) {
            Line line1 = line.getArgument().getLine(count);
            InferenceRule rule = line1.getInferenceRule();
            if (rule instanceof AssumptionIR) {
                asmCount++;
            } else if (rule instanceof DischargeIR) {
                asmCount -= ((DischargeIR) rule).getAssumptionCount();
            }
            count++;
        }
        if (asmCount > 0)
            throw new InferenceException(line, this, InferenceException.Type.PREMISE_IN_ASM);
    }

}
