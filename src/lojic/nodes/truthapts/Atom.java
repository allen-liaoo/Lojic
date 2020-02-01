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
    private String string;
    private boolean[] truths = null;

    public Atom(String str) {
        string = str;
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

    public String getString() {
        return string;
    }

}
