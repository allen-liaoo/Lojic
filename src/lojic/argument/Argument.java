package lojic.argument;

import lojic.DefaultFactory;
import lojic.argument.inference_rule.InferenceRule;
import lojic.argument.inference_rule.PremiseIR;
import lojic.nodes.Node;
import lojic.nodes.connectives.BinaryConnective;
import lojic.nodes.connectives.Connective;
import lojic.parser.LojicParser;
import lojic.parser.SyntaxException;
import lojic.table.TTableBuilder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author AlienIdeology
 *
 * An argument represents a list premises, lines of proofs, and a conclusion
 */
public class Argument {

    /* Sequent Strings */
    /**
     * The character {@value #PREMISE_SEPARATOR} which separates the premises within a sequent
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

    // TODO: Merge premises and proofs list
    private List<Line> lines;

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
        this.tableBuilder = new TTableBuilder();
        this.lines = new ArrayList<>();
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
     * It is recommended to invoke this method along with {@link #isValid()} for calculating semantic validity.
     * Keep in mind that even if the conclusion can be proved by premises alone (with no further lines of proof),
     * the conclusion line which this method will add to the argument has no specified inference rule, so invoking
     * {@link #checkProvability()} immediately will result in {@link InferenceException}.
     *
     * @see #PREMISE_SEPARATOR for the supported character {@value #PREMISE_SEPARATOR} that denote a separator of premises in a sequent
     * @see #LOGICAL_CONSEQUENCE for all supported characters ({@code ⊢, ⊨}) that denote a logical consequence
     *
     * @param sequent The string sequent
     * @throws SyntaxException if there is not 1 logical consequence, or if the conclusion
     *                      string is empty, or if one of the premises is empty, or if the premises or the conclusion contain
     *                      illegal syntax
     * @throws IllegalArgumentException for some exception encountered when adding premises
     *                              See {@link #addPremises(String...)} for why such errors occur
     */
    public Argument fromSequent(String sequent) {
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

        if (!sequent.isEmpty() && !isBlank(sequent)) {
            String[] prems = sequent.split(PREMISE_SEPARATOR);

            for (String p : prems) {
                if (p.isEmpty() || isBlank(p))
                    throw new SyntaxException("A valid sequent must have 1 less comma \",\" than the number of premises!");
            }

            addPremises(prems);
        }

        addLineOfProof(conclusion, null);
        return this;
    }

    /**
     * Add premises to the end of this argument
     * @see #addPremises(int, String...) for more information
     *
     * @param premises The string premises
     * @return This argument for method chaining
     * @throws IllegalArgumentException if any premises provided is null or empty
     * @throws SyntaxException if the premises contain illegal syntax
     */
    public Argument addPremises(String... premises) {
        return addPremises(lineCount() + 1, premises);
    }

    /**
     * Add premises to a specified location in this argument,
     * shifting all lines at and bellow the location further down.
     *
     * Premises are lines which the conclusion's truth depends upon
     * For constructing arguments with syntactically valid inferences, see {@link PremiseIR#checkProvability(Line)}
     * for how premises are treated.
     *
     * @param lineNum The specified location (line number)
     * @param premises The premises to be added
     * @return This argument for method chaining
     * @throws IndexOutOfBoundsException if the location provided is {@code < 1 || > (lineCount() + 1)}
     * @throws IllegalArgumentException if any premises provided is null or empty
     * @throws SyntaxException if the premises contain illegal syntax
     */
    public Argument addPremises(int lineNum, String... premises) {
        if (lineNum < 0 || lineNum > lineCount() + 1)
            throw new IndexOutOfBoundsException("Index provided for setting premises is out of bounds! Index: " + lineNum);

        if (premises == null || premises.length == 0)
            return this;

        List<Line> ls = new ArrayList<>();

        for (String prem : premises) {
            // Null or empty premise
            if (prem == null || prem.isEmpty())
                throw new IllegalArgumentException("Cannot add a null or empty premise to the argument!");

            // strip each premises
            prem = parser.strip(prem);

            Line line = new Line(this);
            line.setNode(parser.parse(prem));
            line.setInferenceRule(DefaultFactory.IR_PREMISE);
            ls.add(line);
        }

        this.lines.addAll(lineNum - 1, ls);
        return this;
    }

