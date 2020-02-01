package lojic.parser.token;

import lojic.nodes.ConnectiveFactory;
import lojic.nodes.connectives.BinaryConnective;
import lojic.nodes.connectives.Connective;
import lojic.nodes.connectives.UnaryConnective;

/**
 * @author AlienIdeology
 */
public class TokenUtil {

    public static boolean isAtomic(String token) {
        for (char ch : token.toCharArray()) {
            if (!Character.isAlphabetic(ch) && !Character.isDigit(ch)) return false;
        }
        return true;
    }

    public static boolean isConnective(String token) {
        for (Connective con : ConnectiveFactory.CONNECTIVES) {
            if (token.equals(con.getOfficialSymbol())) return true;
        }
        return false;
    }

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

    public static boolean isUnaryConnective(String string) {
        for (Connective con : ConnectiveFactory.CONNECTIVES) {
            if (con instanceof UnaryConnective) {
                if (string.equals(con.getOfficialSymbol())) return true;
            }
        }
        return false;
    }

    public static boolean isOpenParenthesis(String string) {
        for (String s : Token.Type.PARENTHESIS_OPEN.SYMBOLS) {
            if(string.equals(s)) return true;
        }
        return false;
    }

    public static boolean isCloseParenthesis(String string) {
        for (String s : Token.Type.PARENTHESIS_CLOSE.SYMBOLS) {
            if(string.equals(s)) return true;
        }
        return false;
    }

    public static boolean isParenthesized(String string) {
        return string.startsWith(Token.Type.PARENTHESIS_OPEN.OFFICIAL_SYMBOL) &&
                string.endsWith(Token.Type.PARENTHESIS_CLOSE.OFFICIAL_SYMBOL);
    }

    public static boolean isFormula(String token) {
        for (Connective con : ConnectiveFactory.CONNECTIVES) {
            if (token.contains(con.getOfficialSymbol())) return true;
        }
        return false;
    }

    // Strip a string of all unofficial connective symbols and white spaces
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
}
