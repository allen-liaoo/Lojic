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

    /* Connectives */

    /**
     * The precedence value by default before anything is read by {@link lojic.parser.LojicParser}
     * This field is for the Lojic library's internal use only, users should ignore this
     */
    public final static int PRECEDENCE_DEFAULT = 60;

    /**
     * The highest precedence value (for parenthesized units and atoms) used by {@link lojic.parser.LojicParser}
     * This field is for the Lojic library's internal use only, users should ignore this
     */
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

    /**
     * Array of all default connectives
     */
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

    /* Parenthesis */

    public static final String[] OPEN_PAREN = new String[]{"(", "(", "{", "["};

    public static final String[] CLOSE_PAREN = new String[]{")", ")", "}", "]"};

    /* True/False Atoms */

    public static final String[] TRUE_ATOMS = new String[]{"T", "⊤", "1"};

    public static final String[] FALSE_ATOMS = new String[]{"F", "⊥", "0"};

    /* Utility Methods for Default Fields */

    public static boolean isDefaultConnective(String token) {
        for (Connective con : DEFAULT_CONNECTIVES) {
            if (token.equals(con.getOfficialSymbol())) return true;
        }
        return false;
    }

    public static boolean isDefaultBinaryConnective(String string) {
        for (Connective con : DEFAULT_CONNECTIVES) {
            if (con.isBinary()) {
                if (string.equals(con.getOfficialSymbol())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isDefaultUnaryConnective(String string) {
        for (Connective con : DEFAULT_CONNECTIVES) {
            if (con.isUnary()) {
                if (string.equals(con.getOfficialSymbol())) return true;
            }
        }
        return false;
    }

    public static Connective getDefaultConnective(String connective) {
        for (Connective con : DEFAULT_CONNECTIVES) {
            if (con.getOfficialSymbol().equals(connective)) return con;
        }
        return null;
    }
}
