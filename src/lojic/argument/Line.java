package lojic.argument;

import lojic.argument.inference_rule.InferenceRule;
import lojic.nodes.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AlienIdeology
 *
 * Line represents a premise, line of proofs, or a conclusion in the {@link Argument} object
 * Each line must contain a {@link Node}, which represents the structure of the logical formula of that line
 * Each line might also have an {@link InferenceRule} or a list of dependency numbers for checking provability.
 * @see Argument#checkProvability()
 */
public class Line {

    private final Argument argument;

    private Node node;
    private InferenceRule inferenceRule;
    private List<Integer> dependencyNumbers;

    Line(Argument argument) {
        this.argument = argument;
        this.node = null;
        this.inferenceRule = null;
        this.dependencyNumbers = new ArrayList<>();
    }

    /**
     * Get the {@link Argument} which this line belongs to
     *
     * @return The argument
     */
    public Argument getArgument() {
        return argument;
    }

    /**
     * Get the integer line number, which is between
     * {@code 1} and {@link Argument#lineCount()}.
     *
     * @return The line number
     */
    public int getNumber() {
        return argument.getLines().indexOf(this) + 1;
    }

    /**
     * Get the {@link Node} of this line
     *
     * @return The node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Check the provability of this line
     * @see Argument#checkProvability() for checking the provability of an argument
     *
     * @throws InferenceException when any inferences are not valid
     */
    public void checkProvability() throws InferenceException {
        if (inferenceRule == null)
            throw new InferenceException(this, null, InferenceException.Type.MISSING_INFERENCE);

        inferenceRule.checkProvability(this);
    }

    /**
     * Get the {@link InferenceRule} of this line
     *
     * @return The inference rule
     */
    public InferenceRule getInferenceRule() {
        return inferenceRule;
    }

    /**
     * Get the dependency numbers of this line
     * A dependency number is a line number of a line in which this line depends upon
     * for its inference. For example:
     * <pre>
     *     1. P         Premise
     *     2. P → Q     Premise
     *     3. Q         1, 2 MP
     * </pre>
     * {@code 1, 2} at the end of line 3 is the dependency numbers of line 3. Line 3 utilizes the inference rule
     * {@link lojic.DefaultFactory#IR_MODUS_POLLENS}, which looks at the two lines that allows the inference to be made.
     *
     * @return The list of integer dependency numbers
     */
    public List<Integer> getDependencyNumbers() {
        return dependencyNumbers;
    }

    /**
     * Check if this line has dependency numbers
     * The only lines that do not have dependency numbers are premises, assumptions, and inference rules such as
     * {@code theorem introduction}.
     *
     * @return true if this line has dependency numbers
     */
    public boolean hasDependencyNumbers() {
        return dependencyNumbers.size() != 0;
    }

    @Override
    public String toString() {
        return node.getString();
    }


    void setNode(Node node) {
        this.node = node;
        this.node.getTableBuilder().copySetting(argument.getTableBuilder());
    }

    void setInferenceRule(InferenceRule  inferenceRule) {
        this.inferenceRule = inferenceRule;
    }

    void setDependencyNumbers(List<Integer> dependencyNumbers) {
        this.dependencyNumbers = dependencyNumbers;
    }
}
