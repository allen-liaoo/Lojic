package lojic.parser;

/**
 * @author AlienIdeology
 *
 * A RuntimeException that is thrown when LojicLexer does not recognize the syntax of a logical expression
 */
public class SyntaxException extends RuntimeException {

    /**
     * Get the string representation of a {@link SyntaxException}'s
     * error indicator, which puts a ^ under the location on a string which the
     * syntax exception occurred
     *
     * @param formula The string formula
     * @param index The location of the error
     * @return The indicator string
     */
    public static String generateIndicator(String formula, int index) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < index; i++) {
            builder.append(" ");
        }

        return formula + "\n" + builder.toString() + "^";
    }

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
     * @param baseString The formula with underlying indicator
     */
    public SyntaxException(int index, String message, String baseString) {
        super("Index " + index + " - " + message + "\n" + generateIndicator(baseString, index));
        this.index = index;
        this.indicator = generateIndicator(baseString, index);
    }

    /**
     * Error message of encountering an unexpected character
     *
     * @param index The index (0~n) of the string
     * @param character The character
     * @param baseString The formula with underlying indicator
     */
    public SyntaxException(int index, CharSequence character, String baseString) {
        super("Index " + index + " - Unexpected character " + "\"" + character + "\"\n" + generateIndicator(baseString, index));
        this.index = index;
        this.indicator = generateIndicator(baseString, index);
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
