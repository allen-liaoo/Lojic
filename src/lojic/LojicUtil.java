package lojic;

import lojic.nodes.connectives.Connective;

/**
 * @author AlienIdeology
 */
// FEATURE: No symbols stripping
public class LojicUtil {
    /**
     * Replace all unofficial symbols of default connectives with official symbols
     * Removes all white spaces
     *
     * @param input The string to be stripped
     * @return The string result
     */
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

    /**
     * Get the string representation of a {@link lojic.parser.SyntaxException}'s
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

}
