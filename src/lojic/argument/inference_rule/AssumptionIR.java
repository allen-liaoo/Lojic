package lojic.argument.inference_rule;

import lojic.argument.Argument;
import lojic.argument.InferenceException;
import lojic.argument.Line;

public final class AssumptionIR extends InferenceRule {

    private static final String ABBREVIATION = "A";

    public AssumptionIR() {
        super(ABBREVIATION);
    }

    @Override
    protected void validity(Line line) throws InferenceException {
        // Cannot have dependency lines
        if (line.hasDependencyNumbers())
            throw new InferenceException(line, this, InferenceException.Type.MISSING_DEPENDENCY_NUM, "An assumption");

        Block block = getBlock(line);

        // Must have lines beneath an assumption
        if (block.getStart() == block.getEnd())
            throw new InferenceException(line, this, InferenceException.Type.MISSING_LINE, "lines beneath the assumption");

        if (block.getEnd() == null)
            throw new InferenceException(line, this, InferenceException.Type.MISSING_LINE, "conclusion line for the assumption");

        // Cannot have no corresponding discharge rule
        //      Do not check for discharge rule's validity
        if (block.getDischargeLine() == null)
            throw new InferenceException(line, this, InferenceException.Type.MISSING_LINE, "discharge inference line for the assumption");
    }

    /**
     * Get the {@link Block} object of this assumption, which represents a scope in an {@link Argument}.
     *
     * A {@link Block} is consisted of three {@link Line} objects: the start(assumption) line, end line, and the line
     * of the discharge inference rule.
     * 1. If there is no line after the starting line, this returns {@code start, null, null}.
     * 2. If no ending line is found, then this returns {@code start, null, null}.
     * 3. If no line of discharge inference rule is found, then this returns {@code start, null, end}.
     *
     * This method calculates the block and its scope based on purely the assumptions below it and the expected
     * number of assumptions ({@link DischargeIR#getAssumptionCount()} its discharge rule demands.
     * There is no guarantee that invoking this method for each assumption will return a block with the same scope.
     * The safest, most correct block information can only be obtained by invoking this method on the first line of assumption
     * in any argument.
     *
     * @param line The assumption line
     * @return The block
     */
    public Block getBlock(Line line) {
        int count = line.getNumber() + 1;
        Argument arg = line.getArgument();

        if (arg.lineCount() < count)
            return new Block(line, null, null);

        // counter of number of assumptions met, including this line
        int asmCount = 1;

        Line endL;
        Line disL = null;

        do {
            endL = arg.getLine(count);
            InferenceRule rule = endL.getInferenceRule(); // TODO: missing inference rule

            if (rule instanceof AssumptionIR) {
                asmCount++;
            } else if (rule instanceof DischargeIR) {
                int ac = ((DischargeIR) rule).getAssumptionCount();
                asmCount -= ac;

                // If a discharge rule has discharged more assumptions than there is
                // Then asmCount < 0
                // If asmCount = -1, discharge rule has discharged 1 more

                if (asmCount <= 0) {

                    disL = endL;

                    int expAsm = asmCount == 0
                            ? ac : ac + asmCount; // expected # of assumptions to meet
                    int lineC = count - 1;

                    // TODO: Debug finding end index
                    // Read backwards to find the end index
                    while (expAsm != 1) {
                        Line line1 = arg.getLine(lineC);
                        InferenceRule rule1 = line1.getInferenceRule();

                        if (rule1 instanceof AssumptionIR)
                            expAsm--;
                        else if (rule1 instanceof DischargeIR)
                            expAsm += ((DischargeIR) rule1).getAssumptionCount();
                        lineC--;
                    }

                    endL = arg.getLine(lineC);

                    // If the end line is same line as the assumption, make it null
                    if (endL == line)
                        endL = null;

                    break;
                }
            }
            count++;
        } while (arg.lineCount() >= count);

        if (asmCount > 0)
            return new Block(line, endL, null);

        return new Block(line, endL, disL);
    }

}
