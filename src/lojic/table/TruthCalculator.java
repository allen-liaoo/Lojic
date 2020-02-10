package lojic.table;

import lojic.DefaultFactory;
import lojic.nodes.Node;
import lojic.nodes.connectives.Connective;
import lojic.nodes.truthapts.Atom;
import lojic.nodes.truthapts.Formula;
import lojic.nodes.truthapts.TruthApt;
import lojic.tree.NodeTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static lojic.table.DetailSetting.*;

/**
 * @author AlienIdeology
 *
 * A calculator for computing truth values of {@link TruthApt} formulas or atoms on a {@link NodeTree}
 */
public class TruthCalculator {

    private final NodeTree nodeTree;
    private int rowsize;

    private List<DetailSetting> detailSettings;

    private String[] trueAtoms;
    private String[] falseAtoms;

    private boolean computedAtoms;
    private boolean computedFormulas; // When formulas are computed, the root is also computed

    /**
     * The constructor for a TruthCalculator
     *
     * @param nodeTree The nodetree which truth values are to be derived from
     */
    public TruthCalculator(NodeTree nodeTree) {
        this.nodeTree = nodeTree;
        init();
    }

    // initialize variables
    private void init() {
        detailDefault();
        TFAtomsDefault();

        // init rowsize
        Atom[] atoms = nodeTree.getAtoms();
        int noneTFAs = atoms.length;
        for (Atom atom : atoms) {
            if (isTAtom(atom.toString()) || isFAtom(atom.toString())) {
                noneTFAs--;
            }
        }
        rowsize = noneTFAs == 0 ? 1 : (int) Math.pow(2, noneTFAs);

        computedAtoms = false;
        computedFormulas = false;
    }

    /**
     * Set the {@link DetailSetting} of the truth table, which determines the output of the table
     * and the way truth values are to be computed
     *
     * @param detailSettings The enum detail settings
     * @return This truth calculator for method chaining
     */
    public TruthCalculator detailSetting(DetailSetting... detailSettings) {
        this.detailSettings = Arrays.asList(detailSettings);
        return this;
    }

    /**
     * Use the default detail settings, which consists of
     * {@link DetailSetting#ATOMS} and {@link DetailSetting#ROOT}
     *
     * @return This truth calculator for method chaining
     */
    public TruthCalculator detailDefault() {
        return detailSetting(ATOMS, ROOT);
    }

    /**
     * Use the default recognized strings of True and False Atoms
     *
     * True/False Atoms are atoms with only a true or false value
     * True Atoms: "T", "⊤", "1"
     * False Atoms: "F", "⊥", "0"
     * For example, any atom with the string "T" will only has the boolean value "true" on the truth table
     *
     * @return This truth calculator for method chaining
     */
    public TruthCalculator TFAtomsDefault() {
        trueAtoms = DefaultFactory.TRUE_ATOMS;
        falseAtoms = DefaultFactory.FALSE_ATOMS;
        return this;
    }

    /**
     * Disable the function of recognizing True/False Atoms
     * This is identical to {@code #setTrueAtoms(null); #setFalseAtoms(null)}
     * @see #setTrueAtoms(String...)
     * @see #setFalseAtoms(String...)
     *
     * @return This truth calculator for method chaining
     */
    public TruthCalculator TFAtomsDisable() {
        setTrueAtoms((String) null);
        setFalseAtoms((String) null);
        return this;
    }

    /**
     * Set the True Atoms that this calculator will recognize
     * Pass a {@code null} or {@code empty} argument in the parameter to disable recognition of True Atoms
     *
     * @param trueAtoms The string of true atoms
     * @return This truth calculator for method chaining
     */
    public TruthCalculator setTrueAtoms(String... trueAtoms) {
        this.trueAtoms = (trueAtoms == null)
                ? new String[]{} : trueAtoms;
        return this;
    }

    /**
     * Set the False Atoms that this calculator will recognize
     * Pass a {@code null} or {@code empty} argument in the parameter to disable recognition of False Atoms
     *
     * @param falseAtoms The false atoms
     * @return This truth calculator for method chaining
     */
    public TruthCalculator setFalseAtoms(String... falseAtoms) {
        this.falseAtoms = (falseAtoms == null)
                ? new String[]{} : falseAtoms;
        return this;
    }

