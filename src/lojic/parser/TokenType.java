package lojic.parser;

import static lojic.DefaultFactory.CLOSE_PAREN;
import static lojic.DefaultFactory.OPEN_PAREN;

/**
 * @author AlienIdeology
 *
 * The type of strings the LojicLexer would encounter and handle accordingly
 *
 * This class is for the Lojic library's internal use only, users should ignore this
 */
enum TokenType {

    // Raw type that only LogicLexer.next() returns
    PARENTHESIS_OPEN (OPEN_PAREN[0],
            OPEN_PAREN[1], OPEN_PAREN[2], OPEN_PAREN[3]),

    PARENTHESIS_CLOSE (CLOSE_PAREN[0],

            CLOSE_PAREN[1], CLOSE_PAREN[2], CLOSE_PAREN[3]),
    UNKNOWN,

    @Deprecated
    END, // When the lexer runs out of string to parse


    // Processed type which LojicLexer returns
    // Parser replaces PARENTHESIS with FORMULA
    ATOM,
    UNARY_CONNECTIVE,
    BINARY_CONNECTIVE,
    FORMULA;
    // If a token is of type FORMULA yet is not a ParsedFormula
    // The token was parenthesized during lexing

    public String OFFICIAL_SYMBOL; // FEATURE: No symbols stripping - Change all occurrences
    public String[] SYMBOLS;
    // symbols must include the official symbol.
    // If String symbol is defined as symbols[0], there would be an ExceptionInInitializerError

    /**
     * @param symbol The official symbol
     * @param symbols All symbols, including the official one
     */
    TokenType(String symbol, String... symbols) {
        OFFICIAL_SYMBOL = symbol;
        SYMBOLS = symbols;
    }

    TokenType(){}

}
