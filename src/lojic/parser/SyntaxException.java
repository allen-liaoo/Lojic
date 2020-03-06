package lojic.parser;

/**
 * @author AlienIdeology
 *
 * A RuntimeException that is thrown when LojicLexer does not recognize the syntax of a logical expression
 */
public class SyntaxException extends RuntimeException {

    private final String indicator;
    private final int index;

    public SyntaxException(String message) {
        super(message);
        this.indicator = null;
        this.index = -1;
    }

    /**
     * Override a syntax exception's message with a new message
     *
     * @param newMsg The new string message
     * @param exception The original syntax exception
     */
    public SyntaxException(String newMsg, SyntaxException exception) {
        super(newMsg);
        this.index = exception.getIndex();
        this.indicator = exception.getIndicator();
    }

    /**
     * Custom error message with an index
     *
     * @param index The index (0~n) of the string
     * @param message The custom message
     * @param indicator The formula with underlying indicator
     */
    public SyntaxException(int index, String message, String indicator) {
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
    public SyntaxException(int index, CharSequence character, String indicator) {
        super("Index " + index + " - Unexpected character " + "\"" + character + "\"\n" + indicator);
        this.index = index;
        this.indicator = indicator;
    }

    /**
     * Get the index (of the string) in which the syntax exception occurs
     *
     * @return The index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the string indicator (which consists of a logical expression
     * and a char "^" pointing at the index in which this syntax exception occurred
     *
     * @return The string indicator
     */
    public String getIndicator() {
        return indicator;
    }

}
