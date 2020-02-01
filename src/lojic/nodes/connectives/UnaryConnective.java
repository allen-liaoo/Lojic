package lojic.nodes.connectives;

/**
 * @author AlienIdeology
 */
public abstract class UnaryConnective extends Connective {


    public UnaryConnective(String official, int precedence, String... others) {
        super(official, precedence, others);
    }

    /**
     * Compute the truth value based on the node on its right.
     *
     * @param right right truth value
     * @return This truth value
     */
    public abstract boolean computeTruth(boolean right);

}
