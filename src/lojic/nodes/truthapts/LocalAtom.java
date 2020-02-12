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

    /**
     * Constructor of a local atom
     * This constructor is for the Lojic library's internal use only, users should ignore this
     * @see lojic.parser.LojicParser for constructing nodes
     *
     * @param level The level of the tree which this formula is on
     * @param parent The formula's parent
     * @param atom The atom this local atom points to
     */
    public LocalAtom(int level, Node parent, Atom atom) {
        super(level, atom.toString(), parent);
        this.atom = atom;
    }

    /**
     * Get the {@link Atom} that this LocalAtom points to
     *
     * @return The atom
     */
    public Atom getAtom() {
        return atom;
    }

    @Override
    public Node copy() {
        return new LocalAtom(level,  parent.copy(),  this.atom.copy());
    }

}
