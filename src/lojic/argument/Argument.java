package lojic.argument;

import lojic.nodes.Node;
import lojic.parser.LojicParser;
import lojic.parser.SyntaxException;
import lojic.table.TTableBuilder;
import lojic.table.TruthTable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author AlienIdeology
 *
 * An argument object that represents a list premises, lines of proofs, and a conclusion
 * This is used to check for semantic validity
 */
public class Argument {

    /* Sequent Strings */
    /**
     * The character {@code ','} which separates the premises within a sequent
     * This is a constant, it cannot be changed or customized
     *
     * @see #fromSequent(String) for constructing an Argument based on a string sequent
     */
    public static final String PREMISE_SEPARATOR = ",";

    /**
     * The characters {@code {'⊢', '|-', '⊨', '|='}} which denotes syntactic or semantic entailment in a sequent
     * These are constants, they cannot be changed or customized
     *
     * All of the characters in this char array serves the same purpose of separating premises from the conclusion
     * in a sequent
     * @see #fromSequent(String) for constructing an Argument based on a string sequent
     */
    public static final String[] LOGICAL_CONSEQUENCE = new String[]{
            // Syntactic consequence, derivability, provability
            "⊢",
            "|-",
            // Semantic consequence
            "⊨",
            "|="
    };

    private final LojicParser parser;
    private TTableBuilder tableBuilder;

    private List<String> premises;
    //private List<TruthTable> proofs; // lines of proof
    private String conclusion;

    /**
     * Constructor for Argument
     * This sets the {@link LojicParser} to a default LojicParser
     * (used to parse each lines of proof),
     * and {@link TTableBuilder} to a default, empty TTableBuilder
     */
    public Argument() {
        this(new LojicParser());
    }

    /**
     * Constructor for Argument
     * This sets the {@link LojicParser} to the specified parser
     * (used to parse each lines of proof),
     * and {@link TTableBuilder} to a default, empty TTableBuilder
     *
     * @param parser The specified LogicParser
     * @throws NullPointerException if the parser is null
     */
    public Argument(LojicParser parser) {
        Objects.requireNonNull(parser, "The LojicParser for the argument cannot be null!");
        this.parser = parser;
        this.tableBuilder = new TTableBuilder()
                .useDefaultTFAtoms()
                .disableSubColumns();
        this.premises = new ArrayList<>();
    }

    /**
     * Set the {@link TTableBuilder} which this Argument uses to construct truth tables
     *
     * @param tableBuilder The table setting
     * @return This argument for method chaining
     * @throws NullPointerException if the setting is null
     */
    public Argument setTableBuilder(TTableBuilder tableBuilder) {
        Objects.requireNonNull(parser, "The TTableSetting for the argument cannot be null!");
        this.tableBuilder = tableBuilder;
        return this;
    }

    /**
     * Add premises and a conclusion based on a string sequent
     *
     * A sequent is a general kind of assertion in the following form:
     * {@code A1, ... , An ⊢ B}
     * in which {@code As} are premises, {@code B} is the conclusion,
     * {@code ,} is the character that separates each premises,
     * and {@code ⊢} is a type of logical consequence (syntactic consequence)
     *
     * For a sequent to be of valid syntax, the character(s) of logical consequence and conclusion must be present
     * There can be only one logical consequence
     * The amount of premises must be one more than the amount of commas (If there is one comma,
     * then there must be two premises)
     *
     * @see #PREMISE_SEPARATOR for the supported character {@value #PREMISE_SEPARATOR} that denote a separator of premises in a sequent
     * @see #LOGICAL_CONSEQUENCE for all supported characters ({@code ⊢, ⊨}) that denote a logical consequence
     *
     * @param sequent The string sequent
     * @throws SyntaxException if there is not 1 logical consequence, or if the conclusion
     *                      string is empty, or if one of the premises is empty
     * @throws IllegalArgumentException for each exception encountered when adding premises
     *                              See {@link #addPremises(String...)} for why such errors occur
     */
    public Argument fromSequent(String sequent) {
        Argument arg = new Argument();
        // Build regex for matching logical consequences
        StringBuilder regex = new StringBuilder();
        for (String lc : LOGICAL_CONSEQUENCE) {
            if (lc.startsWith("|"))
                regex.append("\\");

            regex.append(lc);

            if (!lc.equals(LOGICAL_CONSEQUENCE[LOGICAL_CONSEQUENCE.length-1]))
                regex.append("|");
        }

        Matcher matcher = Pattern.compile(regex.toString())
                .matcher(sequent);

        // No logical consequence
        matcher.find();
        if (matcher.hitEnd()) throw new SyntaxException("A valid sequent must have a symbol of logical consequence!");
        matcher.reset();

        int count = 0;
        String conclusion = "";
        while (matcher.find()) {
            count++;
            // More than 1 logical consequence
            if (count > 1) throw new SyntaxException("A valid sequent must not have more than 1 symbol of logical consequence!");

            int start = matcher.start() + 1;
            if (matcher.group().length() > 1) // for cases such as |-
                start++;
            conclusion = sequent.substring(start);

            // No conclusion
            if (conclusion.isEmpty()) throw new SyntaxException("A valid sequent must have a conclusion!");

            sequent = sequent.substring(0, matcher.start());
        }

        if (!sequent.isEmpty() && !arg.isBlank(sequent)) {
            String[] prems = sequent.split(PREMISE_SEPARATOR);

            for (String p : prems) {
                if (p.isEmpty() || arg.isBlank(p))
                    throw new SyntaxException("A valid sequent must have 1 less comma \",\" than the number of premises!");
            }

            arg.addPremises(prems);
        }

        arg.setConclusion(conclusion);
        return arg;
    }

