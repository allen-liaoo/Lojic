package lojic.nodes.connectives;

import lojic.DefaultFactory;

import java.util.function.Predicate;

/**
 * @author AlienIdeology
 *
 * A logical operator that accepts {@code boolean} value of
 * the atom or formula to its right and returns a {@code boolean} value
 *
 * @see DefaultFactory#NEG for the only default unary connective
 */
public class UnaryConnective extends Connective {

    /**
     * This is the possible values to which the atom or formula on this unary connective's right can have
     * @see Connective#getPossibleTruths() for more information
     */
    public static final boolean[] POSSIBLE_INPUT_VALUES = new boolean[]{true, false};

    private final Predicate<Boolean> computation;

    /**
     * Constructor of a unary connective
     * For an example of how to create an instance of an unary connective,
     * @see DefaultFactory#NEG
     *
     * @param computation The lambda expression used to compute a boolean value based on the truth value
     *                    on the right of this connective
     * @param official The official symbol which the parser recognizes
     * @param precedence The order of precedence. The lower it is, the higher the precedence is.
     *                   This value ranges from {@code 1~5}.
     * @param others Other symbols for the connective
     */
    public UnaryConnective(Predicate<Boolean> computation, String official, int precedence, String... others) {
        super(official, precedence, others);
        this.computation = computation;
    }

    /**
     * Get the {@link Predicate} underlying this unary connective
     *
     * @return The predicate object
     */
    public Predicate<Boolean> getPredicate() {
        return computation;
    }

    /**
     * Compute the truth value based on the node on its right.
     *
     * @param right right truth value
     * @return The truth value
     */
    boolean computeTruth(boolean right) {
        return computation.test(right);
    }

    @Override
    public boolean[] getPossibleTruths() {
        boolean[] truths = new boolean[2];

        for (int i = 0; i < truths.length; i++) {
            truths[i] = computation.test(POSSIBLE_INPUT_VALUES[i]);
        }
        return truths;
    }

}
