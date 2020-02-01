package lojic.nodes.connectives;

/**
 * @author AlienIdeology
 */
public abstract class BinaryConnective extends Connective {


    public BinaryConnective(String official, int precedence, String... others) {
        super(official, precedence, others);
    }

    /**
     * Compute the truth value based on the nodes on its left of right.
     *
     * @param left left truth value
     * @param right right truth value
     * @return This truth value
     */
    public abstract boolean computeTruth(boolean left, boolean right);

}
