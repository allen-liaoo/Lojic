package lojic.nodes;

import com.sun.istack.internal.Nullable;
import lojic.nodes.connectives.BinaryConnective;
import lojic.nodes.connectives.Connective;
import lojic.nodes.connectives.UnaryConnective;

/**
 * @author AlienIdeology
 *
 * Load all the default, recognized connectives.
 */
public class ConnectiveFactory {

    public final static UnaryConnective NEG = new UnaryConnective("¬", 50, "~", "!") {
        @Override
        public boolean computeTruth(boolean right) {
            return !right;
        }
    };

    public final static BinaryConnective AND = new BinaryConnective("∧", 40, "&", "^", "×", "•", "⋅") {
        @Override
        public boolean computeTruth(boolean left, boolean right) {
            return left && right;
        }
    };

    public final static BinaryConnective NAND = new BinaryConnective("↑", 40, "⊼") {
        @Override
        public boolean computeTruth(boolean left, boolean right) {
            return !left || !right;
        }
    };

    public final static BinaryConnective OR = new BinaryConnective("∨", 30, "+", "∥") {
        @Override
        public boolean computeTruth(boolean left, boolean right) {
            return left || right;
        }
    };

    public final static BinaryConnective NOR = new BinaryConnective("↓", 30, "⊽") {
        @Override
        public boolean computeTruth(boolean left, boolean right) {
            return !left && !right;
        }
    };

    public final static BinaryConnective XOR = new BinaryConnective("⊕", 30, "⊻", "↮", "≢") {
        @Override
        public boolean computeTruth(boolean left, boolean right) {
            return (left && !right) || (!left && right);
        }
    };

    public final static BinaryConnective IF = new BinaryConnective("→", 20, "->", "⇒", "⊃") {
        @Override
        public boolean computeTruth(boolean left, boolean right) {
            return !left || right;
        }
    };

    public final static BinaryConnective NIF = new BinaryConnective("↛", 20, "/->", "-/>" ) {
        @Override
        public boolean computeTruth(boolean left, boolean right) {
            return left && !right;
        }
    };

    public final static BinaryConnective IF_CON = new BinaryConnective("←", 20, "<-", "⇐", "⊂") {
        @Override
        public boolean computeTruth(boolean left, boolean right) {
            return left || !right;
        }
    };

    public final static BinaryConnective NIF_CON = new BinaryConnective("↚", 20, "</-", "<-/") {
        @Override
        public boolean computeTruth(boolean left, boolean right) {
            return !left && right;
        }
    };

    public final static BinaryConnective IFF = new BinaryConnective("↔", 10, "<->", "≡", "⇔", "=") {
        @Override
        public boolean computeTruth(boolean left, boolean right) {
            return (left || !right) && (!left || right);
        }
    };

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
    public static Connective getConnective(String connective) {
        for (Connective con : ConnectiveFactory.CONNECTIVES) {
            if (con.getOfficialSymbol().equals(connective)) return con;
        }
        return null;
    }

    public static int getPrecedence(String connective) {
        return getConnective(connective) != null ? getConnective(connective).getPrecedence() : 0;
    }

}
