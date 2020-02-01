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

    public Formula(int lvl, String str, Node prt) {
        super(Token.Type.FORMULA, lvl, str, prt);
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

    public void setTruths(boolean[] ths) {
        truths = ths;
    }

    public boolean[] getTruths() {
        return truths;
    }

    public boolean isSet() {
        return truths != null;
    }

}
