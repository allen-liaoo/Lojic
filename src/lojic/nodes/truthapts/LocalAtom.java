package lojic.nodes.truthapts;

import lojic.nodes.Node;
import lojic.parser.token.Token;

/**
 * @author AlienIdeology
 *
 * Localized Node that contains an Atom object, which is unlocalized.
 * If an atomic proposition is repeated in a logical expression, there will be multiple LocalAtom that represent the
 * atomic proposition in various places.
 * @see Node for localization
 * @see Atom for an unlocalized version
 */
public class LocalAtom extends Node {

    private Atom atom;

    public LocalAtom(int lvl, String str, Node prt, Atom at) {
        super(Token.Type.ATOM, lvl, str, prt);
        atom = new Atom(str);
    }

    public Atom getAtom() {
        return atom;
    }

}
