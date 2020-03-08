package lojic.nodes;

import lojic.nodes.connectives.Connective;
import lojic.table.TTableBuilder;
import lojic.table.TruthTable;

import java.util.List;

/**
 * @author AlienIdeology
 *
 * Localized elements of a logical expression
 * Localization - Objects that are localized contian informations about its whereabouts in the formula tree
 * A node is either a {@link Formula} or a {@link LocalAtom}
 */
public abstract class Node {

    private static final String FORMULA = "FORMULA";
    private static final String CONNECTIVE = "CONNECTIVE";
    private static final String ATOM = "ATOM";

    protected final int level;
    protected String string;
    protected final Node parent;

    protected NodeClimber climber;
    protected TTableBuilder tableBuilder;

    /**
     * Constructor of a node
     * This constructor is for the Lojic library's internal use only, users should ignore this
     * @see lojic.parser.LojicParser for constructing nodes
     *
     * @param level The level of the node tree which this node is on
     * @param string The node's string
     * @param parent The node's parent
     */
    protected Node (int level, String string, Node parent) {
        this.level = level;
        this.string = string;
        this.parent = parent;
        this.tableBuilder = new TTableBuilder(this);
    }

    /**
     * Compute and build a {@link TruthTable} that represents this node
     * with the existing settings of the {@link TTableBuilder}.
     *
     * If one has not changed the {@link TTableBuilder} of this Node, then
     * this builds a truth table with the default settings: Default True/False atoms and no sub-columns
     * @see TTableBuilder for more information on further confirguration
     *
     * @return The truth table
     */
    public TruthTable buildTruthTable() {
        return tableBuilder.build();
    }

    /**
     * Compute and return a {@link TTableBuilder} that represents this node
     * with no sub-columns
     * @see TTableBuilder for more information on further confirguration
     *
     * @return The truth table builder
     */
    public TTableBuilder getTableBuilder() {
        return tableBuilder;
    }

    /**
     * Set the table builder and its settings for every node under this node
     *
     * @param builder The table builder
     */
    public void setUniversalBuilder(TTableBuilder builder) {
        climb().forEach(n ->
                getTableBuilder().copySetting(builder)
        );
    }

    /**
     * Get the number of levels below this Node
     * The node itself is always at level 0
     * The value increases by 1 for every sub-atom or formula
     *
     * @return The integer value of levels
     */
    public int getLevels() {
        final int[] levels = {0};
        climb().forEach(node -> {
            int lvl = node.getLevel() - level;
            if (lvl > levels[0])
                levels[0] = lvl;
        });
        return levels[0];
    }

    /**
     * Get the level of the formula tree which the node is in
     * The Base formula is at level 1, and level increases as the nodes gets more specific
     *
     * @return int value of the node's level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Get the string representation of the node
     *
     * @return string
     */
    public String getString() {
        return string;
    }

    /**
     * Get the (unique, none-repeating) atoms in the Node
     *
     * @return The atoms
     */
    public Atom[] getAtoms() {
        return climb().filter(n -> !n.isFormula())
                .toList().stream()
                .map(n -> ((LocalAtom) n).getAtom())
                .toArray(Atom[]::new);
    }

    /**
     * Get all the Nodes on a level
     *
     * @param level The level
     * @return The nodes on the specified level
     */
    public List<Node> getNodes(int level) {
        return climb().filter(n -> n.getLevel() == level).toList();
    }

    /**
     * Get the {@link NodeClimber}, which is used to filter or iterate the node
     *
     * @return The NodeClimber
     */
    public NodeClimber climb() {
        if (climber == null) climber = new NodeClimber(this);
        // initialize climber later than object instantiation
        // to avoid null pointers
        return climber;
    }

    /**
     * Check if this node is a {@link Formula} or a {@link LocalAtom}
     *
     * @return true if this node is a formula, or false if it is a local atom
     */
    public boolean isFormula() {
        return this instanceof Formula;
    }

    /**
     * Get the parent node
     *
     * @return parent node
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Check if this node is always {@code true}
     * Note that this method generates a {@link TruthTable} with whatever
     * {@link TTableBuilder} settings that already exists in this Node.
     * To configure
     *
     * @return True if this node is always true
     */
    public boolean isTautology() {
        return getTableBuilder().build().rootIsTautology();
    }

