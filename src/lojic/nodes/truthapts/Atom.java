package lojic.nodes.truthapts;

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

    // TODO: getString()
    @Override
    public String toString() {
        return string;
    }

    @Override
    public boolean[] getTruths() {
        return truths;
    }

    @Override
    public boolean isSet() {
        return truths != null;
    }

    @Override
    public void setTruths(boolean[] truths) {
        this.truths = truths;
    }

    /**
     * Copy the data of this atom and return the result
     *
     * @return A copy of this atom
     */
    public Atom copy() {
        return new Atom(String.valueOf(string));
    }

}
