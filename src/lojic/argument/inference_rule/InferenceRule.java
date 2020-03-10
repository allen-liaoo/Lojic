package lojic.argument.inference_rule;


import lojic.argument.Argument;
import lojic.argument.InferenceException;
import lojic.argument.Line;

/**
 * @author AlienIdeology
 *
 * InferenceRule represents rule of inference in any given {@link Line} of an {@link Argument}
 */
public abstract class InferenceRule {

    protected final String abbreviation;

    public InferenceRule(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public void checkProvability(Line line) throws InferenceException {
        for (Integer dpy : line.getDependencyNumbers()) {
            if (dpy >= line.getNumber())
                throw new InferenceException(line, this, InferenceException.Type.DEPENDENCY_NUM_TOO_BIG);

            if (dpy < 1)
                throw new InferenceException(line, this, InferenceException.Type.DEPENDENCY_NUM_TOO_SMALL);
        }
        validity(line);
    }

    protected abstract void validity(Line line) throws InferenceException;

    /*
        Each syntactic validity check should include:
        1. Valid Form
            a. Context-specific, but includes relevant connectives
        2. Valid dependency number
            a. Does not refer to numbers later than the current line number
        3. Valid scope for all sub-formulas/dependencies involved
            a. Does not access lines from inaccessible scopes (parallel assumptions, children assumptions)

        Premise:
        1. NA
        2. No dependency number
        3. Should not be within any assumption

        Assumption:
        1. NA
        2. No dependency number
        3. Exists a corresponding discharge rule

        Discharge:
        1. Context-specific
            a. Right number of assumptions discharged
        2. Dependency should include premises, assumptions, assumption conclusions
        3. Should exist corresponding assumption rule(s)

        Other:
        1. Context-specific
        2. Right dependencies involved
        3. Right scope
     */

}