    /**
     * Check if this node is always {@code false}
     * Note that this method generates a {@link TruthTable} with whatever
     * {@link TTableBuilder} settings that already exists in this Node.
     *
     * @return True if this node is always false
     */
    public boolean isContradiction() {
        return getTableBuilder().build().rootIsContradiction();
    }

    /**
     * Check if this node's string equals another node's
     * Theoretically, if {@link #structureEquals(Node)} returns true for a node, then this method should also returns true
     * But there is no guarantee that the ideal result would be the case, especially for nodes that are instances of {@link Formula}s
     * So, it is recommended to always use {@link #structureEquals(Node)} instead
     *
     * @param node The other node
     * @return True if this node's string equals another node's
     */
    public boolean stringEquals(Node node) {
        return string.equals(node.getString());
    }

    /**
     * Check if this node's structure is the same as another node's
     * Note that this method does not check for symmetric property of some binary connectives
     * For example, a formula of {@code P&Q} is not the same as a formula of {@code Q&P}.
     *
     * @param node The other node
     * @return True if this node's structure is the same as another node's
     */
    // TODO: Check for symmetric property
    public boolean structureEquals(Node node) {
        if (this.getClass() != node.getClass()) return false;
        if (isFormula()) {
            Connective con1 = ((Formula) this).getConnective();
            Connective con2 = ((Formula) node).getConnective();
            if (!con1.getOfficialSymbol().equals(con2.getOfficialSymbol())) return false;

            Node[] nodes1 = ((Formula) this).getChildren();
            Node[] nodes2 = ((Formula) node).getChildren();
            if (nodes1.length != nodes2.length) return false;

            for (int i = 0; i < nodes1.length; i++) {
                if(!nodes1[i].structureEquals(nodes2[i]))
                    return false;

                // Check for symmetric property by semantic equivalence
            }
            return true;
        } else {
            return stringEquals(node);
        }
    }

    /**
     * Copy the structure and data of this node and return the result
     *
     * @return A copy of this node
     */
    public abstract Node copy();

    /**
     * Get the string representation of the tree
     * The string is in the following format:
     * {@code [Indent][Node_Type]([level]): [Node_String]}
     * When a node is the root node, its string in this special format:
     * {@code ROOT_[Node_Type](0): [Node_String]}
     * When a node is a formula node, there is a connective string appended to the original format:
     * {@code (CONNECTIVE: [Official_Symbol])}
     *
     * @return The string representation of the tree
     */
    public String print() {
        StringBuilder builder = new StringBuilder();
        printNode(builder, this, 1);
        while (builder.toString().endsWith("\n")) {
            builder.delete(builder.length()-1, builder.length());
        }
        return builder.toString();
    }

    private static void printNode(StringBuilder builder, Node node, int tabs) {
        if (node instanceof LocalAtom) {
            builder.append(ATOM)
                    .append('(')
                    .append(node.getLevel())
                    .append("): ")
                    .append(node.getString())
                    .append('\n');

        } else if (node instanceof Formula) {
            Formula formula = (Formula) node;
            builder.append(FORMULA)
                    .append('(')
                    .append(formula.getLevel())
                    .append("): ")
                    .append(formula.getString());
            if (formula.getConnective() != null) {
                builder.append(" (")
                        .append(CONNECTIVE)
                        .append(':').append(' ')
                        .append(formula.getConnective().getOfficialSymbol())
                        .append(')');
            }
            builder.append('\n');

            Node[] children = formula.getChildren();
            for (Node n : children) {
                for (int t = 0; t < tabs; t++) {
                    builder.append("  ");
                }

                if (n instanceof Formula) {
                    printNode(builder, n, tabs + 1);
                } else if (n instanceof LocalAtom) {
                    builder.append(ATOM)
                            .append('(')
                            .append(n.getLevel())
                            .append("): ")
                            .append(n.getString())
                            .append('\n');
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Node{" +
                "level=" + level +
                ", string='" + string + "'" +
                ", type=" + ((this instanceof LocalAtom) ?
                "LOCAL_" + ATOM : FORMULA) +
                "}";
    }

}
