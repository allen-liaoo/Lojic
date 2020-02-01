package lojic.parser;

import lojic.parser.token.Token;
import lojic.parser.token.TokenUtil;

/**
 * @author AlienIdeology
 *
 * A reader of logical expressions
 * Reads a string in terms of groups {@code alphabetic-chars|parenthesis|connective|unrecognized-char}
 * Only {@code alphabetic-chars} are grouped into a char sequence with length > 1
 */
public class StringReader {

    private String string;
    private int index;

    public StringReader (String string) {
        this.string = string;
        index = -1;
    }

    /**
     * @return 1. String of alphabetic character(s)
     *         2. Char of parenthesis
     *         3. Char of connectives
     *         4. Char of unrecognized characters (Numbers, unicodes)
     */
    public Token next() {
        index++;
        String ch = string.substring(index, index+1);
        Token.Type type;

        if (TokenUtil.isBinaryConnective(ch)) type = Token.Type.BINARY_CONNECTIVE;
        else if (TokenUtil.isUnaryConnective(ch)) type = Token.Type.UNARY_CONNECTIVE;
        else if (TokenUtil.isOpenParenthesis(ch)) {
            ch = Token.Type.PARENTHESIS_OPEN.OFFICIAL_SYMBOL;
            type = Token.Type.PARENTHESIS_OPEN;
        }
        else if (TokenUtil.isCloseParenthesis(ch)) {
            ch = Token.Type.PARENTHESIS_CLOSE.OFFICIAL_SYMBOL;
            type = Token.Type.PARENTHESIS_CLOSE;
        }
        else if (TokenUtil.isAtomic(ch)) {
            type = Token.Type.ATOM;
            StringBuilder cache = new StringBuilder(ch);
            while (hasNext()) {
                ch = peekChar();
                if (TokenUtil.isAtomic(ch)) {
                    cache.append(ch);
                    index++;
                } else {
                    break;
                }
            }
            ch = cache.toString();
        }
        else type = Token.Type.UNKNOWN;

        return new Token(this, ch, type);
    }

    /**
     * @return The next Token, or null if there is no next token
     */
    public Token peek() {
        Token token = null;
        if (hasNext()) {
            int temp = index;
            token = next();
            setIndex(temp);
        }
        return token;
    }

    private String peekChar() {
        return string.substring(index+1, index+2);
    }

    public boolean hasNext() {
        return string.length() > index+1;
    }

    public int index() {
        return index;
    }

    public void setIndex(int in) {
        index = in;
    }

    public void updateString(String string) {
        this.string = string;
    }

    public int length() {
        return string.length();
    }

    @Override
    public String toString() {
        return string;
    }
}
