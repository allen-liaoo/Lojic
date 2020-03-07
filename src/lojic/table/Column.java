package lojic.table;

import lojic.nodes.truthapts.Atom;
import lojic.nodes.truthapts.Formula;
import lojic.nodes.truthapts.TruthApt;

/**
 * @author AlienIdeology
 *
 * A column of the {@link TruthTable},
 * a {@link Formula} and/or {@link Atom} (See {@link Column#getFormula()} and {@link Column#getAtom()},
 * and an array of boolean values ({@code boolean[] values}).
 */
public class Column {

    private final Formula formula;
    private Atom atom;
    private final boolean[] values;
    private Column subColumnLeft;
    private Column subColumnRight;

    Column(Formula formula, boolean[] values) {
        this(formula, values, null, null);
    }

    Column(Atom atom, boolean[] values) {
        this( null, values, null, null);
        this.atom = atom;
    }

    Column(Formula formula, boolean[] values,
           Column subColumnLeft, Column subColumnRight) {
        this.formula = formula;
        this.atom = null;
        this.values = values;
        this.subColumnLeft = subColumnLeft;
        this.subColumnRight = subColumnRight;
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
     * Check if this column represents an atom
     * A column can either represent an atom or the root formula
     *
     * @return true if this column represents an atom, false if it represents a root formula
     */
    public boolean isAtom() {
        return atom != null;
    }

    /**
     * Check if this column has all {@code true} values
     *
     * @return True if this column is always true
     */
    public boolean isTautology() {
        return getTruthApt().isTautology();
    }

    /**
     * Check if this column object has all {@code false} values
     *
     * @return True if this column is always false
     */
    public boolean isContradiction() {
        return getTruthApt().isContradiction();
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
     * @see TTableBuilder#setSubColumnsLevel(int) for more information on sub-columns
     *
     * @return True if this column contain sub-columns
     */
    public boolean hasSubColumn() {
        return hasSubColumnLeft() || hasSubColumnRight();
    }

    /**
     * Check if this column has any sub-columns on its left
     * A formula with an unary main connective has no left sub-column
     * @see TTableBuilder#setSubColumnsLevel(int) for more information on sub-columns
     *
     * @return True if this column contain sub-columns on its left
     */
    public boolean hasSubColumnLeft() {
        return subColumnLeft != null;
    }

    /**
     * Check if this column has any sub-columns on its right
     * @see TTableBuilder#setSubColumnsLevel(int) for more information on sub-columns
     *
     * @return True if this column contain sub-columns on its right
     */
    public boolean hasSubColumnRight() {
        return subColumnRight != null;
    }

    /**
     * Given that this column denotes a formula, and the {@link TTableBuilder}'s setting
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
     * Given that this column denotes a formula, and the {@link TTableBuilder}'s setting
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

    void setSubColumnLeft(Column subColumnLeft) {
        this.subColumnLeft = subColumnLeft;
    }

    void setSubColumnRight(Column subColumnRight) {
        this.subColumnRight = subColumnRight;
    }

    @Override
    public String toString() {
        return "Column{" +
                "formula/atom=" + (formula == null ? atom : formula) +
                ", subColumnLeft=" + (subColumnLeft != null ? "PRESENT" : "NULL") +
                ", subColumnRight=" + (subColumnRight != null ? "PRESENT" : "NULL") +
                '}';
    }

}