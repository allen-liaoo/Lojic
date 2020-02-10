package lojic;

import com.sun.istack.internal.Nullable;
import lojic.nodes.connectives.Connective;
import lojic.parser.TokenType;

/**
 * @author AlienIdeology
 */
// FEATURE: No symbols stripping
public class LojicUtil {

    public static boolean isAtomic(String token) {
        for (char ch : token.toCharArray()) {
            if (!Character.isAlphabetic(ch) && !Character.isDigit(ch)) return false;
        }
        return true;
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

    public static boolean isParenthesized(String string) {
        return string.startsWith(TokenType.PARENTHESIS_OPEN.OFFICIAL_SYMBOL) &&
                string.endsWith(TokenType.PARENTHESIS_CLOSE.OFFICIAL_SYMBOL);
    }

    public static boolean isFormula(String token) {
        for (Connective con : DefaultFactory.DEFAULT_CONNECTIVES) {
            if (token.contains(con.getOfficialSymbol())) return true;
        }
        return false;
    }

    public boolean isDefaultConnective(String token) {
        for (Connective con : DefaultFactory.DEFAULT_CONNECTIVES) {
            if (token.equals(con.getOfficialSymbol())) return true;
        }
        return false;
    }

    public boolean isDefaultBinaryConnective(String string) {
        for (Connective con : DefaultFactory.DEFAULT_CONNECTIVES) {
            if (con.isBinary()) {
                if (string.equals(con.getOfficialSymbol())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDefaultUnaryConnective(String string) {
        for (Connective con : DefaultFactory.DEFAULT_CONNECTIVES) {
            if (con.isUnary()) {
                if (string.equals(con.getOfficialSymbol())) return true;
            }
        }
        return false;
    }

    @Nullable
    public Connective getDefaultConnective(String connective) {
        for (Connective con : DefaultFactory.DEFAULT_CONNECTIVES) {
            if (con.getOfficialSymbol().equals(connective)) return con;
        }
        return null;
    }

    // Strip a string of all unofficial connective symbols and white spaces
    // FEATURE: No symbols stripping - Remove for loops
    public static String strip(String input) {
        input = input.replaceAll("\\s", ""); // Get rid of all white spaces

        for (Connective con : DefaultFactory.DEFAULT_CONNECTIVES) {
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

        return formula + "\n" + builder.toString() + "^";
    }

}
