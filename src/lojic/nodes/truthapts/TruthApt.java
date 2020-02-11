package lojic.nodes.truthapts;

/**
 * @author AlienIdeology
 *
 * Nodes with truth-table values
 */
public interface TruthApt {

    /**
     * Get the truth values
     *
     * @return The truth values
     */
    boolean[] getTruths();

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
