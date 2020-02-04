package lojic.parser;

import lojic.nodes.ConnectiveFactory;
import lojic.nodes.connectives.BinaryConnective;
import lojic.nodes.connectives.Connective;
import lojic.nodes.connectives.UnaryConnective;
import lojic.parser.token.TokenType;

/**
 * @author AlienIdeology
 */
public class LojicUtil {

    public static boolean isAtomic(String token) {
        for (char ch : token.toCharArray()) {
            if (!Character.isAlphabetic(ch) && !Character.isDigit(ch)) return false;
        }
        return true;
    }

    // FEATURE: No symbols stripping - Change if condition
    public static boolean isConnective(String token) {
        for (Connective con : ConnectiveFactory.CONNECTIVES) {
            if (token.equals(con.getOfficialSymbol())) return true;
        }
        return false;
    }

    // FEATURE: No symbols stripping - Change if condition
    public static boolean isBinaryConnective(String string) {
        for (Connective con : ConnectiveFactory.CONNECTIVES) {
            if (con instanceof BinaryConnective) {
                if (string.equals(con.getOfficialSymbol())) {
                    return true;
                }
            }
        }
        return false;
    }

    // FEATURE: No symbols stripping - Change if condition
    public static boolean isUnaryConnective(String string) {
        for (Connective con : ConnectiveFactory.CONNECTIVES) {
            if (con instanceof UnaryConnective) {
                if (string.equals(con.getOfficialSymbol())) return true;
            }
        }
        return false;
    }

    public static boolean isOpenParenthesis(String string) {
        for (String s : TokenType.PARENTHESIS_OPEN.SYMBOLS) {
            if(string.equals(s)) return true;
        }
        return false;
    }

    public static boolean isCloseParenthesis(String string) {
        for (String s : TokenType.PARENTHESIS_CLOSE.SYMBOLS) {
            if(string.equals(s)) return true;
        }
        return false;
    }

    // FEATURE: No symbols stripping
    public static boolean isParenthesized(String string) {
        return string.startsWith(TokenType.PARENTHESIS_OPEN.OFFICIAL_SYMBOL) &&
                string.endsWith(TokenType.PARENTHESIS_CLOSE.OFFICIAL_SYMBOL);
    }

    public static boolean isFormula(String token) {
        for (Connective con : ConnectiveFactory.CONNECTIVES) {
            if (token.contains(con.getOfficialSymbol())) return true;
        }
        return false;
    }

    // Strip a string of all unofficial connective symbols and white spaces
    // FEATURE: No symbols stripping - Remove for loops
    public static String strip(String input) {
        input = input.replaceAll("\\s", ""); // Get rid of all white spaces

        for (Connective con : ConnectiveFactory.CONNECTIVES) {
            for (String s : con.getSymbols()) {
                if (input.contains(s))
                    input = input.replace(s, con.getOfficialSymbol());
            }
        }
        return input;
    }

    // For printing detailed error messages which indicates the location the error occurs
    public static String generateIndicator(String formula, int index) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < index; i++) {
            builder.append(" ");
        }
        return formula + "\n" + builder.toString() +
                "\u001b[31m" + // Red word
                "^" +
                "\u001b[0m"; // Reset
    }

}
