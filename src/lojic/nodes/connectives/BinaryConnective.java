package lojic.nodes.connectives;

import lojic.DefaultFactory;

import java.util.function.BiPredicate;

/**
 * @author AlienIdeology
 *
 * A logical operator that accepts {@code boolean} values of
 * the atom or formula to its left and right and returns a {@code boolean} value
 *
 * @see DefaultFactory for a list of the default binary connectives
 */
public class BinaryConnective extends Connective {

    private final BiPredicate<Boolean, Boolean> computation;

    /**
     * Constructor of a binary connective
     * For examples of how to create instances of binary connectives,
     * @see DefaultFactory for its binary connectives
     *
     * @param computation The lambda expression used to compute a boolean value based on truth values
     *                    on the left and right of this connective
     * @param official The official symbol which the parser recognizes
     * @param precedence The order of precedence. The lower it is, the higher the precedence is.
     *                   This value ranges from {@code 1~5}.
     * @param others Other symbols for the connective
     */
    public BinaryConnective(BiPredicate<Boolean, Boolean> computation, String official, int precedence, String... others) {
        super(official, precedence, others);
        this.computation = computation;
    }

    /**
     * Compute the truth value based on the nodes on its left of right.
     *
     * @param left left truth value
     * @param right right truth value
     * @return The truth value
     */
    public boolean computeTruth(boolean left, boolean right) {
        return computation.test(left, right);
    }

    /**
     * Get the {@link BiPredicate} underlying this binary connective
     *
     * @return The bi-predicate object
     */
    public BiPredicate<Boolean, Boolean> getBiPredicate() {
        return computation;
    }

    @Override
    public boolean[] getPossibleTruths() {
        boolean[] truths = new boolean[4];
        boolean[] truthsL = new boolean[]{true, true, false, false};
        boolean[] truthsR = new boolean[]{true, false, true, false};

        for (int i = 0; i < truths.length; i++) {
            truths[i] = computation.test(truthsL[i], truthsR[i]);
        }
        return truths;
    }

}
