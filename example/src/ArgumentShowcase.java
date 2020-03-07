import lojic.argument.Argument;
import lojic.parser.LojicParser;
import lojic.table.TTableBuilder;

/**
 * @author AlienIdeology
 *
 * This is a showcase of {@link lojic.argument.Argument}
 */
public class ArgumentShowcase {

    public static void main(String[] args) {

        // One can construct an argument in two ways
        // (1) by invoking methods like #addPremises(String) and #setConclusion(String), Or
        // (2) by providing a String sequent

        // (1)
        Argument argument = new Argument();
        argument.addPremises("P->Q", "P")
                .setConclusion("Q");

        // You can print the argument
        System.out.println(argument.print());
        /*
        The result:
        1. P→Q
        2. P
        3. Q
         */

        // Check the argument's (semantic) validity
        System.out.println(argument.isValid()); // returns true



        // (2)
        // By the way, you can configure the LojicParser and TruthCalculator
        // which are used in parsing each line and constructing truth tables, respectively
        Argument argument1 = new Argument(
                new LojicParser()
                // Manipulate parser settings
        );

        argument1.setTableBuilder(
                new TTableBuilder()
                // Manipulate calculator settings
        );

        // Parse a sequent
        argument1.fromSequent("P->Q, P |- Q");
        // premises are separated by ",", and a symbol of logical consequence (such as |-) separates
        // the conclusion from the premises

        // argument1 is now equivalent to argument

        argument1.getLine(2); // returns "P"
        System.out.println(argument1.isValid()); // returns true

    }

}
