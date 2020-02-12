package lojic.tree;

import lojic.nodes.Node;
import lojic.nodes.truthapts.Atom;
import lojic.nodes.truthapts.Formula;
import lojic.nodes.truthapts.LocalAtom;
import lojic.table.TruthCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AlienIdeology
 *
 * The abstract syntax tree of a logical expression,
 * parsed by {@link lojic.parser.LojicParser}.
 */
public class NodeTree {

    /**
     * Make a copy of a node tree, utilizing methods like {@link Node#copy()} and {@link Atom#copy()}
     *
     * @param tree The original node tree to be copied from
     * @return The new node tree
     */
    public static NodeTree copyOf(NodeTree tree) {
        List<Atom> atoms = new ArrayList<>();
        for (Atom a : tree.getAtoms()) {
            atoms.add(a.copy());
        }

        return new NodeTree(tree.getRoot().copy(), atoms.toArray(new Atom[0]));
    }

    private final Node root;
    private int levels;
    private final Atom[] atoms;
    private final TreeClimber climber;

    private static final String ROOT = "ROOT";
    private static final String FORMULA = "FORMULA";
    private static final String CONNECTIVE = "CONNECTIVE";
    private static final String ATOM = "ATOM";

    /**
     * Constructor of a NodeTree
     * This constructor is for the Lojic library's internal use only, users should ignore this
     * @see lojic.parser.LojicParser#parse for creating instances of nodetrees
     *
     * @param root The root node
     * @param atoms The unique atoms in the logical expression
     */
    public NodeTree(Node root, Atom[] atoms) {
        this.root = root;
        this.atoms = atoms;
        this.climber = new TreeClimber(root);

        this.levels = 0;
        climber.forEach(node -> {
            if (node.getLevel() > levels)
                levels = node.getLevel();
        });
    }

    /**
     * Create an instance of {@link TruthCalculator} which
     * can be used to generate a {@link lojic.table.TruthTable}
     *
     * @return The truth calculator
     */
    public TruthCalculator createCalculator() {
        return new TruthCalculator(this);
    }

    /**
     * Get the root node
     * This returns either a {@link LocalAtom} or a {@link Formula}.
     *
     * @return The root node
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Get the number of levels in the NodeTree
     * The root of the tree is always level 0
     * The value increases by 1 for every sub-atom or formula
     *
     * @return The integer value of levels
     */
    public int getLevels() {
        return levels;
    }

    /**
     * Get the (unique, none-repeating) atoms in the NodeTree
     *
     * @return The atoms
     */
    public Atom[] getAtoms() {
        return atoms;
    }

    /**
     * Get all the Nodes on a level
     *
     * @param level The level
     * @return The nodes on the specified level
     */
    public Node[] getNodes(int level) {
        return climber.filter(n -> n.getLevel() == level).toList().toArray(new Node[0]);
    }

    /**
     * Get the TreeClimber, which is used to filter or iterate the tree
     * @see TreeClimber
     *
     * @return The TreeClimber
     */
    public TreeClimber climb() {
        return climber;
    }

    /**
     * Get the string representation of the tree
     * The string is in the following format:
     * {@code [Indent][Node_Type]([level]): [Node_String]}
     * When a node is the root node, its string in this special format:
     * {@code ROOT_[Node_Type](0): [Node_String]}
     * When a node is a formula node, there is a connective string appended to the original format:
     * {@code (CONNECTIVE: [Official_Symbol])}
     *
     * @return The string representation of the tree
     */
    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(ROOT).append("_");
        printNode(builder, root, 1);
        while (builder.toString().endsWith("\n")) {
            builder.delete(builder.length()-1, builder.length());
        }
        return builder.toString();
    }

    private void printNode(StringBuilder builder, Node node, int tabs) {
        if (node instanceof LocalAtom) {
            builder.append(ATOM)
                    .append('(')
                    .append(node.getLevel())
                    .append("): ")
                    .append(node.getString())
                    .append('\n');

        } else if (node instanceof Formula) {
            Formula formula = (Formula) node;
            builder.append(FORMULA)
                    .append('(')
                    .append(formula.getLevel())
                    .append("): ")
                    .append(formula.getString());
            if (formula.getConnective() != null) {
                builder.append(" (")
                        .append(CONNECTIVE)
                        .append(')').append(':').append(' ')
                        .append(formula.getConnective().getOfficialSymbol())
                        .append(')');
            }
            builder.append('\n');

            Node[] children = formula.getChildren();
            for (Node n : children) {
                for (int t = 0; t < tabs; t++) {
                    builder.append("  ");
                }

                if (n instanceof Formula) {
                    printNode(builder, n, tabs + 1);
                } else if (n instanceof LocalAtom) {
                    builder.append(ATOM)
                            .append('(')
                            .append(n.getLevel())
                            .append("): ")
                            .append(n.getString())
                            .append('\n');
                }
            }
        }
    }

}
