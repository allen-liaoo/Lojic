package lojic.nodes.truthapts;

/**
 * @author AlienIdeology
 *
 * Nodes with truth-table values
 */
public interface TruthApt {

    /**
     * Set the truth values
     * Used to set the column of booleans for the truth-table
     *
     * @param truths The truth values
     */
    void setTruths(boolean[] truths);

    /**
     * Get the truth values
     *
     * @return The truth values
     */
    boolean[] getTruths();

    /**
     * Check if the truth values are set
     *
     * @return true if set, false if not
     */
    boolean isSet();

}
