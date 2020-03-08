package lojic.table;

import lojic.nodes.Atom;
import lojic.nodes.Formula;
import lojic.nodes.Node;
import lojic.nodes.connectives.Connective;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author AlienIdeology
 *
 * A truth table that shows the truth values of each atoms and formulas, and the logical relation between each them.
 * This consists of {@link Column}s of {@link Atom} and {@link Formula}
 * and boolean arrays of truth values which corresponds to each TruthApt object
 *
 * @see TTableBuilder#build() for creating instances of truth tables
 */
public class TruthTable {

    private final Node node;
    private final List<Column> columns; // Internally stored columns
    private final int subColumnsLevel;

    /**
     * Constructor of TruthTable
     * This constructor is for the Lojic library's internal use only, users should ignore this
     * @see TTableBuilder#build() for creating instances of truth tables
     *
     * @param node The node which the table is generated from
     * @param subColumnsLevel The level of sub-columns that this table has
     */
    TruthTable(Node node, List<Column> columns, int subColumnsLevel) {
        this.node = node;
        this.columns = columns;
        this.subColumnsLevel = subColumnsLevel;
    }

    /**
     * Get the {@link Node} which this table is generated upon
     *
     * @return The node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Get the list of {@link Column}s of the truth table
     *
     * @return The list of columns
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * Get a list of columns of which represents the root column's atoms
     *
     * @return The list of non-null atom columns
     */
    public List<Column> getAtomColumns() {
        return columns.stream()
                .filter(Column::isAtom)
                .collect(Collectors.toList());
    }

    /**
     * Get a the column that represents the root formula,
     * which is always present (non-null)
     *
     * @return The non-null root column
     */
    public Column getRootColumn() {
        return columns.get(columns.size()-1);
    }

    /**
     * Get a column by index
     *
     * @param index The column (y) index
     * @return The column
     * @throws IndexOutOfBoundsException if the index is out of range
     *      {@code index < 0 || index >= size()})
     */
    public Column getColumn(int index) {
        return columns.get(index);
    }

    /**
     * Get a row of boolean values by index
     *
     * @param index The row (x) index
     * @return An array of boolean values
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *      ({@code index < 0 || index >= size()})
     */
    public boolean[] getRow(int index) {
        boolean[] row = new boolean[getColumnSize()];
        for (int i = 0; i < getColumnSize(); i++) {
            row[i] = columns.get(i).getValues()[index];
        }
        return row;
    }

    /**
     * Get a single boolean value by row (x) and column (y) indexes
     *
     * @param rowIndex The row (x) index
     * @param columnIndex The column (y) index
     * @return The boolean value
     * @throws IndexOutOfBoundsException if the column index is out of range
     *      {@code index < 0 || index >= size()})
     * @throws ArrayIndexOutOfBoundsException if the row index is out of range
     *      ({@code index < 0 || index >= size()})
     */
    public boolean getCell(int rowIndex, int columnIndex) {
        return columns.get(columnIndex).getValues()[rowIndex];
    }

    /**
     * Get the {@link Integer} number of rows (x) of this table
     *
     * @return The number of rows
     */
    public int getRowSize() {
        return columns.isEmpty() ?
                -1 : columns.get(0).getValues().length;
    }

    /**
     * Get the {@link Integer} number of columns (y) of this table
     *
     * @return The number of columns
     */
    public int getColumnSize() {
        return columns.size();
    }

    /**
     * Get the integer value if levels of sub-columns on this table
     * This returns {@code 0} if this table has no sub-columns
     * @see TTableBuilder#setSubColumnsLevel(int) for more information on sub-columns
     *
     * @return the integer level of sub-columns
     */
    public int getSubColumnsLevel() {
        return subColumnsLevel;
    }

    /**
     * Check if this truth table shows sub-columns
     *
     * @return true if this table show sub-columns
     */
    public boolean showsSubColumns() {
        return subColumnsLevel > 0 || subColumnsLevel == -1;
    }

    /**
     * Remove a column by index
     *
     * @param index The index
     * @return The column that is removed
     */
    public Column removeColumn(int index) {
        return columns.remove(index);
    }

    /**
     * Check if the conclusion (last column) is a tautology (it is always true).
     *
     * @return true only if the conclusion is always true
     * @throws NullPointerException if the truth table is empty
     */
    public boolean rootIsTautology() {
        if (columns.isEmpty()) throw new NullPointerException("Empty truth table!");

        Column root = columns.get(columns.size()-1);
        return root.isTautology();
    }

    /**
     * Check if the conclusion (last column) is a contradiction (it is always false).
     *
     * @return true only if the conclusion is always false
     * @throws NullPointerException if the truth table is empty
     */
    public boolean rootIsContradiction() {
        if (columns.isEmpty()) throw new NullPointerException("Empty truth table!");

        Column root = columns.get(columns.size()-1);
        return root.isContradiction();
    }

