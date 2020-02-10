package lojic.table;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import lojic.nodes.Node;
import lojic.nodes.truthapts.TruthApt;

/**
 * @author AlienIdeology
 *
 * A column of the {@link TruthTable},
 * which contains a {@link DetailSetting},
 * a {@link Node} and/or {@link TruthApt} (See {@link Column#getNode()},
 * and an array of boolean values ({@code boolean[] values}).
 */
public class Column {

    private final DetailSetting setting;
    private final Node node;
    private final TruthApt truthApt;
    private final boolean[] values;
    private final boolean[] subColumnLeft;
    private final boolean[] subColumnRight;

    Column(DetailSetting setting, Node node, TruthApt truthApt, boolean[] values) {
        this.setting = setting;
        this.node = node;
        this.truthApt = truthApt;
        this.values = values;
        this.subColumnLeft = null;
        this.subColumnRight = null;
    }

    Column(DetailSetting setting, Node node, TruthApt truthApt, boolean[] values,
           boolean[] subColumnLeft, boolean[] subColumnRight) {
        this.setting = setting;
        this.node = node;
        this.truthApt = truthApt;
        this.values = values;
        this.subColumnLeft = subColumnLeft;
        this.subColumnRight = subColumnRight;
    }

    /**
     * Get the table's {@link DetailSetting} which this column is a type of
     *
     * @return The column's detail setting
     */
    public DetailSetting getSetting() {
        return setting;
    }

    /**
     * Return the {@link Node} of this column, or null if the column denotes an atom
     *
     * @return The node
     */
    @Nullable
    public Node getNode() {
        return node;
    }

    /**
     * Get the {@link TruthApt} object of this column, which is never null
     *
     * @return The truth apt object
     */
    @NotNull
    public TruthApt getTruthApt() {
        return truthApt;
    }

    /**
     * Get the boolean values of this column
     * This only includes the main boolean values. If there are sub-columns under this column,
     * use {@link #getSubColumnLeft()} and {@link #getSubColumnRight()}
     *
     * @return The boolean values of this column
     */
    public boolean[] getValues() {
        return values;
    }

    /**
     * Given that this column denotes a formula, and the detail setting is set to show sub columns,
     * get the array of boolean values to the left of this formula's BINARY connective.
     *
     * This returns {@code null} if the detail setting is NOT set to show sub columns,
     * or if this formula represents a formula with an UNARY connective,
     * or if this column does not denote a formula.
     *
     * @return Nullable boolean values on the left of a formula
     */
    @Nullable
    public boolean[] getSubColumnLeft() {
        return subColumnLeft;
    }

    /**
     * Given that this column denotes a formula, and the detail setting is set to show sub columns,
     * get the array of boolean values to the right of this formula's (binary or unary) connective.
     *
     * This returns {@code null} the detail setting is NOT set to show sub columns,
     * or if this column does not denote a formula.
     *
     * @return Nullable boolean values on the right of a formula
     */
    @Nullable
    public boolean[] getSubColumnRight() {
        return subColumnRight;
    }

}