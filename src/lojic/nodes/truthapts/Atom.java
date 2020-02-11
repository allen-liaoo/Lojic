package lojic.nodes.truthapts;

import lojic.tree.NodeTree;

import java.util.Arrays;

/**
 * @author AlienIdeology
 *
 * An atomic proposition
 * This is a truth-apt variable with an alphabetic string.
 * If an atomic proposition is repeated in a logical expression, there will be only one Atom that represent the atomic
 * proposition.
 * @see LocalAtom for a localized version
 */
public class Atom implements TruthApt {
    private final String string;
    private boolean[] truths = null;

    /**
     * Constructor of an atom
     * This constructor is for the Lojic library's internal use only, users should ignore this
     * @see LocalAtom for more information
     *
     * @param string The atom's string
     */
    public Atom(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

    public boolean[] getTruths() {
        return truths;
    }

    public boolean isSet() {
        return truths != null;
    }

    public void setTruths(boolean[] truths) {
        this.truths = truths;
    }

    /**
     * Copy the data of this atom and return the result
     * This method is for the Lojic library's internal use only, users should ignore this
     * @see lojic.tree.NodeTree#copyOf(NodeTree) for copying nodetrees
     *
     * @return A copy of this atom
     */
    public Atom copy() {
        Atom atom = new Atom(String.valueOf(string));
        atom.setTruths(Arrays.copyOf(truths, truths.length));
        return atom;
    }

    @Override
    public boolean equals(Object obj) {
        return string.equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }

}
