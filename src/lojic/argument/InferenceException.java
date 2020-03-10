package lojic.argument;

import lojic.argument.inference_rule.InferenceRule;

import java.util.Formatter;

/**
 * @author AlienIdeology
 *
 * InferenceException is thrown when an {@link InferenceRule} deems a {@link Line} syntactically invalid
 */
public class InferenceException extends Exception {

    /**
     * Get the string representation of a {@link InferenceException}'s
     * error indicator
     *
     * @param line The string line
     * @return The indicator string
     */
    public static String generateIndicator(Line line) {
        return line.getNumber() + ". " + line.toString();
    }

    private Line line;
    private InferenceRule inferenceRule;

    public InferenceException(Line line, InferenceRule inferenceRule, Type type) {
        super(type + "\n" + generateIndicator(line));
        this.line = line;
        this.inferenceRule = inferenceRule;
    }

    public InferenceException(Line line, InferenceRule inferenceRule, Type type, Object... args) {
        super(type.format(args) + "\n" + generateIndicator(line));
        this.line = line;
        this.inferenceRule = inferenceRule;
    }

    public Line getLine() {
        return line;
    }

    public InferenceRule getInferenceRule() {
        return inferenceRule;
    }

    public enum Type {

        MISSING_INFERENCE("Any line of proof must have a specified inference!"),
        DEPENDENCY_NUM_TOO_BIG("Any line cannot have a dependency number greater than its own line number!"),
        DEPENDENCY_NUM_TOO_SMALL("Any line cannot have a dependency number smaller than 1!"),

        /* Premise */
        PREMISE_IN_ASM("A premise cannot be introduced within the scope of any assumption!"),

        /* Assumption */
        // %s: For line type (A premise, An assumption, etc)
        MISSING_DEPENDENCY_NUM("%s cannot have any dependency numbers!"),

        // %s: For line types missing (a conclusion line, inference rule, etc)
        MISSING_LINE("Missing %s!")

        ;

        // shared formatter
        private static Formatter formatter = new Formatter();

        private final String msg;

        Type(String msg) {
            this.msg = msg;
        }

        public String format(Object... args) {
            return formatter.format(msg, args).toString();
        }

        @Override
        public String toString() {
            return msg;
        }

    }
}
