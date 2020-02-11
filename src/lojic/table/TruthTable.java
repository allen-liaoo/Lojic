package lojic.table;

import lojic.nodes.connectives.Connective;
import lojic.nodes.truthapts.Formula;
import lojic.tree.NodeTree;

import java.util.List;

/**
 * @author AlienIdeology
 *
 * A truth table that shows the truth values of each atoms and formulas, and the logical relation between each them.
 * This consists of {@link Column}s of {@link lojic.nodes.truthapts.TruthApt} and boolean arrays of truth values
 * which corresponds to each TruthApt object
 */
public class TruthTable {

    private final TruthCalculator calculator;
    private final NodeTree nodeTree;
    private final List<Column> columns;

    private final int rowSize;
    private final int columnSize;

    private boolean isTautology = true;
    private boolean isContradiction = true;

    TruthTable(TruthCalculator calculator, List<Column> columns) {
        this.calculator = calculator;
        this.nodeTree = calculator.getNodeTree();
        this.columns = columns;

        // define row & column size
        if (columns.isEmpty()) {
            this.rowSize = 0;
            this.columnSize = 0;
        } else {
            this.rowSize = columns.get(0).getValues().length;
            this.columnSize = columns.size();
        }

        if (!columns.isEmpty()) {
            Column root = columns.get(columns.size()-1);
            for (boolean val : root.getValues()) {
                if (val)
                    isContradiction = false;
                else
                    isTautology = false;
            }
        }
    }

    /**
     * Get the {@link NodeTree} which this table is generated upon
     *
     * @return The node tree
     */
    public NodeTree getNodeTree() {
        return nodeTree;
    }

    /**
     * Get the list of {@link Column}s of the truth table
     *
     * @return The list of columns
     */
    public List<Column> getFullTable() {
        return columns;
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
        boolean[] row = new boolean[columnSize];
        for (int i = 0; i < columnSize; i++) {
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
        return rowSize;
    }

    /**
     * Get the {@link Integer} number of columns (y) of this table
     *
     * @return The number of columns
     */
    public int getColumnSize() {
        return columnSize;
    }

    /**
     * Check if the conclusion (last column) is a tautology (it is always true).
     *
     * @return true only if the conclusion is always true
     */
    public boolean isTautology() {
        return isTautology;
    }

    /**
     * Check if the conclusion (last column) is a contradiction (it is always false).
     *
     * @return true only if the conclusion is always flase
     */
    public boolean isContradiction() {
        return isContradiction;
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
     * For a truth table with {@link DetailSetting#SUB_COLUMNS}, the table would look like this:
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
     * (With the truth values of atoms or formulas to the right and left of any formula shown)
     *
     * @return The string representation of the table
     */
    public String print() {
        StringBuilder builder = new StringBuilder();
        int[] widths = new int[columnSize];

        // Print first row
        for (int i = 0; i < columnSize; i++) {
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
        for (int j = 0; j < rowSize; j++) {
            for (int k = 0; k < columnSize; k++) {
                builder.append('|').append(' ');

                Column column = columns.get(k);
                formulaValString(builder, column, widths[k], j);

                builder.append(' ');
            }
            builder.append('|').append(horizLine);
        }

        return builder.toString();
    }

    private void formulaValString(StringBuilder builder, Column column, int width, int index) {
        int mid; // index in which t/f value is inserted, starts from 1

        if(column.getAtom() != null) { // atom column

            mid = column.getName().length() / 2;
            if (!(mid % 2 == 0)) mid -= 1;
            if (mid <= 0) mid = 1;

        } else { // formula column

            Formula formula = column.getFormula();
            mid = conIndex(column, formula.getConnective());
            System.out.println("Con: " + mid);
        }

        if (column.hasSubColumnLeft()) {
            formulaValString(builder, column.getSubColumnLeft(), mid - 1, index);
        } else {
            addSpaces(builder, mid - 1);
        }

        builder.append(
                (column.getValues()[index]) ? 'T' : 'F'
        );

        int end = (width >= 2) ? width - mid : width - mid - 1;
        if (column.hasSubColumnRight()) {
            formulaValString(builder, column.getSubColumnRight(), end, index);
        } else {
            addSpaces(builder, end);
        }

        //System.out.println("width: " + width + " middle: " + middle);

    }

    // get the index of the connective in a formula string
    private int conIndex(Column column, Connective connective) {

        int index = 0;
        String name = column.getName();

        if (connective.isBinary()) {
            Column left = column.getSubColumnLeft();
            String lname = left.getName();

            int removed = 0;
            while (!name.startsWith(lname)) {
                removed++;
                name = name.substring(1);
            }

            index += removed;
            index += lname.length();
            index += 1;

        } else {

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
    private void addSpaces(StringBuilder builder, int times) {
        for (int i = 0; i < times; i++) {
            builder.append(' ');
        }
    }

    // w: 1 m: 0
    // w: 2 m: 0
    // w: 3 m: 1
    // w: 4 m: 2
    // w: 5 m: 2
    // w: 6 m: 3
    // w: 7 m: 3
    // w: 8 m: 4
        /*
        +---+-----+---------+
        | p | qr  | (p→qr)  |
        +---+-----+---------+
        | T |  T  | T TT    |*/

}
