package lojic.nodes.connectives;

import lojic.DefaultFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author AlienIdeology
 *
 * Logical operators, including Binary and Unary operators
 * @see DefaultFactory for the default, supported operators
 */
public abstract class Connective {

    private final String symbol;
    private final String[] symbols;
    private final int precedence;
    private boolean isRightAssociative;

    Connective(String official, int precedence, String... others) {
        symbol = official;
        this.precedence = precedence;
        symbols = others;
    }

    /**
     * Get the symbol which the parser recognizes
     * @return the official symbol
     */
    public String getOfficialSymbol() {
        return symbol;
    }

    /**
     * Get all other symbols
     * @return the other symbols
     */
    public String[] getSymbols() {
        return symbols;
    }

    /**
     * Get all symbols
     * @return all symbols
     */
    public String[] getAllSymbols() {
        List<String> all = new ArrayList<>(Arrays.asList(symbols));
        all.add(symbol);
        return (String[]) all.toArray();
    }

    /**
     * Get the integer value of the precedence
     * @return precedence value in the interval {@code 1~5}
     */
    public int getPrecedence() {
        return precedence;
    }

    /**
     * Check if this class denotes a BinaryConnective
     *
     * @return true if this class denotes a BinaryConnective
     */
    public boolean isBinary() {
        return this instanceof BinaryConnective;
    }

    /**
     * Check if this class denotes a UnaryConnective
     *
     * @return true if this class denotes a UnaryConnective
     */
    public boolean isUnary() {
        return this instanceof UnaryConnective;
    }

    /**
     * Check if the connective is right associative.
     * In logic, all binary connectives are right associative. Unary connectives are neither right nor left associative.
     *
     * @return True if the connective is right associative.
     */
    public boolean isRightAssociative() {
        return isRightAssociative;
    }

    /**
     * Set the connective's associativity.
     * This method is for the Lojic library's internal use only, users should ignore this
     * @see lojic.parser.LojicParser#setAssociativity(int, boolean) for setting associativity of connectives
     *
     * @param isRightAssociative true if the connective is right associative,
     *                           false if it is left associative.
     */
    public void setAssociativity(boolean isRightAssociative) {
        this.isRightAssociative = isRightAssociative;
    }

    /**
     * Get an array of all possible {@code boolean} values that this connective
     * could return, based on the truth values of the atoms and/or formulas to the right
     * (and to the left, for binary connectives) of this connective
     *
     * For Unary connectives:
     * 1. This returns an array with a size of {@code 2}
     * 2. The array looks like this:
     * <pre>
     * +--------------+------------+
     * | Atom/Formula | Connective |
     * +--------------+------------+
     * |      T       | boolean[0] |
     * +--------------+------------+
     * |      F       | boolean[1] |
     * +--------------+------------+
     * </pre>
     *
     * For Binary Connectives:
     * 1. This returns an array with a size of {@code 4}
     * 2. The array looks like this:
     * <pre>
     * +--------------+--------------+------------+
     * | Atom/Formula | Atom/Formula | Connective |
     * +--------------+--------------+------------+
     * |      T       |      T       | boolean[0] |
     * +--------------+--------------+------------+
     * |      T       |      F       | boolean[1] |
     * +--------------+--------------+------------+
     * |      F       |      T       | boolean[2] |
     * +--------------+--------------+------------+
     * |      F       |      F       | boolean[3] |
     * +--------------+--------------+------------+
     * </pre>
     *
     * @return The array of possible truth values
     */
    public abstract boolean[] getPossibleTruths();

    /**
     * Return the truth value of the connective given its operand'(s) truth values
     * This is a wrapper method for the subclasses {@link BinaryConnective} and {@link UnaryConnective}
     *
     * @param booleans The operand'(s) truth values
     * @return The truth value of the connective
     * @throws IllegalArgumentException if the number of boolean arguments passed to this method does not
     *          fit the type of connective this object denotes.
     */
    public boolean computeTruth(boolean... booleans) throws IllegalArgumentException {
        if (this instanceof BinaryConnective) {
            if (booleans.length != 2) throw new IllegalArgumentException("Unexpected number of parameter(s)!");
            else return ((BinaryConnective) this).computeTruth(booleans[0], booleans[1]);
        } else if (this instanceof UnaryConnective) {
            if (booleans.length != 1) throw new IllegalArgumentException("Unexpected number of parameter(s)!");
            else return ((UnaryConnective) this).computeTruth(booleans[0]);
        }
        return false; // This will never be reached
    }


}
