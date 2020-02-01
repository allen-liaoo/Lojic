package lojic.parser;

/**
 * @author AlienIdeology
 */
public class SyntaxException extends RuntimeException {

    private String indicator;
    private int index;

    public SyntaxException (String message) {
        super(message);
    }

    /**
     * Custom error message with an index
     *
     * @param index The index (0~n) of the string
     * @param message The custom message
     * @param indicator The formula with underlying indicator
     */
    public SyntaxException (int index, String message, String indicator) {
        super("Index " + index + " - " + message + "\n" + indicator);
        this.index = index;
        this.indicator = indicator;
    }

    /**
     * Error message of encountering an unexpected character
     *
     * @param index The index (0~n) of the string
     * @param character The character
     * @param indicator The formula with underlying indicator
     */
    public SyntaxException (int index, CharSequence character, String indicator) {
        super("Index " + index + " - Unexpected character " + "\"" + character + "\"\n" + indicator);
        this.index = index;
        this.indicator = indicator;
    }

    public int getIndex() {
        return index;
    }

    public String getIndicator() {
        return indicator;
    }

}