    /**
     * Get the string representation of the table
     * The string is in the following format:
     * <pre>[Horizontal Line]
     * [Names]
     * [Horizontal Line]
     * [Truth Values]
     * [Horizontal Line]
     * ...</pre>
     *
     * For example:
     * <pre>
     * +---+---+-------+
     * | P | Q | (P→Q) |
     * +---+---+-------+
     * | T | T |   T   |
     * +---+---+-------+
     * | T | F |   F   |
     * +---+---+-------+
     * | F | T |   T   |
     * +---+---+-------+
     * | F | F |   T   |
     * +---+---+-------+
     * </pre>
     *
     * For a truth table with with sub-columns, the table would look like this:
     * <pre>
     * +---+---+-------+
     * | P | Q | (P→Q) |
     * +---+---+-------+
     * | T | T |  TTT  |
     * +---+---+-------+
     * | T | F |  TFF  |
     * +---+---+-------+
     * | F | T |  FTT  |
     * +---+---+-------+
     * | F | F |  FTF  |
     * +---+---+-------+
     * </pre>
     * Sub-columns are columns of the truth values (of atoms or formulas) to the
     * right and left of the main formula
     * @see TTableBuilder#setSubColumnsLevel(int) for more infornation on truth columns
     *
     * @return The string representation of the table
     */
    public String print() {
        StringBuilder builder = new StringBuilder();
        int[] widths = new int[getColumnSize()];

        // Print first row
        for (int i = 0; i < getColumnSize(); i++) {
            builder.append('|').append(' ');

            Column column = columns.get(i);
            String name = column.getName();
            int length = name.length();
            widths[i] = (length % 2 == 0)  // name with even # of chars
                    ? length + 1 : length;

            builder.append(name)
                    .append((length % 2 == 0) ? ' ' : "")
                    // offset the string by 1 to the left
                    // so that each column has an odd # of hyphens '-'
                    .append(' ');
        }
        builder.append('|');

        String horizLine = '\n' + horizLine(widths) + '\n';
        builder.insert(0, horizLine)
                .append(horizLine);

        // Print the rest of the truth tables (T/Fs)
        for (int j = 0; j < getRowSize(); j++) {
            for (int k = 0; k < getColumnSize(); k++) {
                builder.append('|').append(' ');

                Column column = columns.get(k);
                if (column.getAtom() != null || !showsSubColumns()) {
                    int size = widths[k];
                    addSpaces(builder, size-1);

                    builder.insert(builder.length() - (size / 2), tfChar(column, j));
                } else {
                    printFormula(builder, column, j);

                    // account for previous offset of name string
                    if ((column.getName().length() % 2) == 0)
                        builder.append(' ');
                }

                builder.append(' ');
            }
            builder.append('|').append(horizLine);
        }

        return builder.toString();
    }

    /* Printing methods */

    private void printFormula(StringBuilder builder, Column column, int index) {
        if (column.getFormula() == null) {
            printAtom(builder, column, index);
            return;
        }

        // Add space under "("
        builder.append(' ');

        int conIndex = conIndex(column, column.getFormula().getConnective());
        conIndex--; // offset by the first parenthesis

        if (column.hasSubColumnLeft()) {
            Column left = column.getSubColumnLeft();
            printFormula(builder, left, index);
        } else {
            addSpaces(builder, conIndex - 1);
        }

        builder.append(tfChar(column, index));

        if (column.hasSubColumnRight()) {
            Column right = column.getSubColumnRight();
            printFormula(builder, right, index);
        } else {
            addSpaces(builder, column.getName().length() - conIndex - 2);
        }

        // Add space under ")"
        builder.append(' ');
    }

    private void printAtom(StringBuilder builder, Column column, int index) {
        String atomStr = column.getName();
        int spaces = atomStr.length() / 2;
        addSpaces(builder, spaces);

        builder.append(tfChar(column, index));

        addSpaces(builder, atomStr.length() - spaces - 1);
    }

    // get the index of the connective in a formula string
    private int conIndex(Column column, Connective connective) {

        int index = 0;
        String name = column.getName();

        if (connective.isBinary()) {
            Node left = column.getFormula().getChildren()[0];
            String lname = left.getString();

            int removed = 0;
            while (!name.startsWith(lname)) {
                removed++;
                name = name.substring(1);
            }

            index += removed;
            index += lname.length();
            index += 1;

        } else {

            // counts the amount of "(" before an unary connective
            int count = 0;
            String temp = name;
            while (temp.startsWith("(")) {
                temp = temp.substring(1);
                count++;
            }
            index += count + 1;
        }

        return index;
    }

    // return the string of a horizontal line
    private String horizLine(int[] sizes) {
        StringBuilder builder = new StringBuilder();

        for (int size : sizes) {
            builder.append('+').append('-');
            for (int j = 0; j < size; j++) {
                builder.append('-');
            }
            builder.append('-');
        }
        builder.append('+');

        return builder.toString();
    }

    // add # amount of space
    // Can be replaced by String#repeat (since java 11)
    private void addSpaces(StringBuilder builder, int times) {
        for (int i = 0; i < times; i++) {
            builder.append(' ');
        }
    }

    private char tfChar(Column column, int index) {
        return (column.getValues()[index]) ? 'T' : 'F';
    }

}