    /**
     * Add premises to this Argument
     * Premises are lines which the conclusion's truth depends upon
     *
     * @param premises The string premises
     * @return This argument for method chaining
     * @throws NullPointerException if any premises provided is null or empty
     * @throws IllegalArgumentException if any premises is already registered in this Argument
     */
    public Argument addPremises(String... premises) {
        if (premises == null || Arrays.stream(premises).anyMatch(Objects::isNull)
                || Arrays.stream(premises).anyMatch(String::isEmpty))
            throw new NullPointerException("Cannot add a null or empty premise to the argument!");

        premises = Arrays.stream(premises)
                .map(parser::strip) // strip each premises
                .toArray(String[]::new);

        if (Arrays.stream(premises).anyMatch(this.premises::contains) ||
                containRepeats(premises))
            throw new IllegalArgumentException("Cannot add a premise that is already registered in an argument!");

        this.premises.addAll(Arrays.asList(premises));
        return this;
    }

    /**
     * Remove premises to this Argument
     *
     * @param premises The string premises
     * @return This argument for method chaining
     * @throws NullPointerException if any premises provided is null or empty
     */
    public Argument removePremises(String... premises) {
        if (premises == null || Arrays.stream(premises).anyMatch(Objects::isNull)
                || Arrays.stream(premises).anyMatch(String::isEmpty))
            throw new NullPointerException("Cannot remove a null or empty premise to the argument!");

        this.premises.removeAll(Arrays.stream(premises)
                .map(parser::strip) // strip each premises
                .collect(Collectors.toList()));
        return this;
    }

    /**
     * Set the conclusion of this Argument
     * If a conclusion already existed, this overrides the conclusion
     *
     * @param conclusion The conclusion
     * @return This argument for method chaining
     * @throws NullPointerException if the conclusion provided is null
     * @throws IllegalArgumentException if the conclusion is empty
     */
    public Argument setConclusion(String conclusion) {
        Objects.requireNonNull(conclusion, "Cannot set a null conclusion to the argument!");
        if (conclusion.isEmpty()) throw new IllegalArgumentException("Cannot set an empty conclusion to the argument!");

        this.conclusion = parser.strip(conclusion);
        return this;
    }

    /**
     * Get a line by its line number
     * which starts at 1
     *
     * @param number The integer line number
     * @return The string line
     * @throws IndexOutOfBoundsException if the line number provided is out of bounds
     *                               ({@code # < 0} or {@code # > the conclusion's line number})
     */
    public String getLine(int number) {
        String res;
        if (number < 0 || number > lineCount()) {
            throw new IndexOutOfBoundsException("Line number provided is out of bounds!");
        } else if (number < lineCount()) {
            res = premises.get(number-1);
        } else {
            res = conclusion;
        }
        return res;
    }

    /**
     * Get the {@link Node} object of a line
     *
     * @param number The line number
     * @return The node object
     */
    public Node getNode(int number) {
        Node node = parser.parse(getLine(number));
        node.getTableBuilder()
            .copySetting(tableBuilder);
        return node;
    }

