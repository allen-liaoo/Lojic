package lojic.nodes.connectives;

/**
 * @author AlienIdeology
 */
@FunctionalInterface
public interface UnaryConnective {

    /**
     * Compute the truth value based on the node on its right.
     *
     * @param right right truth value
     * @return This truth value
     */
    boolean computeTruth(boolean right);

}
