package lojic.nodes.truthapts;

import lojic.nodes.Node;
import lojic.nodes.connectives.Connective;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author AlienIdeology
 *
 * A truth-apt set of nodes with a connective relation
 * This denotes a non-atomic proposition
 */
public class Formula extends Node implements TruthApt {

    private Connective connective;
    private Node[] children;
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
    public Formula(int level, String string, Node parent) {
        super(level, string, parent);
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
     * Check if this formula is the root of a {@link lojic.tree.NodeTree}
     *
     * @return True if this formula is the root of a node tree
     */
    public boolean isRoot() {
        return super.getParent() == null;
    }

    /**
     * Sets the children nodes of this formula
     *
     * @param nodes The children nodes, could be a LocalAtom or another Formula
     */
    public void setChildren(Node[] nodes) {
        children = nodes;
    }

    /**
     * Set the main connective of this formula
     * This method is for the Lojic library's internal use only, users should ignore this
     *
     * @param con the connective
     */
    public void setConnective(Connective con) {
        connective = con;
    }

    /**
     * Set the formula's string
     * This method is for the Lojic library's internal use only, users should ignore this
     *
     * @param string the string
     */
    public void setString(String string) {
        super.string = string;
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
        Formula formula = new Formula(level, String.valueOf(string), parent.copy());
        List<Node> childs = new ArrayList<>();
        for(Node child : children) {
            childs.add(child.copy());
        }

        formula.setChildren(childs.toArray(new Node[0]));
        formula.setConnective(connective);
        formula.setTruths(Arrays.copyOf(truths, truths.length));

        return formula;
    }

}
