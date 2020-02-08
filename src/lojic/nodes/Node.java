package lojic.nodes;

import lojic.parser.token.TokenType;

/**
 * @author AlienIdeology
 *
 * Localized elements of a logical expression
 * Localization - Objects that are localized contian informations about its whereabouts in the formula tree
 */
public class Node {

    protected TokenType type;
    protected int level;
    protected String string;
    protected final Node parent;

    public Node (TokenType type, int level, String string, Node parent) {
        this.type = type;
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
    public TokenType getType() {
        return type;
    }

}
