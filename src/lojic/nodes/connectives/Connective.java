package lojic.nodes.connectives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author AlienIdeology
 *
 * Logical operators, including Binary and Unary operators
 * @see lojic.nodes.ConnectiveFactory
 */
public abstract class Connective {

    private final String symbol;
    private final String[] symbols;
    private final int precedence;

    /**
     * @param official The official symbol which the parser recognizes
     * @param precedence The order of precedence. The lower it is, the higher the precedence is.
     *                   This value ranges from {@code 1~5}.
     * @param others Other symbols for the connective
     */
    public Connective(String official, int precedence, String... others) {
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

}
