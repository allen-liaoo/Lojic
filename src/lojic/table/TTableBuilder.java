package lojic.table;

import lojic.DefaultFactory;
import lojic.nodes.Node;
import lojic.nodes.connectives.Connective;
import lojic.nodes.truthapts.Atom;
import lojic.nodes.truthapts.Formula;
import lojic.nodes.truthapts.LocalAtom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the builder for a truth table
 *
 * This allows for truth tables of any Node in the same structure to have unique settings
 */
public class TTableBuilder {

    public static final int COLUMN_ALL_LEVELS = -1;

    private Node node;
    private final List<Column> columns; // Internally stored columns
    private int rowSize;

    private String[] trueAtoms;
    private String[] falseAtoms;
    private int subColumnsLevel;
    //private List<Node> showNodeColumns;

    /**
     * Constructor of an empty TTableBuilder used to copy settings in from
     * See methods such as {@link lojic.argument.Argument#setTableBuilder(TTableBuilder)}
     * or {@link Node#setUniversalBuilder(TTableBuilder)} for instances that an empty TTableBuilder
     * is useful
     */
    public TTableBuilder() {
        this(null);
    }

    /**
     * Constructor for a TTableBuilder
     *
     * @param node The node which this builder is building the table from
     */
    public TTableBuilder(Node node) {
        if (node == null) {
            this.node = null;
            this.columns = null;
            this.rowSize = -1;
            return;
        }

        this.node = node;
        this.columns = new ArrayList<>();
        this.subColumnsLevel = 0;
        useDefaultTFAtoms();
    }

    /**
     * Copy the setting of another table builder and override the setting of this builder
     *
     * @param builder The other builder
     * @return This table builder for method chaining
     */
    public TTableBuilder copySetting(TTableBuilder builder) {
        this.subColumnsLevel = builder.getSubColumnsLevel();
        this.trueAtoms = builder.getTrueAtoms();
        this.falseAtoms = builder.getFalseAtoms();
        return this;
    }

    /**
     * Set the levels of sub-columns that a table should show
     *
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
     * with a parameter {@code int level = 0} or by invoking {@link #disableSubColumns()}.
     *
     * To configure the table to show all sub-columns, pass the variable {@link TTableBuilder#COLUMN_ALL_LEVELS}
     * as the parameter. The variable has the same value as {@code -1}.
     *
     * @see TruthTable#print() documentation for the difference in presentation of a truth table with and without
     * sub-columns.
     *
     * @param level the integer levels of sub-columns
     * @return This table builder for method chaining
     * @throws IllegalArgumentException if the integer level < -1
     */
    public TTableBuilder setSubColumnsLevel(int level) {
        if(level < -1) throw new IllegalArgumentException("Sub-columns level must be greater or equal to -1!");
        this.subColumnsLevel = level;
        return this;
    }

