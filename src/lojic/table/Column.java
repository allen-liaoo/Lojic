package lojic.table;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import lojic.nodes.truthapts.Atom;
import lojic.nodes.truthapts.Formula;
import lojic.nodes.truthapts.TruthApt;

/**
 * @author AlienIdeology
 *
 * A column of the {@link TruthTable},
 * which contains a {@link DetailSetting},
 * a {@link Formula} and/or {@link Atom} (See {@link Column#getFormula()} and {@link Column#getAtom()},
 * and an array of boolean values ({@code boolean[] values}).
 */
public class Column {

    private final DetailSetting setting;
    private final Formula formula;
    private Atom atom;
    private final boolean[] values;
    private final boolean[] subColumnLeft;
    private final boolean[] subColumnRight;

    Column(DetailSetting setting, Formula formula, boolean[] values) {
        this(setting, formula, values, null, null);
    }

    Column(DetailSetting setting, Atom atom, boolean[] values) {
        this(setting, null, values, null, null);
        this.atom = atom;
    }

    Column(DetailSetting setting, Formula formula, boolean[] values,
           boolean[] subColumnLeft, boolean[] subColumnRight) {
        this.setting = setting;
        this.formula = formula;
        this.atom = null;
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
     * Get the name of this column, which is the string representation of this column's formula or atom
     *
     * @return The name of this column
     */
    public String getName() {
        return formula != null ? formula.getString() : atom.toString();
    }

    /**
     * Return the {@link Formula} of this column, or null if the column denotes an atom
     *
     * @return The formula
     */
    @Nullable
    public Formula getFormula() {
        return formula;
    }

    /**
     * Get the {@link Atom} object of this column, or null if the column denotes a formula
     *
     * @return The atom
     */
    @Nullable
    public Atom getAtom() {
        return atom;
    }

    /**
     * Get the {@link TruthApt} object of this column, which is never null
     *
     * @return The truth-apt object
     */
    @NotNull
    public TruthApt getTruthApt() {
        return formula != null ? formula : atom;
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

    public boolean hasSubColumn() {
        return subColumnLeft != null || subColumnRight != null;
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