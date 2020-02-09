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

    @Override
    public boolean equals(Object obj) {
        return string.equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }

}
