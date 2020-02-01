package lojic.parser.token;

import lojic.parser.StringReader;

/**
 * @author AlienIdeology
 */
public class Token implements CharSequence {

    private StringReader reader;
    private String string;
    private Type type;
    private int location;

    public Token(StringReader reader, String string, Type type) {
        this.reader = reader;
        this.string = string;
        this.type = type;
        this.location = 0;
    }

    public Token(StringReader reader, String string, Type type, int location) {
        this.reader = reader;
        this.string = string;
        this.type = type;
        this.location = location;
    }

    public StringReader getReader() {
        return reader;
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

    public Type getType() {
        return type;
    }

    public boolean isType(Type type) {
        return this.type.equals(type);
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
     */
    public enum Type {

        // Raw type that StringReader returns
        ATOM,
        UNARY_CONNECTIVE,
        BINARY_CONNECTIVE,
        PARENTHESIS_OPEN ("(", "(", "{", "["),
        PARENTHESIS_CLOSE (")", ")", "}", "]"),
        UNKNOWN,


        // Processed type
        // Parser replaces UNARY, BINARY and PARENTHESIS with FORMULA
        FORMULA;

        public String OFFICIAL_SYMBOL;
        public String[] SYMBOLS;
        // symbols must include the official symbol.
        // If String symbol is defined as symbols[0], there would be an ExceptionInInitializerError
        Type(String symbol, String... symbols) {
            OFFICIAL_SYMBOL = symbol;
            SYMBOLS = symbols;
        }

        Type() {}

    }

}
