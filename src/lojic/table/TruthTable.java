package lojic.table;

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

    TruthTable(TruthCalculator calculator, List<Column> columns) {
        this.calculator = calculator;
        this.nodeTree = calculator.getNodeTree();
        this.columns = columns;
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
     * Get the {@link Integer} number of rows of this table
     *
     * @return The number of rows
     */
    public int getRowSize() {
        return calculator.getRowSize();
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
     * @return The string representation of the table
     */
    public String print() {
        StringBuilder builder = new StringBuilder();
        int[] sizes = new int[columns.size()];

        // Print first row
        for (int i = 0; i < columns.size(); i++) {
            builder.append('|').append(' ');

            Column column = columns.get(i);
            String name = column.getName();
            int length = name.length();
            sizes[i] = (length % 2 == 0)  // name with even # of chars
                    ? length + 1 : length;

            builder.append(name)
                    .append((length % 2 == 0) ? ' ' : "")
                    // offset the string by 1 to the left
                    // so that each column has an odd # of hyphens '-'
                    .append(' ');
        }
        builder.append('|');

        String horizLine = '\n' + horizLine(sizes) + '\n';
        builder.insert(0, horizLine)
                .append(horizLine);

        // Print the rest of the truth tables (T/Fs)
        for (int j = 0; j < calculator.getRowSize(); j++) {
            for (int k = 0; k < columns.size(); k++) {
                builder.append('|').append(' ');

                Column column = columns.get(k);
                boolean value = column.getValues()[j];
                char ch = value ? 'T' : 'F';
                int size = sizes[k];

                if (column.hasSubColumn()) {
                    // TODO: show subcolumns
                } else {
                    int count = 0;
                    while(count < (size-1)) {
                        count++;
                        builder.append(' ');
                    }
                    builder.insert(builder.length() - (size / 2), ch)
                            .append(' ');
                }

            }
            builder.append('|').append(horizLine);
        }

        return builder.toString();
    }

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

}
