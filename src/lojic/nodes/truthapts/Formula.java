package lojic.nodes.truthapts;

import lojic.nodes.Node;
import lojic.nodes.connectives.Connective;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AlienIdeology
 *
 * A truth-apt set of nodes with a connective relation
 * This denotes a non-atomic proposition
 */
public class Formula extends Node implements TruthApt {

    private final Connective connective;
    protected Node[] children;
    private boolean[] truths = null;

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

    @Override
    public boolean[] getTruths() {
        return truths;
    }

    @Override
    public boolean isSet() {
        return truths != null;
    }

    @Override
    public void setTruths(boolean[] truths) {
        this.truths = truths;
    }

    @Override
    public Node copy() {
        List<Node> childs = new ArrayList<>();
        for(Node child : children) {
            childs.add(child.copy());
        }

        Node[] children = childs.toArray(new Node[0]);

        Formula formula = new Formula(level, String.valueOf(string), connective, parent.copy());
        formula.children = children;
        formula.climber = this.climber;
        formula.tableSetting = this.tableSetting;
        return formula;
    }

}
