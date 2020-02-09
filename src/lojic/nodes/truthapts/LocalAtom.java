package lojic.nodes.truthapts;

import lojic.nodes.Node;

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

    private final Atom atom;

    public LocalAtom(int level, Node parent, Atom atom) {
        super(level, atom.toString(), parent);
        this.atom = atom;
    }

    public Atom getAtom() {
        return atom;
    }

}
