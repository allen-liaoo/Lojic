package lojic.nodes.connectives;

/**
 * @author AlienIdeology
 */
@FunctionalInterface
public interface BinaryConnective {

    /**
     * Compute the truth value based on the nodes on its left of right.
     *
     * @param left left truth value
     * @param right right truth value
     * @return This truth value
     */
    boolean computeTruth(boolean left, boolean right);

}