    /**
     * Compute the truth values of the {@link TruthTable}
     *
     * @return The truth table
     */
    public TruthTable compute() {
        if (!computedAtoms)
            fillAtomTruths();
        if (!computedFormulas)
            computeFormulaTruths();

        if (detailSettings == null)
            detailDefault();

        List<Column> columns = new ArrayList<>();
        if (detailSettings.contains(ATOMS)) {
            for (Atom atom : nodeTree.getAtoms()) {
                columns.add(new Column(ATOMS, null, atom, atom.getTruths()));
            }
        }

        if (detailSettings.contains(FORMULAS)) {
            nodeTree.climb().forEach(node -> {
                if (node instanceof Formula
                        && !((Formula) node).isRoot()) {
                    Formula formula = (Formula) node;
                    if (!detailSettings.contains(SUB_COLUMNS)) {
                        columns.add(new Column(FORMULAS, formula, formula, formula.getTruths()));
                        return;
                    }

                    TruthApt child1 = formula.getChildren()[0].getTruthApt();
                    if (formula.getConnective().isBinary()) {
                        TruthApt child2 = formula.getChildren()[1].getTruthApt();
                        columns.add(new Column(FORMULAS, formula, formula, formula.getTruths(), child1.getTruths(), child2.getTruths()));
                    } else if (formula.getConnective().isUnary()) {
                        columns.add(new Column(FORMULAS, formula, formula, formula.getTruths(), null, child1.getTruths()));
                    }
                }
            });
        }

        if (detailSettings.contains(ROOT)) {
            Node root = nodeTree.getRoot();
            TruthApt ta = root.getTruthApt();
            columns.add(new Column(ROOT, root, ta, ta.getTruths()));
        }

        return new TruthTable(nodeTree, columns);
    }

    private void computeFormulaTruths() {
        int levels = nodeTree.getLevels();

        for (int i = levels; i >= 0; i--) {
            Node[] nodes = nodeTree.getNodes(i);
            for (Node node : nodes) {
                if (!(node instanceof Formula)) continue;

                Formula formula = (Formula) node;
                if (!formula.isSet()) {
                    boolean[] truths = new boolean[rowsize];
                    Connective connective = formula.getConnective();

                    for (int j = 0; j < truths.length; j++) {
                        Node child1 = formula.getChildren()[0];
                        TruthApt ta1 = child1.getTruthApt();
                        boolean left = ta1.getTruths()[j];

                        if (connective.isUnary()) {
                            truths[j] = connective.computeTruth(left);

                        } else if (connective.isBinary()) {
                            Node child2 = formula.getChildren()[1];
                            TruthApt ta2 = child2.getTruthApt();
                            boolean right = ta2.getTruths()[j];
                            truths[j] = connective.computeTruth(left, right);
                        }
                    }
                    formula.setTruths(truths);
                    // DEBUG: System.out.println(formula.getString() + " " + Arrays.toString(truths));
                }
            }
        }
        computedFormulas = true;
    }

    /*
        Algorithm:
            1. For N atoms, there are 2 ^ N rows.
            2. For each atom n (1 <= n <= N), true values should fill (2 ^ N) / (2 ^ n) slots, then false values
               The process (t->f->t...) repeats (2 ^ n times) until the whole column is filled.
     */
    private void fillAtomTruths() {
        Atom[] atoms = nodeTree.getAtoms();

        int count = 0;
        for (int i = 0; i < atoms.length; i++) {
            Atom atom = atoms[i];
            boolean[] truths = new boolean[rowsize];

            if (isTAtom(atom.toString())) {
                Arrays.fill(truths,true);
            } else if (isFAtom(atom.toString())) {
                Arrays.fill(truths,false);
            } else {
                count++;
                boolean value = true;
                int times = (int) Math.pow(2, count); // the amount of times the process of filling should be repeated
                int slots = rowsize / times; // the amount of slots one should fill true/false
                // DEBUG: System.out.println("RowSize: " + rowsize + " Times: " + times + " Slots: " + slots);

                int index = 0; // the starting index which t/f value changes
                for (int j = 0; j < times; j++) {
                    Arrays.fill(truths,index, (index + slots), value);
                    value = !value;
                    index += slots;
                }
            }
            atom.setTruths(truths);
        }
        this.computedAtoms = true;
    }

    private boolean isTAtom(String atom) {
        for (String ta : trueAtoms) {
            if (ta.equals(atom)) return true;
        }
        return false;
    }

    private boolean isFAtom(String atom) {
        for (String fa : falseAtoms) {
            if (fa.equals(atom)) return true;
        }
        return false;
    }

}
