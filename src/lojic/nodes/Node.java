package lojic.nodes;

import lojic.nodes.truthapts.Formula;
import lojic.nodes.truthapts.LocalAtom;
import lojic.nodes.truthapts.TruthApt;

/**
 * @author AlienIdeology
 *
 * Localized elements of a logical expression
 * Localization - Objects that are localized contian informations about its whereabouts in the formula tree
 * A node is either a {@link Formula} or a {@link LocalAtom}
 */
public class Node {

    protected int level;
    protected String string;
    protected final Node parent;

    public Node (int level, String string, Node parent) {
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
     * Check if this node is a {@link Formula}
     *
     * @return true if this node is a formula
     */
    public boolean isFormula() {
        return !(this instanceof LocalAtom);
    }

    /**
     * Get the parent node
     *
     * @return parent node
     */
    public Node getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "Node{" +
                "level=" + level +
                ", string='" + string + "'" +
                ", type=" + ((this instanceof LocalAtom) ?
                "LOCAL_ATOM" : "FORMULA") +
                "}";
    }

    /**
     * Get the type enum of this node
     *
     * @return Node type
     */
    /*public TokenType getType() {
        return type;
    }*/

}
