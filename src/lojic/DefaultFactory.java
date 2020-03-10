package lojic;

import lojic.argument.InferenceException;
import lojic.argument.Line;
import lojic.argument.inference_rule.AssumptionIR;
import lojic.argument.inference_rule.DischargeIR;
import lojic.argument.inference_rule.InferenceRule;
import lojic.argument.inference_rule.PremiseIR;
import lojic.nodes.connectives.BinaryConnective;
import lojic.nodes.connectives.Connective;
import lojic.nodes.connectives.UnaryConnective;
import lojic.table.TTableBuilder;

/**
 * @author AlienIdeology
 *
 * Contains static fields of:
 * 1. Default, recognized connectives, their precedence, and their associativity (right associative by default)
 * 2. Default true/false atoms (See {@link TTableBuilder#useDefaultTFAtoms()}
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

    public final static UnaryConnective NEG = new UnaryConnective((right) -> !right, 
            "¬", 50, "~", "!");

    public final static BinaryConnective AND = new BinaryConnective((left, right) -> left && right,
            "∧", 40, "/\\", "&", "^", "×", "•", "⋅");

    public final static BinaryConnective NAND = new BinaryConnective((left, right) -> !left || !right,
            "↑", 40, "⊼");

    public final static Connective OR = new BinaryConnective((left, right) -> left || right,
            "∨", 30, "\\/", "|", "+", "∥");

    public final static BinaryConnective NOR = new BinaryConnective((left, right) -> !left && !right,
            "↓", 30, "⊽");

    public final static BinaryConnective XOR = new BinaryConnective((left, right) -> (left && !right) || (!left && right),
            "⊕", 30, "⊻", "<-/->", "<=/=>", "↮", "≢");

    public final static BinaryConnective IF = new BinaryConnective((left, right) -> !left || right,
            "→", 20, "->", "=>", "⇒", "⊃", ">");

    public final static BinaryConnective NIF = new BinaryConnective((left, right) -> left && !right,
            "↛", 20, "-/>", "=/>");

    public final static BinaryConnective IF_CON = new BinaryConnective((left, right) -> left || !right,
            "←", 20, "<-", "<=", "⇐", "⊂", "<");

    public final static BinaryConnective N_IF_CON = new BinaryConnective((left, right) -> !left && right,
            "↚", 20, "</-", "</=", "<-/");

    public final static BinaryConnective IFF = new BinaryConnective((left, right) -> (left || !right) && (!left || right),
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

    /* TruthTable */

    public static final String[] TRUE_ATOMS = new String[]{"T", "⊤", "1"};

    public static final String[] FALSE_ATOMS = new String[]{"F", "⊥", "0"};

    /* Inference Rules */

    public static final PremiseIR IR_PREMISE = new PremiseIR();

    public static final AssumptionIR IR_ASSUMPTION = new AssumptionIR();

    // Normal IRs

    public static final InferenceRule IR_AND_INTRO = new InferenceRule(AND.getOfficialSymbol() + "I") {
        @Override
        protected void validity(Line line) throws InferenceException {
            // TODO: AND Introduction
        }
    };

    public static final InferenceRule IR_MODUS_POLLENS = new InferenceRule("MT") {
        @Override
        protected void validity(Line line) throws InferenceException {
            // TODO: MP
        }
    };

    // Discharge IRs
    public static final DischargeIR IR_IF_INTRO = new DischargeIR(IF.getOfficialSymbol() + "I", 1) {
        @Override
        protected void validity(Line line) throws InferenceException {
            // TODO: IF Introduction
        }
    };

    public static final DischargeIR IR_OR_ELIM = new DischargeIR(OR.getOfficialSymbol() + "E", 2) {
        @Override
        protected void validity(Line line) throws InferenceException {
            // TODO: OR Elimination
        }
    };

}
