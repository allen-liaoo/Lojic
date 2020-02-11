package lojic.table;

/**
 * @author AlienIdeology
 *
 * The detail setting used to customize {@link TruthTable}'s columns output and what values {@link TruthCalculator} compute
 */
public enum ColumnType {

    /**
     * Columns of atoms
     */
    ATOMS,

    /**
     * Columns of non-root formulas
     */
    FORMULAS,

    /**
     * Columns of atoms and/or non-root formulas which belongs within a column of a formula
     */
    SUB_COLUMNS,

    /**
     * A column of the root atom or formula, which is the original (full) logical expression
     */
    ROOT

}
