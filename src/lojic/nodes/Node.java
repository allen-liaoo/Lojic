package lojic.nodes;

import lojic.nodes.truthapts.Formula;
import lojic.nodes.truthapts.LocalAtom;
import lojic.nodes.truthapts.TruthApt;
import lojic.tree.NodeTree;

/**
 * @author AlienIdeology
 *
 * Localized elements of a logical expression
 * Localization - Objects that are localized contian informations about its whereabouts in the formula tree
 * A node is either a {@link Formula} or a {@link LocalAtom}
 */
public abstract class Node {

    protected int level;
    protected String string;
    protected final Node parent;

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
     * Return the {@link TruthApt} object which this Node represents (in the case of a {@link Formula})
     * or contains (in the case of a {@link LocalAtom})
     *
     * @return The {@link TruthApt} object
     */
    public TruthApt getTruthApt() {
        return (this instanceof LocalAtom) ?
                ((LocalAtom) this).getAtom() : (Formula) this;
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
     * Copy the data of this node and return the result
     * This method is for the Lojic library's internal use only, users should ignore this
     * @see lojic.tree.NodeTree#copyOf(NodeTree) for copying nodes
     *
     * @return A copy of this node
     */
    public abstract Node copy();

    @Override
    public String toString() {
        return "Node{" +
                "level=" + level +
                ", string='" + string + "'" +
                ", type=" + ((this instanceof LocalAtom) ?
                "LOCAL_ATOM" : "FORMULA") +
                "}";
    }

}
