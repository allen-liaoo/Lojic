import lojic.DefaultFactory;
import lojic.nodes.Node;
import lojic.nodes.NodeClimber;
import lojic.nodes.connectives.UnaryConnective;
import lojic.parser.LojicParser;
import lojic.table.Column;
import lojic.table.TTableBuilder;
import lojic.table.TruthTable;

import java.util.List;

/**
 * @author AlienIdeology
 *
 * This is a showcase of {@link LojicParser}, {@link Node}, {@link TTableBuilder} and {@link TruthTable}
 */
public class TruthTableShowcase {

    public static void main(String[] args) {

        // First, a LojicParser is used to parse the logical expression
        LojicParser parser = new LojicParser();

        // Before parsing a logical expression, one can customize the parser's settings
        // Like so:

        parser.setAssociativity(10, false); // this sets the connectives with a precedence
                                                                       // of 10 to be left associative

        // This overrides the default negation connective with a new official symbol
        parser.replaceConnective(
                "¬",    // The original official symbol is "¬"
                                  // See DefaultFactory.NEG
                new UnaryConnective(
                        (right) -> !right,
                        "!",
                        50,
                        "¬", "~"
                    )
        );

        Node formula = parser.removeConnectives(DefaultFactory.XOR)
                // This removes the connective XOR (⊕). Parser will no longer recognize that connective
                .parse("(P->(Q->P))");// Method chaining!

        String treeResult = formula.print();
        System.out.println(treeResult);
        /*
        treeResult =

        ROOT_FORMULA(0): (P→(Q→P)) (CONNECTIVE): →)
            ATOM(1): P
            FORMULA(1): (Q→P) (CONNECTIVE): →)
                ATOM(2): Q
                ATOM(2): P

         */

        // Get a TreeClimber object for a NodeTree, used to iterate over NodeTree with lambda expressions
        NodeClimber climber = formula.climb();
        List<Node> filteredNodes =  climber.filter(Node::isFormula) // Get a list of formula nodes from a node tree
                .toList();

        // Get the TTableBuilder to fill in a TruthTable
        TTableBuilder builder = formula.getTableBuilder();

        // Method chaining to configure the table's settings
        builder.setSubColumnsLevel(TTableBuilder.COLUMN_ALL_LEVELS)  // shows all sub-columns, no matter the levels
                .disableTFAtoms();  // Disable the recognition of True/False atoms

        // Create a truth table
        // compute() can be called multiple times with different calculator settings to change the table output
        TruthTable table = builder.build();

        String tableResult = table.print();
        System.out.println(tableResult);
        /*
        tableResult =

        +---+---+-------+-----------+
        | P | Q | (Q→P) | (P→(Q→P)) |
        +---+---+-------+-----------+
        | T | T |  TTT  |  TT TTT   |
        +---+---+-------+-----------+
        | T | F |  FTT  |  TT FTT   |
        +---+---+-------+-----------+
        | F | T |  TFF  |  FT TFF   |
        +---+---+-------+-----------+
        | F | F |  FTF  |  FT FTF   |
        +---+---+-------+-----------+

         */

        // You can also...
        List<Column> fullTable = table.getColumns(); // Get a list of columns
        Column pAtom = table.getColumn(0);  // Get the first column "P"
        boolean[] row = table.getRow(0);  // Get the first truth values row ()
        boolean cell = table.getCell(0, 1);  // Get the cell with index (0, 1) -
        boolean tautology = table.rootIsTautology();  // tautology = true, since "(P->(Q->P)" is always true

    }

}
