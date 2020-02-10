package lojic.table;

import lojic.tree.NodeTree;

import java.util.List;

/**
 * @author AlienIdeology
 *
 * A truth table
 * This consists of {@link Column}s of {@link lojic.nodes.truthapts.TruthApt} and boolean arrays of truth values
 * which corresponds to each TruthApt object
 */
public class TruthTable {

    private final NodeTree nodeTree;
    private final List<Column> columns;

    TruthTable(NodeTree nodeTree, List<Column> columns) {
        this.nodeTree = nodeTree;
        this.columns = columns;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();

        for (Column column : columns) {

        }

        return builder.toString();
    }

}
