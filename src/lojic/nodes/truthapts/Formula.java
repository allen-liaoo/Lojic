package lojic.nodes.truthapts;

import lojic.nodes.Node;
import lojic.nodes.connectives.BinaryConnective;
import lojic.nodes.connectives.Connective;
import lojic.nodes.connectives.UnaryConnective;
import lojic.parser.token.Token;

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

    public Formula(int level, String string, Node parent) {
        super(Token.Type.FORMULA, level, string, parent);
    }

    /**
     * Get the children nodes of this formula
     *
     * @return The children nodes, or null if not set
     */
    public Node[] getChildren() {
        return children;
    }

    public Connective getConnective() {
        return connective;
    }

    public BinaryConnective getBinConnective() {
        return connective instanceof BinaryConnective ? (BinaryConnective) connective : null;
    }

    public UnaryConnective getUConnective() {
        return connective instanceof UnaryConnective ? (UnaryConnective) connective : null;
    }

    public boolean isBinary() {
        return connective instanceof BinaryConnective;
    }

    public boolean isBase() {
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

    public void setConnective(Connective con) {
        connective = con;
    }

    public void setTruths(boolean[] truths) {
        this.truths = truths;
    }

    public boolean[] getTruths() {
        return truths;
    }

    public boolean isSet() {
        return truths != null;
    }

}
