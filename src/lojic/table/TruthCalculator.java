package lojic.table;

import lojic.DefaultFactory;
import lojic.nodes.Node;
import lojic.nodes.connectives.Connective;
import lojic.nodes.truthapts.Atom;
import lojic.nodes.truthapts.Formula;
import lojic.nodes.truthapts.LocalAtom;
import lojic.nodes.truthapts.TruthApt;
import lojic.tree.NodeTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static lojic.table.ColumnType.*;

/**
 * @author AlienIdeology
 *
 * A calculator for computing truth values of {@link TruthApt} formulas or atoms on a {@link NodeTree}
 * The method {@link #compute()} generates a {@link TruthTable} base on the filled truth values
 */
public class TruthCalculator {

    private final NodeTree nodeTree;
    private final int rowSize;

    private List<ColumnType> columnTypes;

    private String[] trueAtoms;
    private String[] falseAtoms;

    private int subColumnsLevel; // the level (how deep) of sub-columns that one want this calculator to construct

    private boolean computedAtoms;
    private boolean computedFormulas; // When formulas are computed, the root is also computed

    /**
     * The constructor for a TruthCalculator
     *
     * @param nodeTree The {@link NodeTree} which truth values are to be derived from
     * @throws NullPointerException If the node tree is null
     */
    public TruthCalculator(NodeTree nodeTree) {
        Objects.requireNonNull(nodeTree, "Cannot construct a TruthCalculator with a null NodeTree!");
        this.nodeTree = nodeTree;

        showColumnsDefault();
        tfAtomsDefault();

        // init rowsize
        Atom[] atoms = nodeTree.getAtoms();
        int noneTFAs = atoms.length;
        for (Atom atom : atoms) {
            if (isTAtom(atom.toString()) || isFAtom(atom.toString())) {
                noneTFAs--;
            }
        }
        this.rowSize = noneTFAs == 0 ? 1 : (int) Math.pow(2, noneTFAs);

        this.subColumnsLevel = 0;
        this.computedAtoms = false;
        this.computedFormulas = false;
    }

    /**
     * Set the {@link ColumnType} of the truth table, which determines the output of the table
     * and the way truth values are to be computed
     *
     * @param columnTypes The enum column types to be included in this table
     * @return This truth calculator for method chaining
     */
    public TruthCalculator showColumns(ColumnType... columnTypes) {
        this.columnTypes = Arrays.asList(columnTypes);
        return this;
    }

    /**
     * Use the default column types, which consists of
     * {@link ColumnType#ATOMS} and {@link ColumnType#ROOT}
     *
     * @return This truth calculator for method chaining
     */
    public TruthCalculator showColumnsDefault() {
        return showColumns(ATOMS, ROOT);
    }

    /**
     * Sub-columns are columns of atoms and/or non-root formulas
     * which belongs within a column of a formula
     *
     * This method sets the level of sub-columns in which one wants the {@link TruthTable} to show
     * How level is calculated:
     * Level 0 is the main column itself.
     * Level 1 denotes the sub-columns on the main column's right and/or left.
     * Level 2 denotes the sub-columns of the level 1's sub-column(s).
     * And so on...
     *
     * Keep in mind that when a formula is present as a column and as a sub-column,
     * the integer levels of the formula's column and sub-column(s) are not necessarily the same,
     * since the levels of sub-columns are relative to their main column.
     *
     * To disable showing sub-columns (which is the default setting), invoking this method
     * with a parameter {@code int level = 0}.
     *
     * @param level the level
     * @return This truth calculator for method chaining
     * @throws IllegalArgumentException if the integer level < 0.
     */
    public TruthCalculator showSubColumns(int level) {
        if(level < 0) throw new IllegalArgumentException("Sub-columns level must be greater or equal to 0");
        this.subColumnsLevel = level;
        return this;
    }

    /**
     * This shows all sub-columns of any formula or root formula
     * This is identical as invoking the method {@link #showSubColumns(int)} while
     * passing {@link Integer#MAX_VALUE} as the parameter
     *
     * @see #showSubColumns(int) for more information on sub-columns
     *
     * @return This truth calculator for method chaining
     */
    public TruthCalculator showAllSubColumns() {
        return showSubColumns(Integer.MAX_VALUE);
    }

    /**
     * This is the default setting, which shows no sub-columns
     * @see #showSubColumns(int) for more information on sub-columns
     *
     * @return This truth calculator for method chaining
     */
    public TruthCalculator disableSubColumns() {
        return showSubColumns(0);
    }

