package lojic.table;

import lojic.nodes.truthapts.Atom;
import lojic.nodes.truthapts.Formula;
import lojic.nodes.truthapts.TruthApt;

/**
 * @author AlienIdeology
 *
 * A column of the {@link TruthTable},
 * which contains a {@link ColumnType},
 * a {@link Formula} and/or {@link Atom} (See {@link Column#getFormula()} and {@link Column#getAtom()},
 * and an array of boolean values ({@code boolean[] values}).
 */
public class Column {

    private final ColumnType setting;
    private final Formula formula;
    private Atom atom;
    private final boolean[] values;
    private final Column subColumnLeft;
    private final Column subColumnRight;

    Column(ColumnType setting, Formula formula, boolean[] values) {
        this(setting, formula, values, null, null);
    }

    Column(ColumnType setting, Atom atom, boolean[] values) {
        this(setting, null, values, null, null);
        this.atom = atom;
    }

    Column(ColumnType setting, Formula formula, boolean[] values,
           Column subColumnLeft, Column subColumnRight) {
        this.setting = setting;
        this.formula = formula;
        this.atom = null;
        this.values = values;
        this.subColumnLeft = subColumnLeft;
        this.subColumnRight = subColumnRight;
    }

    /**
     * Get the table's {@link ColumnType} which this column is a type of
     * This will only return these types of setting:
     * 1. {@link ColumnType#ATOMS}
     * 2. {@link ColumnType#FORMULAS}
     * 2. {@link ColumnType#ROOT}
     *
     * @return The column's detail setting
     */
    public ColumnType getSetting() {
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
    public Formula getFormula() {
        return formula;
    }

    /**
     * Get the {@link Atom} object of this column, or null if the column denotes a formula
     *
     * @return The atom
     */
    public Atom getAtom() {
        return atom;
    }

    /**
     * Get the {@link TruthApt} object of this column, which is never null
     *
     * @return The truth-apt object
     */
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

    /**
     * Check if this column has any sub-columns
     * @see TruthCalculator#showSubColumns(int) for more information on sub-columns
     *
     * @return True if this column contain sub-columns
     */
    public boolean hasSubColumn() {
        return hasSubColumnLeft() || hasSubColumnRight();
    }

    /**
     * Check if this column has any sub-columns on its left
     * A formula with an unary main connective has no left sub-column
     * @see TruthCalculator#showSubColumns(int) for more information on sub-columns
     *
     * @return True if this column contain sub-columns on its left
     */
    public boolean hasSubColumnLeft() {
        return subColumnLeft != null;
    }

    /**
     * Check if this column has any sub-columns on its right
     * @see TruthCalculator#showSubColumns(int) for more information on sub-columns
     *
     * @return True if this column contain sub-columns on its right
     */
    public boolean hasSubColumnRight() {
        return subColumnRight != null;
    }

    /**
     * Given that this column denotes a formula, and the {@link TruthCalculator}'s setting
     * allowed for the generation of this column's sub-columns,
     * get the column to the left of this formula's BINARY connective.
     *
     * This returns {@code null} if the detail setting is NOT set to show formulas and sub columns,
     * or if this formula has an UNARY main connective,
     * or if this column does not denote a formula.
     *
     * @return Nullable column on the left of a formula
     */
    public Column getSubColumnLeft() {
        return subColumnLeft;
    }

    /**
     * Given that this column denotes a formula, and the {@link TruthCalculator}'s setting
     * allowed for the generation of this column's sub-columns,
     * get the column to the right of this formula's (binary or unary) connective.
     *
     * This returns {@code null} the detail setting is NOT set to show formulas and sub columns,
     * or if this column does not denote a formula.
     *
     * @return Nullable column on the right of a formula
     */
    public Column getSubColumnRight() {
        return subColumnRight;
    }

    @Override
    public String toString() {
        return "Column{" +
                "setting=" + setting +
                ", formula/atom=" + (formula == null ? atom : formula) +
                ", subColumnLeft=" + (subColumnLeft != null ? "PRESENT" : "NULL") +
                ", subColumnRight=" + (subColumnRight != null ? "PRESENT" : "NULL") +
                '}';
    }
}