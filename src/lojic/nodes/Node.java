package lojic.nodes;

import lojic.parser.token.Token;

/**
 * @author AlienIdeology
 *
 * Localized elements of a logical expression
 * Localization - Objects that are localized contian informations about its whereabouts in the logical expression
 */
public class Node {

    protected Token.Type type;
    protected int level;
    protected String string;
    protected Node parent;

    public Node (Token.Type tp, int lvl, String str, Node prt) {
        type = tp;
        level = lvl;
        string = str;
        parent = prt;
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
     * These are raw, unprocessed strings of the original logical experession
     *
     * @return string
     */
    public String getString() {
        return string;
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
     * Get the type enum of this node
     *
     * @return Node type
     */
    public Token.Type getType() {
        return type;
    }

}