    /**
     * Add lines of proof to the end of this argument
     *
     * This method adds lines with {@code null} inference rules and {@code 0} number of dependency lines.
     * Note that all lines of proof must have specified {@link InferenceRule} when checking for provability.
     *
     * @see #addLineOfProof(String, InferenceRule, int...) for more information on what are lines of proof
     *
     * @param lines The non-null, non-empty string lines
     * @return This argument for method chaining
     * @throws IllegalArgumentException If any of the strings provided are null or empty.
     * @throws SyntaxException if any of the strings contain illegal syntax
     */
    public Argument addLinesOfProof(String... lines) {
        for (String line : lines) {
            if (line == null || line.isEmpty())
                throw new IllegalArgumentException("Cannot add a null or empty line of proof to the argument!");

            addLineOfProof(line, null);
        }
        return this;
    }

    /**
     * Add a lines of proof to the specified location in this argument,
     * shifting all lines at and bellow the location further down.
     *
     * This method adds lines with {@code null} inference rules and {@code 0} number of dependency lines.
     * Note that all lines of proof must have specified {@link InferenceRule} when checking for provability.
     *
     * @see #addLineOfProof(int, String, InferenceRule, int...) for more information on what are lines of proof
     *
     * @param lines The non-null, non-empty string lines
     * @return This argument for method chaining
     * @throws IndexOutOfBoundsException  if the line number is {@code < 1 || > (lineCount() + 1)}
     * @throws IllegalArgumentException If any of the strings provided are null or empty.
     * @throws SyntaxException if any of the strings contain illegal syntax
     */
    public Argument addLinesOfProof(int lineNum, String... lines) {
        List<Line> toBeAdded = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.isEmpty())
                throw new IllegalArgumentException("Cannot add a null or empty line of proof to the argument!");

            line = parser.strip(line);

            Line li = new Line(this);
            li.setNode(parser.parse(line));

