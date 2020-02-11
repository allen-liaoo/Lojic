package lojic;

import lojic.nodes.connectives.Connective;
import lojic.table.TruthCalculator;

/**
 * @author AlienIdeology
 *
 * Contains static fields of:
 * 1. Default, recognized connectives, their precedence, and their associativity (right associative by default)
 * 2. Default true/false atoms (See {@link TruthCalculator#TFAtomsDefault()}
 */
// FEATURE: No symbols stripping - Remove official symbols
public class DefaultFactory {

    public final static int PRECEDENCE_DEFAULT = 60;

    public final static int PRECEDENCE_HIGHEST = 70;

    public final static Connective NEG = new Connective((right) -> !right, 
            "¬", 50, "~", "!");

    public final static Connective AND = new Connective((left, right) -> left && right,
            "∧", 40, "/\\", "&", "^", "×", "•", "⋅");

    public final static Connective NAND = new Connective((left, right) -> !left || !right,
            "↑", 40, "⊼");

    public final static Connective OR = new Connective((left, right) -> left || right,
            "∨", 30, "\\/", "|", "+", "∥");

    public final static Connective NOR = new Connective((left, right) -> !left && !right,
            "↓", 30, "⊽");

    public final static Connective XOR = new Connective((left, right) -> (left && !right) || (!left && right),
            "⊕", 30, "⊻", "<-/->", "<=/=>", "↮", "≢");

    public final static Connective IF = new Connective((left, right) -> !left || right,
            "→", 20, "->", "=>", "⇒", "⊃", ">");

    public final static Connective NIF = new Connective((left, right) -> left && !right,
            "↛", 20, "-/>", "=/>");

    public final static Connective IF_CON = new Connective((left, right) -> left || !right,
            "←", 20, "<-", "<=", "⇐", "⊂", "<");

    public final static Connective N_IF_CON = new Connective((left, right) -> !left && right,
            "↚", 20, "</-", "</=", "<-/");

    public final static Connective IFF = new Connective((left, right) -> (left || !right) && (!left || right),
            "↔", 10, "<>", "<->", "<=>", "≡", "⇔", "=");

    public static final Connective[] DEFAULT_CONNECTIVES = new Connective[] {
            // Order of this list is important for replacing unofficial symbols
            NEG,
            AND, NAND,

            OR, NOR, XOR,

            IFF,  // "<->" replaced before "<-" or "->"
            NIF,
            N_IF_CON, // "<-/" replaced before "<-"
            IF_CON, // "<" replaced second to last
            IF // ">" replaced last
    };

    public static final String[] TRUE_ATOMS = new String[]{"T", "⊤", "1"};

    public static final String[] FALSE_ATOMS = new String[]{"F", "⊥", "0"};

}
