package lojic.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AlienIdeology
 *
 * A string sequence that is generated by LojicLexer
 * A token can be a single character, or multiple characters (atom or formula, parenthesized or not)
 */
class Token implements CharSequence {

    private LojicLexer lexer;
    private String string;
    private TokenType type;
    private int location;

    public Token(LojicLexer lexer, String string, TokenType type) {
        this.lexer = lexer;
        this.string = string;
        this.type = type;
        this.location = 0;
    }

    public Token(LojicLexer lexer, String string, TokenType type, int location) {
        this.lexer = lexer;
        this.string = string;
        this.type = type;
        this.location = location;
    }

    public LojicLexer getLexer() {
        return lexer;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return string;
    }

    public TokenType getType() {
        return type;
    }

    public boolean isType(TokenType type) {
        return this.type.equals(type);
    }

    public boolean isParsedFormula() {
        return this instanceof ParsedFormula;
    }

    @Override
    public int length() {
        return string.length();
    }

    @Override
    public char charAt(int index) {
        return string.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return string.substring(start, end);
    }

    /**
     * @author AlienIdeology
     *
     * This is a wrapper for a formula in which its sub-components were already parsed.
     */
    static class ParsedFormula extends Token {

        private List<Token> tokens;

        public ParsedFormula(LojicLexer lexer, int location) {
            super(lexer, "", TokenType.FORMULA, location);
            this.tokens = new ArrayList<>();
        }

        public ParsedFormula(Token token) {
            this(token.getLexer(), token.getLocation());
            add(token);
        }

        public List<Token> getTokens() {
            return tokens;
        }

        public void add(Token token) {
            tokens.add(token);
        }

        public void setString(String string) {
            super.string = string;
        }

    }

}