    /**
     * Disable showing sub-columns
     * This is equivalent with invoking the method {@link #setSubColumnsLevel(int)} with {@code 0} as the argument
     *
     * @return This table builder for method chaining
     */
    public TTableBuilder disableSubColumns() {
        return setSubColumnsLevel(0);
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
     * @return This table builder for method chaining
     */
    public TTableBuilder useDefaultTFAtoms() {
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
     * @return This table builder for method chaining
     */
    public TTableBuilder disableTFAtoms() {
        setTrueAtoms();
        setFalseAtoms();
        return this;
    }

    /**
     * Set the string of atoms that the parser will recognize as "True Atoms"
     * False atoms are atoms that always has a truth value of {@code true}
     * Pass a {@code null} or {@code empty} argument in the parameter to disable recognition of True Atoms
     *
     * @param trueAtoms The string of true atoms
     * @return This table builder for method chaining
     */
    public TTableBuilder setTrueAtoms(String... trueAtoms) {
        this.trueAtoms = trueAtoms;
        return this;
    }

    /**
     * Set the string of atoms that the parser will recognize as "False Atoms"
     * False atoms are atoms that always has a truth value of {@code false}
     * Pass a {@code null} or {@code empty} argument in the parameter to disable recognition of False Atoms
     *
     * @param falseAtoms The false atoms
     * @return This table builder for method chaining
     */
    public TTableBuilder setFalseAtoms(String... falseAtoms) {
        this.falseAtoms = falseAtoms;
        return this;
    }

    /**
     * Build the truth table with the specified settings
     *
     * @return The truth table
     * @throws UnsupportedOperationException if this builder is an empty builder used to copy
     * TTableBuilder settings (such that the object is constructed with no parameter arguments
     * @see #TTableBuilder() for constructing an empty builder
     */
    public TruthTable build() {
        if (node == null) throw new UnsupportedOperationException("Cannot build a TruthTable from an empty TTableBuilder!");

        List<Column> tabl = buildTable();
        return new TruthTable(node, tabl, subColumnsLevel);
    }

    /**
     * Get the recognized symbols of true atoms
     * @see #setTrueAtoms(String...)
     *
     * @return an array of string symbols of true atoms
     */
    public String[] getTrueAtoms() {
        return trueAtoms;
    }

    /**
     * Get the recognized symbols of false atoms
     * @see #setFalseAtoms(String...)
     *
     * @return an array of string symbols of false atoms
     */
    public String[] getFalseAtoms() {
        return falseAtoms;
    }

    /**
     * Get the sub-column level
     *
     * @return The integer sub-column level
     */
    public int getSubColumnsLevel() {
        return subColumnsLevel;
    }

    /**
     * Check if a string would be interpreted as a "True Atom"
     * @see #setTrueAtoms(String...)
     *
     * @param atom The string atom
     * @return true if the string would be interpreted as a true atom
     */
    public boolean isTAtom(String atom) {
        for (String ta : trueAtoms) {
            if (ta.equals(atom)) return true;
        }
        return false;
    }

    /**
     * Check if a string would be interpreted as a "False Atom"
     * @see #setFalseAtoms(String...)
     *
     * @param atom The string atom
     * @return true if the string would be interpreted as a false atom
     */
    public boolean isFAtom(String atom) {
        for (String fa : falseAtoms) {
            if (fa.equals(atom)) return true;
        }
        return false;
    }

    /*
    1. Compute atom truths
    2. Compute all formula truths, including sub-formulas. store to a list (List<Column>)
    3. Select relevant columns from the list, restructure to include sub-columns (which are already made)
    */
    public List<Column> buildTable() {
        // init rowsize
        Atom[] atoms = node.getAtoms();
        int noneTFAs = atoms.length;
        for (Atom atom : atoms) {
            if (isTAtom(atom.toString()) || isFAtom(atom.toString())) {
                noneTFAs--;
            }
        }
        this.rowSize = noneTFAs == 0 ? 1 : (int) Math.pow(2, noneTFAs);

        computeAtomTruths(atoms);

        Column root = columns.stream()
                .filter(c -> c.getName().equals(node.getString()))
                .findAny().orElse(null); // non-null

        // Add atom columns
        List<Column> columnsT = new ArrayList<>(columns);

        if (root == null) {

            // build all formula columns
            computeFormulaTruths();

            root = columns.get(columns.size()-1);

            // handle sub-columns
            if (subColumnsLevel != 0) {
                buildSubColumns(root, 0);
            }

            columnsT.add(root);

        } else {
            columnsT.add(new Column(((LocalAtom) node).getAtom(), root.getValues()));
        }
        return columnsT;
    }

    /* Build Columns */

    private void buildSubColumns(Column formula, int lvlCount) {
        //if (subColumnsLevel != -1 && lvlCount > subColumnsLevel) return;

        Node leftN = formula.getFormula().getChildren()[0];

        Column left = findSubColumn(leftN, lvlCount+1);

        if (formula.getFormula().getConnective().isUnary()){
            formula.setSubColumnRight(left); // right is left :)
        } else {
            Node rightN = formula.getFormula().getChildren()[1];
            Column right = findSubColumn(rightN, lvlCount+1);

            formula.setSubColumnLeft(left);
            formula.setSubColumnRight(right);
        }
    }

    private Column findSubColumn(Node node, int lvlCount) {
        if (subColumnsLevel == -1 || lvlCount <= subColumnsLevel) {
            Column column = getColumn(node);
            if (!column.isAtom()) {
                buildSubColumns(column, lvlCount);
            }
            return column;
        }
        return null;
    }

    private Column getColumn(Node node) {
        if (node instanceof LocalAtom) {
            return columns.stream()
                    .filter(Column::isAtom)
                    .filter(c -> c.getName().equals(node.getString()))
                    .findAny().get(); // non-null
        } else {
            return columns.stream()
                    .filter(c -> !c.isAtom())
                    .filter(c -> c.getFormula().getLevel() == node.getLevel())
                    .filter(c -> c.getName().equals(node.getString()))
                    .findAny().get(); // non-null
        }
    }

    /*
        Construct column objects and fill in truth values for all formulas and sub-formulas.
        This should be invoked after atom truths are computed
     */
    private void computeFormulaTruths() {
        int levels = node.getLevels();

        for (int i = levels; i >= 0; i--) {
            List<Node> nodes = node.getNodes(i);
            for (Node n : nodes) {
                if (!(n instanceof Formula)) continue;

                Formula formula = (Formula) n;
                boolean[] truths = new boolean[rowSize];
                Connective connective = formula.getConnective();

                for (int j = 0; j < truths.length; j++) {
                    Node child1 = formula.getChildren()[0];
                    boolean left = getColumn(child1).getValues()[j];

                    if (connective.isUnary()) {
                        truths[j] = connective.computeTruth(left);

                    } else if (connective.isBinary()) {
                        Node child2 = formula.getChildren()[1];
                        boolean right = getColumn(child2).getValues()[j];
                        truths[j] = connective.computeTruth(left, right);
                    }
                }
                columns.add(new Column(formula, truths));
                // DEBUG: System.out.println(formula.getString() + " " + Arrays.toString(truths));
            }
        }
    }

    /*
        Algorithm:
            1. For N atoms, there are 2 ^ N rows.
            2. For each atom n (1 <= n <= N), true values should fill (2 ^ N) / (2 ^ n) slots, then false values
               The process (t->f->t...) repeats (2 ^ n times) until the whole column is filled.
     */
    private void computeAtomTruths(Atom[] atoms) {
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
            columns.add(new Column(atom, truths));
        }
    }

}