    /**
     * Get a {@link TruthTable} from a line number
     * The table could be from any line of proof
     *
     * @param number The line number
     * @return The truth table
     * @throws IndexOutOfBoundsException if the line number provided is out of bounds
     *                              ({@code # < 0} or {@code # > the conclusion's line number})
     * @throws SyntaxException if the parser encountered any lines of proof with illegal syntax
     */
    public TruthTable getTruthTable(int number) {
        return getNode(number)
                .getTableBuilder()
                .build();
    }

    /**
     * Get the number of lines in this argument
     *
     * @return The integer number of lines
     */
    public int lineCount() {
        return premises.size() - 1 //premises
                + 1; // conclusion
    }

    /**
     * Get a list of string premises
     *
     * @return The string premises
     */
    public List<String> getPremises() {
        return premises;
    }

    /**
     * Get the string conclusion
     *
     * @return The string conclusion
     */
    public String getConclusion() {
        return conclusion;
    }

    /**
     * Check if this argument is semantically valid,
     * which means that all possible combinations atoms's truth-values
     * that lead to true premises will never lead to a false conclusion
     *
     * @return True if the root is never false given that the premises are true
     * @throws SyntaxException if the parser encountered any lines of proof with illegal syntax
     * @throws UnsupportedOperationException if the conclusion is not set yet
     */
    public boolean isValid() {
        if (conclusion == null)
            throw new UnsupportedOperationException("Cannot compute the validity of an argument when it lacks a conclusion!");

        // Corresponding Conditional
        // Premises : {A, B...}, Conclusion: Z
        // If the argument is valid, then (A & B & C...) -> Z must be true
        StringBuilder corrCon = new StringBuilder();
        int linecount = 0;
        HashMap<Integer, Integer> tracker = new HashMap<>(); // tracks index of each lines of proof in the corrCon and the lint number

        // Premises
        if (!premises.isEmpty()) {
            corrCon.append("(");
            String lastP = premises.get(premises.size() - 1);
            for (String prem : premises) {
                linecount++;
                tracker.put(corrCon.length() + 2, linecount);
                corrCon.append("(").append(prem).append(")");
                if (!prem.equals(lastP)) corrCon.append("&");
            }
            corrCon.append(")")
                    .append("→");
        }

        linecount++;
        tracker.put(corrCon.length()+2, linecount);

        // Conclusion
        corrCon.append("(")
                .append(conclusion)
                .append(")");

         try {

             return parser.parse(corrCon.toString())
                     .getTableBuilder()
                     .copySetting(tableBuilder)
                     .build()
                     .rootIsTautology();

         } catch (SyntaxException se) {
             int index = se.getIndex();
             int lNumber = 0;
             for (int key : tracker.keySet()) {
                 if (index >= key) {
                     lNumber = tracker.get(key);
                     break;
                 }
             }

             try {
                 parser.parse(getLine(lNumber));
             } catch (SyntaxException se2) { // catches syntaxexception by parsing the specific line alone
                 throw new SyntaxException("Syntax Error on the #" + lNumber + " line of proof\n" + se2.getMessage(), se2);
             }
         }
         return false;
    }

    /**
     * Get the string presentation of this argument
     * If the argument is in the form:
     * {@code P → Q, P ⊢ Q}
     * where {@code ⊢} is syntactic consequence and {@code ,} separates premises,
     * then the argument string is as follows:
     * 1. P → Q
     * 2. P
     * 3. Q
     * where 1 and 2 are premises and 3 is the conclusion
     *
     * @return The string argument
     */
    public String print() {
        StringBuilder str = new StringBuilder();
        AtomicInteger index = new AtomicInteger(1);
        premises.forEach(s -> str.append(index.getAndIncrement()).append(". ").append(s).append("\n"));
        str.append(index.getAndIncrement()).append(". ").append(conclusion);
        return str.toString();
    }

    @Override
    public String toString() {
        return "Argument{" +
                "parser=" + parser +
                ", calculator=" + tableBuilder +
                ", premises=" + premises +
                ", conclusion='" + conclusion + '\'' +
                '}';
    }

    private boolean isBlank(String str) {
        for (char ch : str.toCharArray()) {
            if (!Character.isWhitespace(ch)) return false;
        }
        return true;
    }

    private <T> boolean containRepeats(T[] array) {
        List<T> ts = Arrays.asList(array);
        for (int i = 0; i < ts.size(); i++) {
            for (int j = 0; j < ts.size(); j++) {
                if (i == j) continue;
                if (ts.get(i).equals(ts.get(j)))
                    return true;
            }
        }
        return false;
    }

}
