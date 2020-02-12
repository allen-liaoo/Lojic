package lojic.nodes.truthapts;

/**
 * @author AlienIdeology
 *
 * Nodes with {@code boolean } truth values,
 * which would be set by a {@link lojic.table.TruthCalculator}
 * and shown on a {@link lojic.table.TruthTable}
 */
public interface TruthApt {

    /**
     * Get the truth values
     *
     * @return The truth values
     */
    boolean[] getTruths();

    /**
     * Check if this truth-apt object has all {@code true} values
     *
     * @return True if this truth-apt object is always true
     */
    default boolean isTautology() {
        boolean isTautology = true;
        for (boolean val : getTruths()) {
            if (!val) isTautology = false;
        }
        return isTautology;
    }

    /**
     * Check if this truth-apt object has all {@code false} values
     *
     * @return True if this truth-apt object is always false
     */
    default boolean isContradiction() {
        boolean isContradiction = true;
        for (boolean val : getTruths()) {
            if (val) isContradiction = false;
        }
        return isContradiction;
    }


    /**
     * Check if the truth values are set
     * When an instance of  {@link lojic.table.TruthTable} is created, all truth apt objects will already be set
     * This method is for the Lojic library's internal use only, users should ignore this
     *
     * @return true if set, false if not
     */
    boolean isSet();

    /**
     * Set the truth values
     * Used to set the column of booleans for the truth-table
     * This method is for the Lojic library's internal use only, users should ignore this
     *
     * @param truths The truth values
     */
    void setTruths(boolean[] truths);

}
