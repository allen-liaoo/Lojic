package lojic.nodes;

import com.sun.istack.internal.Nullable;
import lojic.nodes.connectives.Connective;

/**
 * @author AlienIdeology
 *
 * Load all the default, recognized connectives.
 */
// FEATURE: No symbols stripping - Remove official symbols
public class ConnectiveFactory {

    public final static Connective NEG = new Connective((right) -> !right, 
            "¬", 50, "~", "!");

    public final static Connective AND = new Connective((left, right) -> left && right,
            "∧", 40, "&", "^", "×", "•", "⋅");

    public final static Connective NAND = new Connective((left, right) -> !left || !right,
            "↑", 40, "⊼");

    public final static Connective OR = new Connective((left, right) -> left || right,
            "∨", 30, "+", "∥");

    public final static Connective NOR = new Connective((left, right) -> !left && !right,
            "↓", 30, "⊽");

    public final static Connective XOR = new Connective((left, right) -> (left && !right) || (!left && right),
            "⊕", 30, "⊻", "↮", "≢");

    public final static Connective IF = new Connective((left, right) -> !left || right,
            "→", 20, "->", "⇒", "⊃");

    public final static Connective NIF = new Connective((left, right) -> left && !right,
            "↛", 20, "/->", "-/>" );

    public final static Connective IF_CON = new Connective((left, right) -> left || !right,
            "←", 20, "<-", "⇐", "⊂");

    public final static Connective NIF_CON = new Connective((left, right) -> !left && right,
            "↚", 20, "</-", "<-/");

    public final static Connective IFF = new Connective((left, right) -> (left || !right) && (!left || right),
            "↔", 10, "<->", "≡", "⇔", "=");

    public static final Connective[] CONNECTIVES = new Connective[] {
            // Order of this list is important for replacing unofficial symbols
            NEG,
            AND, NAND,
            OR, NOR, XOR,
            IFF,  // IFF before IF group because "<->" should be replaced first, then "<-" or "->"
            NIF, NIF_CON,
            IF, IF_CON,
    };

    @Nullable
    // FEATURE: No symbols stripping - Change if condition
    public static Connective getConnective(String connective) {
        for (Connective con : ConnectiveFactory.CONNECTIVES) {
            if (con.getOfficialSymbol().equals(connective)) return con;
        }
        return null;
    }

}
