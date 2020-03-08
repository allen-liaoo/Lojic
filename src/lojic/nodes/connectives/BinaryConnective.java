package lojic.nodes.connectives;

import lojic.DefaultFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @author AlienIdeology
 *
 * A logical operator that accepts {@code boolean} values of
 * the atom or formula to its left and right and returns a {@code boolean} value
 *
 * @see DefaultFactory for a list of the default binary connectives
 */
public final class BinaryConnective extends Connective {

    /**
     * This is the possible combinations of values to which the atom or formula on this binary connective's right and left can have
     * @see Connective#getPossibleTruths() for more information
     */
    public static final BoolPair[] POSSIBLE_INPUT_VALUES = new BoolPair[] {
            new BoolPair(true, true),
            new BoolPair(true, false),
            new BoolPair(false, true),
            new BoolPair(false, false)
    };

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
     * Get the {@link BiPredicate} underlying this binary connective
     *
     * @return The bi-predicate object
     */
    public BiPredicate<Boolean, Boolean> getBiPredicate() {
        return computation;
    }

    /**
     * Get all possible combination of truth values to connective's right and left
     * by the truth value of this connective
     *
     * For example, if this Binary Connective is material implication {@code →},
     * then {@code #getPossibleTruthsByValue(true)} will return
     * {@link BoolPair}s of {BoolPair[true, true], BoolPair[false, true], BoolPair[false, false]}
     *
     * On the other-hand, {@code #getPossibleTruthsByValue(false)} will return
     * a {@link BoolPair} of {BoolPair[true, false]}
     *
     * @see BoolPair for the lite object used to wrap pair values
     *
     * @param value The truth value of this connective
     * @return The pairs of truth values
     */
    public BoolPair[] getPossibleTruthsByValue(boolean value) {
        boolean[] pTs = getPossibleTruths();

        List<BoolPair> res = new ArrayList<>();

        for (int i = 0; i < pTs.length; i++) {
            if (pTs[i] == value)
                res.add(new BoolPair(POSSIBLE_INPUT_VALUES[i].left(), POSSIBLE_INPUT_VALUES[i].right()));
        }
        return res.toArray(new BoolPair[0]);
    }

    /**
     * Compute the truth value based on the nodes on its left of right.
     *
     * @param left left truth value
     * @param right right truth value
     * @return The truth value
     */
    boolean computeTruth(boolean left, boolean right) {
        return computation.test(left, right);
    }

    @Override
    public boolean[] getPossibleTruths() {
        boolean[] truths = new boolean[4];

        for (int i = 0; i < truths.length; i++) {
            truths[i] = computation.test(POSSIBLE_INPUT_VALUES[i].left(), POSSIBLE_INPUT_VALUES[i].right());
        }
        return truths;
    }

    /**
     * A lite wrapper for containing two boolean values
     */
    public static class BoolPair {
        private final boolean left;
        private final boolean right;

        public BoolPair(boolean left, boolean right) {
            this.left = left;
            this.right = right;
        }

        public boolean left() {
            return left;
        }

        public boolean right() {
            return right;
        }

    }

}
