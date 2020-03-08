package lojic.nodes;

import lojic.nodes.connectives.Connective;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AlienIdeology
 *
 * A truth-apt set of nodes with a connective relation
 * This denotes a non-atomic proposition
 */
public class Formula extends Node {

    private final Connective connective;
    protected Node[] children;

    /**
     * Constructor of a formula
     * This constructor is for the Lojic library's internal use only, users should ignore this
     * @see lojic.parser.LojicParser for constructing nodes
     *
     * @param level The level of the tree which this formula is on
     * @param string The formula's string
     * @param parent The formula's parent
     */
    public Formula(int level, String string, Connective connective, Node parent) {
        super(level, string, parent);
        this.connective = connective;
    }

    /**
     * Get the children nodes of this formula
     *
     * @return The children nodes, or null if not set
     */
    public Node[] getChildren() {
        return children;
    }

    /**
     * Get the main connective of this formula
     *
     * @return The connective
     */
    public Connective getConnective() {
        return connective;
    }

    /**
     * Check if this formula is the root of a {@link Node} tree structure
     *
     * @return True if this formula is the root of a node tree
     */
    public boolean isRoot() {
        return super.getParent() == null;
    }

    Node copy(Node parentCopy) {
        Formula formula = new Formula(
                level,
                String.valueOf(string),
                connective,
                parentCopy
        );

        List<Node> childs = new ArrayList<>();
        for(Node child : children) {
            childs.add(child.copy(formula));
        }

        Node[] children = childs.toArray(new Node[0]);

        formula.children = children;
        return formula;
    }

}