            toBeAdded.add(li);
        }

        this.lines.addAll(lineNum - 1, toBeAdded);
        return this;
    }

    /**
     * Add a line of proof to end of this argument,
     *
     * @see #addLineOfProof(int, String, InferenceRule, int...) for more information on lines of proof
     *
     * @param line The non-null string line
     * @param ir The inference rule, which may be null.
     * @param dependencyNumber The line numbers of dependency lines this line's inference depends on, which may be null or empty.
     * @return This argument for method chaining
     * @throws IllegalArgumentException If the string provided is null or empty.
     * @throws SyntaxException if the string line contains illegal syntax
     */
    public Argument addLineOfProof(String line, InferenceRule ir, int... dependencyNumber) {
        return addLineOfProof(lineCount() + 1, line, ir, dependencyNumber);
    }

    /**
     * Add a line of proof to the specified location in this argument,
     * shifting all lines at and bellow the location further down.
     *
     * A line of proof is a line in the argument with a specified {@link InferenceRule}, which allows one to
     * {@link #checkProvability()} of that line and of the whole argument. For adding premises, it is recommended
     * (and it is more convenient) to use the method {@link #addPremises(String...)}.
     *
     * @see InferenceRule for more information on inference rules
     *
     * @param lineNum The location (line number)
     * @param line The non-null string line
     * @param ir The inference rule, which may be null.
     *           Invoking {@link #checkProvability()} or {@link Line#checkProvability()} will result in {@link InferenceException}
     *           if any line has a null {@link InferenceRule}.
     * @param dependencyNumber The line numbers of dependency lines this line's inference depends on, which may be null or empty.
     *                         See {@link Line#getDependencyNumbers()} for more information.
     * @return This argument for method chaining
     * @throws IndexOutOfBoundsException  if the line number is {@code < 1 || > (lineCount() + 1)}
     * @throws IllegalArgumentException If the string provided is null or empty.
     * @throws SyntaxException if the string line contains illegal syntax
     */
    public Argument addLineOfProof(int lineNum, String line, InferenceRule ir, int... dependencyNumber) {
        if (lineNum < 0 || lineNum > lineCount() + 1)
            throw new IndexOutOfBoundsException("Index provided for setting premises is out of bounds! Index: " + lineNum);

        if (line == null || line.isEmpty())
            throw new IllegalArgumentException("Cannot add a null or empty line of proof!");

        // strip the line
        line = parser.strip(line);

        Line li = new Line(this);
        li.setNode(parser.parse(line));
        if (ir != null)
            li.setInferenceRule(ir);
        li.setInferenceRule(ir);
        if (dependencyNumber != null && dependencyNumber.length != 0)
            li.setDependencyNumbers(Arrays.stream(dependencyNumber).boxed().collect(Collectors.toList()));

        this.lines.add(lineNum - 1, li);
        return this;
    }

    /**
     * Remove lines from this argument
     *
     * @param lineNums The line numbers of lines to be removed
     * @return This argument for method chaining
     */
    public Argument removeLines(int... lineNums) {
        if (lineNums == null || lineNums.length == 0)
            return this;

        List<Line> toBeRemoved = new ArrayList<>();
        Arrays.stream(lineNums).forEach(num -> {
            if (num < 1 || num > lineCount())
                throw new IndexOutOfBoundsException("An index provided for removing lines is out of bounds! Index: " + num);

            toBeRemoved.add(lines.get(num - 1));
        });

        this.lines.removeAll(toBeRemoved);
        return this;
    }

    /**
     * Get a {@link Line} by its line number
     * which starts at 1, ends at {@link #lineCount()}
     *
     * @param number The integer line number
     * @return The line
     * @throws IndexOutOfBoundsException if the line number provided is out of bounds
     *                               ({@code # < 1} or {@code # > the conclusion's line number})
     */
    public Line getLine(int number) {
        Line res = null;
        if (number < 1 || number > lineCount()) {
            throw new IndexOutOfBoundsException("Line number provided is out of bounds!");
        } else if (number <= lineCount()) {
            res = lines.get(number-1);
        }
        return res;
    }

    /**
     * Get the number of lines in this argument
     *
     * @return The integer number of lines
     */
    public int lineCount() {
        return lines.size(); // premises + lines of proof + conclusion
    }

    /**
     * Get the {@link LojicParser} used to parse each line of proof
     *
     * @return The lojic parser
     */
    public LojicParser getParser() {
        return parser;
    }

    /**
     * Get the {@link TTableBuilder} used to build truth tables of each line,
     * which is never null.
     *
     * @return The table builder
     */
    public TTableBuilder getTableBuilder() {
        return tableBuilder;
    }

    /**
     * Get a list of {@link Line} in this argument
     *
     * @return The lines
     */
    public List<Line> getLines() {
        return lines;
    }

    /**
     * Get the conclusion {@link Line}.
     *
     * If there are no lines in this argument, or if all lines in this argument are premises,
     * then this returns null.
     *
     * If not, the conclusion will always be the last line of the argument.
     * Use {@link #addLineOfProof(String, InferenceRule, int...)} or {@link #addLinesOfProof(String...)} }
     * for adding a non-premise line to the end of the argument.
     *
     * @return The conclusion, or null no conclusion is found
     */
    public Line getConclusion() {
        return lines.isEmpty() ||
                lines.stream().allMatch(line -> line.getInferenceRule() instanceof PremiseIR)
                ? null : lines.get(lines.size()-1);
    }

    /**
     * Check whether the conclusion of this argument
     * is syntactically valid (provable) from the rest of the lines of proofs (including non-premises).
     *
     * Fitting {@link InferenceException} are thrown, and expected to be caught, whenever any line of proof is not syntactically valid.
     *
     * @see InferenceRule for more information on syntactic validity
     * @throws InferenceException if any inference is not allowed
     * @throws UnsupportedOperationException if the argument lacks a conclusion. (See {@link #getConclusion()})
     */
    public void checkProvability() throws InferenceException {
        if (getConclusion() == null)
            throw new UnsupportedOperationException("Cannot compute the provability of an argument when it lacks a conclusion!");

        // Check missing inference rules on each line, before validity
        for (Line line : lines) {
            if (line.getInferenceRule() == null)
                throw new InferenceException(line, null, InferenceException.Type.MISSING_INFERENCE);
        }

        // Check each line's validity
        for (Line line : lines) {
            line.getInferenceRule().checkProvability(line);
        }
    }

    /**
     * Check if this argument is semantically valid,
     * which means that all possible combinations atoms's truth-values
     * that lead to true premises will never lead to a false conclusion.
     *
     * This method extracts premises and the conclusion (last line of the argument) from the lines of proof
     * stored in this argument, and checks for their semantic validity. All lines of proof that are not premises are ignored.
     *
     * This method does not check the provability of the conclusion from premises and lines of proofs, so no
     * {@link InferenceException} will be thrown. For checking syntactic validity, see {@link #checkProvability()}.
     *
     * @return True if the root is never false given that the premises are true
     * @throws UnsupportedOperationException if no conclusions are registered in this argument. See {@link #getConclusion()}
     *                                      for what instances has conclusions
     */
    public boolean isValid() {
        if (getConclusion() == null)
            throw new UnsupportedOperationException("Cannot compute the semantic validity of an argument when it lacks a conclusion!");

        // Corresponding Conditional
        // Premises : {A, B...}, Conclusion: Z
        // If the argument is valid, then (A & B & C...) -> Z must be true
        StringBuilder corrCon = new StringBuilder();
        int linecount = 0;
        HashMap<Integer, Integer> tracker = new HashMap<>(); // tracks index of each lines of proof in the corrCon and the lint number

        /*
            If the parser does recognize AND and IF connective, then use those connectives.

            IF the parser does not recognizing characters like & amd ->
            we create unique instances of connectives that has the same truth function
            but with rare characters to avoid repeated official characters
         */

        // AND
        boolean setAnd = false;
        boolean[] andV = new boolean[]{true, false, false, false};
        Connective and = parser.getConnectives().stream()
                .filter(con -> Arrays.equals(con.getPossibleTruths(), andV))
                .findAny().orElse(null);

        if (and == null) {
                // assign rare characters
               and = new BinaryConnective((left, right) -> left && right,
                       "ˋ", 40);
            parser.addConnectives(and);
            setAnd = true;
        }

        // IF
        boolean setImpl = false;
        boolean[] implV = new boolean[]{true, false, true, true};
        Connective impl = parser.getConnectives().stream()
                .filter(con -> Arrays.equals(con.getPossibleTruths(), implV))
                .findAny().orElse(null);

        if (impl == null) {
                impl = new BinaryConnective((left, right) -> !left || right,
                    "ˊ", 20);
            parser.addConnectives(impl);
            setImpl = true;
        }

        // Premises
        if (!lines.isEmpty()) {
            corrCon.append("(");
            Line lastP = lines.get(lines.size() - 1);
            for (int i = 0; i < lines.size()-1; i++) {
                Line prem = lines.get(i);
                linecount++;
                tracker.put(corrCon.length() + 2, linecount);
                corrCon.append("(").append(prem).append(")");

                if (!prem.toString().equals(lastP.toString()))
                    corrCon.append(and.getOfficialSymbol());
            }
            corrCon.append(")")
                    .append(impl.getOfficialSymbol());
        }

        linecount++;
        tracker.put(corrCon.length()+2, linecount);

        // Conclusion
        corrCon.append("(")
                .append(getConclusion())
                .append(")");

        Node node = parser.parse(parser.strip(corrCon.toString()));

        if (setAnd)
            parser.removeConnectives(and);

        if (setImpl)
            parser.removeConnectives(impl);

        return node.getTableBuilder()
                .copySetting(tableBuilder)
                .build()
                .rootIsTautology();

         /*
         Old code. above parsing will never meet SyntaxException due to lines of proofs already being checked for syntax
         at each individual methods

         try {
             parser,parse
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
             } catch (SyntaxException se2) { // catches syntax exception by parsing the specific line alone
                 throw new SyntaxException("Syntax Error on the #" + lNumber + " line of proof\n" + se2.getMessage(), se2);
             }
         }*/
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
        lines.forEach(s -> str.append(index.getAndIncrement()).append(". ").append(s).append("\n"));
        return str.toString();
    }

    @Override
    public String toString() {
        return "Argument{" +
                "parser=" + parser +
                ", calculator=" + tableBuilder +
                ", premises=" + lines +
                '}';
    }

    private boolean isBlank(String str) {
        for (char ch : str.toCharArray()) {
            if (!Character.isWhitespace(ch)) return false;
        }
        return true;
    }

}
