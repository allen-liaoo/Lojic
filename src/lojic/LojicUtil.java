package lojic;

import lojic.nodes.connectives.Connective;

/**
 * @author AlienIdeology
 */
// FEATURE: No symbols stripping
public class LojicUtil {

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