    /**
     * Use the default recognized strings of True and False Atoms
     *
     * True/False Atoms are atoms with only a true or false value
     * True Atoms: "T", "⊤", "1"
     * False Atoms: "F", "⊥", "0"
     * For example, any atom with the string "T" will only has the boolean value "true" on the truth table
     * @see DefaultFactory#TRUE_ATOMS for default true atoms
     * @see DefaultFactory#FALSE_ATOMS for default false atoms
     *
     * @return This truth calculator for method chaining
     */
    public TruthCalculator tfAtomsDefault() {
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
    public TruthCalculator tfAtomsDisable() {
        setTrueAtoms();
        setFalseAtoms();
        return this;
    }

    /**
     * Set the True Atoms that this calculator will recognize
     * Pass a {@code null} or {@code empty} argument in the parameter to disable recognition of True Atoms
     * @see #tfAtomsDefault()
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
     * @see #tfAtomsDefault()
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

        if (columnTypes == null)
            showColumnsDefault();

        List<Column> exports = new ArrayList<>();
        boolean showsSubColumns = subColumnsLevel > 0;

        if (columnTypes.contains(ATOMS)) {
            for (Atom atom : nodeTree.getAtoms()) {
                exports.add(new Column(ATOMS, atom, atom.getTruths()));
            }
        }

        if (columnTypes.contains(FORMULAS)) {
            nodeTree.climb().forEach(node -> {
                if (node instanceof Formula
                        && !((Formula) node).isRoot()) {

                    Formula formula = (Formula) node;
                    if (!showsSubColumns) {
                        exports.add(new Column(FORMULAS, formula, formula.getTruths()));
                        return;
                    }

                    exports.add(buildSubColumns(formula, 0));
                }
            });
        }

        root:
        if (columnTypes.contains(ROOT)) {
            Node root = nodeTree.getRoot();
            TruthApt ta = root.getTruthApt();

            if (!root.isFormula()) {
                exports.add(new Column(ROOT, ((LocalAtom) root).getAtom(), ta.getTruths()));
                break root;
            }

            // handle root formula
            if (!showsSubColumns) {

                exports.add(new Column(ROOT, (Formula) root, ta.getTruths()));

            } else {

                Column temp = buildSubColumns((Formula) root, 0);
                exports.add(new Column(ROOT, temp.getFormula(), temp.getValues(), temp.getSubColumnLeft(), temp.getSubColumnRight()));

            }
        }

        return new TruthTable(nodeTree, exports, subColumnsLevel);
    }

    private Column buildSubColumns(Formula formula, int lvlCount) {
        //if (lvlCount > subColumnsLevel) return null;
        Node leftN = formula.getChildren()[0];

        Column left = subcol(leftN, lvlCount+1);

        if (formula.getConnective().isUnary()){
            return new Column(FORMULAS, formula,formula.getTruths(), null, left);
        } else {
            Node rightN = formula.getChildren()[1];
            Column right = subcol(rightN, lvlCount+1);
            return new Column(FORMULAS, formula,formula.getTruths(), left, right);
        }
    }

    private Column subcol(Node node, int lvlCount) {
        if (lvlCount > subColumnsLevel) return null;
        Column column;
        if (node instanceof LocalAtom)
            column = new Column(ATOMS, ((LocalAtom) node).getAtom(), node.getTruthApt().getTruths());
        else {
            column = buildSubColumns((Formula) node, lvlCount);
        }
        return column;
    }

    private void computeFormulaTruths() {
        int levels = nodeTree.getLevels();

        for (int i = levels; i >= 0; i--) {
            Node[] nodes = nodeTree.getNodes(i);
            for (Node node : nodes) {
                if (!(node instanceof Formula)) continue;

                Formula formula = (Formula) node;
                if (!formula.isSet()) {
                    boolean[] truths = new boolean[rowSize];
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
        for (Atom atom : atoms) {
            boolean[] truths = new boolean[rowSize];

            if (isTAtom(atom.toString())) {

                Arrays.fill(truths, true);

            } else if (isFAtom(atom.toString())) {

                Arrays.fill(truths, false);

            } else {
                count++;
                boolean value = true;
                int times = (int) Math.pow(2, count); // the amount of times the process of filling should be repeated
                int slots = rowSize / times; // the amount of slots one should fill true/false
                // DEBUG: System.out.println("RowSize: " + rowsize + " Times: " + times + " Slots: " + slots);

                int index = 0; // the starting index which t/f value changes
                for (int j = 0; j < times; j++) {
                    Arrays.fill(truths, index, (index + slots), value);
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
