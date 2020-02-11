package lojic.nodes.connectives;

import lojic.DefaultFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author AlienIdeology
 *
 * Logical operators, including Binary and Unary operators
 * @see DefaultFactory
 */
public class Connective {

    private final Object connective;
    private final String symbol;
    private final String[] symbols;
    private final int precedence;
    private boolean isRightAssociative;

    /**
     * Constructor of a binary connective
     * For examples of how to create instances of binary connectives,
     * @see DefaultFactory for its binary connectives
     *
     * @param connective The functional interface that returns this connective's truth values
     * @param official The official symbol which the parser recognizes
     * @param precedence The order of precedence. The lower it is, the higher the precedence is.
 *                   This value ranges from {@code 1~5}.
     * @param others Other symbols for the connective
     */
    public Connective(BinaryConnective connective, String official, int precedence, String... others) {
        this.connective = connective;
        symbol = official;
        this.precedence = precedence;
        symbols = others;
    }

    /**
     * Constructor of a unary connective
     * For an example of how to create an instance of an unary connective,
     * @see DefaultFactory#NEG
     *
     * @param connective The functional interface that returns this connective's truth values
     * @param official The official symbol which the parser recognizes
     * @param precedence The order of precedence. The lower it is, the higher the precedence is.
 *                   This value ranges from {@code 1~5}.
     * @param others Other symbols for the connective
     */
    public Connective(UnaryConnective connective, String official, int precedence, String... others) {
        this.connective = connective;
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
        return connective instanceof BinaryConnective;
    }

    /**
     * Check if this class denotes a UnaryConnective
     *
     * @return true if this class denotes a UnaryConnective
     */
    public boolean isUnary() {
        return connective instanceof UnaryConnective;
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
     * @see lojic.parser.LojicParser#setAssociativity(int, boolean)  for setting associativity of connectives
     *
     * @param isRightAssociative true if the connective is right associative,
     *                           false if it is left associative.
     */
    public void setAssociativity(boolean isRightAssociative) {
        this.isRightAssociative = isRightAssociative;
    }

    /**
     * Return the truth value of the connective given its operand'(s) truth values
     *
     * @param booleans The operand'(s) truth values
     * @return The truth value of the connective
     * @throws IllegalArgumentException if the number of boolean arguments passed to this method does not
     *          fit the type of connective this object denotes.
     */
    public boolean computeTruth(boolean... booleans) throws IllegalArgumentException {
        if (connective instanceof BinaryConnective) {
            if (booleans.length != 2) throw new IllegalArgumentException("Unexpected number of parameter(s)!");
            else return ((BinaryConnective) connective).computeTruth(booleans[0], booleans[1]);
        } else if (connective instanceof UnaryConnective) {
            if (booleans.length != 1) throw new IllegalArgumentException("Unexpected number of parameter(s)!");
            else return ((UnaryConnective) connective).computeTruth(booleans[0]);
        } else {
            return false; // This will never be reached
        }
    }


}
