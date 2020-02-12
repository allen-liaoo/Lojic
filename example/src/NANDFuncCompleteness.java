import lojic.parser.LojicParser;
import lojic.table.TruthTable;

/**
 * @author AlienIdeology
 *
 * The logical operator NAND (↑) is logically equivilent to  ¬∨.
 *
 *      ¬ (P ∨ Q) ≡ (P ↑ Q)
 *
 * NAND (↑) is an operator that, by itself, is functionally complete--
 * which is to say that using just this operator, one can denote all possible truth tables.
 *
 * Here are some well known sets of functionally complete operators:
 * {¬, ∧} (Negation, And)
 * {¬, ∨) (Negation, Or
 *
 * To prove that NAND by itself is functionally complete, this class contains the code that
 * showcases truth tables of NAND expressions which are identical to other logical operators
 * (Including Negation, And, Or, and If)
 */
public class NANDFuncCompleteness {

    public static void main(String[] args) {

        LojicParser parser = new LojicParser();

        /*
        First,
        NEGATION

        ¬P ≡ (P ↑ P)
        */
        TruthTable negTTable = parser.parse("P↑P")
                .createCalculator()
                .compute();

        System.out.println(
                negTTable.print()
        );

        System.out.println(
                parser.parse("¬P")
                        .createCalculator()
                        .compute()
                        .print()
        );

        /*

        This is the result:

        +---+-------+
        | P | (P↑P) |
        +---+-------+
        | T |   F   |
        +---+-------+
        | F |   T   |
        +---+-------+

        Which is identical to the truth table of ¬P:


        +---+-------+
        | P | (¬P)  |
        +---+-------+
        | T |   F   |
        +---+-------+
        | F |   T   |
        +---+-------+

        <!-------------------------------->
        System.out.println(
                parser.parse("¬P")
                    .createCalculator()
                    .compute()
                .print()
        );
        <!-------------------------------->

         */

        /*
        Let's move on to
        AND

        (P & Q) ≡ (P↑Q) ↑ (P↑Q)

        P↑Q is essentially ¬(P&Q).
        When we combine two of P↑Q together, we are essentially
        negating P↑Q, resulting in ¬¬(P&Q), which is (P&Q)
         */

        TruthTable andTTable = parser.parse("(P↑Q) ↑ (P↑Q)")
                .createCalculator()
                .compute();

        System.out.println(
                andTTable.print()
        );

        /*

        This is the result:

        +---+---+---------------+
        | P | Q | ((P↑Q)↑(P↑Q)) |
        +---+---+---------------+
        | T | T |       T       |
        +---+---+---------------+
        | T | F |       F       |
        +---+---+---------------+
        | F | T |       F       |
        +---+---+---------------+
        | F | F |       F       |
        +---+---+---------------+

        Which is identical to the truth table of (P & Q):

        +---+---+-------+
        | P | Q | (P∧Q) |
        +---+---+-------+
        | T | T |   T   |
        +---+---+-------+
        | T | F |   F   |
        +---+---+-------+
        | F | T |   F   |
        +---+---+-------+
        | F | F |   F   |
        +---+---+-------+

         */

        /*
        Here comes the most complicated one:
        OR

        (P ∨ Q) ≡ (P↑P) ↑ (Q↑Q)

        Let's break it down:
        P↑P is ¬P
        Q↑Q is ¬Q       (remember negation?)

        Since
        ¬(P ∨ Q) is (¬P) ∧ (¬Q)
        and
        ¬¬(P ∨ Q) is ¬((¬P) ∧ (¬Q))
        ¬¬(P ∨ Q) would be:
        (P↑P) ↑ (Q↑Q)

         */


        TruthTable orTTable = parser.parse("(P↑P)↑(Q↑Q)")
                // Or, if you're bored, use
                // "(((P↑P)↑(Q↑Q))↑((P↑P)↑(Q↑Q)))↑(((P↑P)↑(Q↑Q))↑((P↑P)↑(Q↑Q)))"
                .createCalculator()
                .compute();

        System.out.println(
                orTTable.print()
        );

        /*

        This is the result:

        +---+---+---------------+
        | P | Q | ((P↑P)↑(Q↑Q)) |
        +---+---+---------------+
        | T | T |       T       |
        +---+---+---------------+
        | T | F |       T       |
        +---+---+---------------+
        | F | T |       T       |
        +---+---+---------------+
        | F | F |       F       |
        +---+---+---------------+

        Which is identical to the truth table of (P ∨ Q):

        +---+---+-------+
        | P | Q | (P∨Q) |
        +---+---+-------+
        | T | T |   T   |
        +---+---+-------+
        | T | F |   T   |
        +---+---+-------+
        | F | T |   T   |
        +---+---+-------+
        | F | F |   F   |
        +---+---+-------+

         */

        /*
        Almost there! Lastly,
        IF

        (P -> Q) ≡ ((P↑(Q↑Q))↑(P↑(Q↑Q))) ↑ ((P↑(Q↑Q))↑(P↑(Q↑Q)))

        (Oof... do we even want to know how it works?)
        So,
        ¬(P∧Q) ≡ (P -> Q)       (try it)

        P↑(Q↑Q) is (P ↑ ¬Q)
        (P ↑ ¬Q) ↑ (P ↑ ¬Q) would be the negation of (P ↑ ¬Q),
        which is (P & ¬Q)

        Repeat the process once again, and we get ¬(P∧Q).
        Tada!
         */

        TruthTable ifTTable = parser.parse("((P↑(Q↑Q))↑(P↑(Q↑Q)))↑((P↑(Q↑Q))↑(P↑(Q↑Q)))")
                .createCalculator()
                .compute();

        System.out.println(
                ifTTable.print()
        );

        /*

        The result is:

        +---+---+-----------------------------------------------+
        | P | Q | (((P↑(Q↑Q))↑(P↑(Q↑Q)))↑((P↑(Q↑Q))↑(P↑(Q↑Q)))) |
        +---+---+-----------------------------------------------+
        | T | T |                       T                       |
        +---+---+-----------------------------------------------+
        | T | F |                       F                       |
        +---+---+-----------------------------------------------+
        | F | T |                       T                       |
        +---+---+-----------------------------------------------+
        | F | F |                       T                       |
        +---+---+-----------------------------------------------+


        Which is identical to the truth table of (P -> Q):

        +---+---+-------+
        | P | Q | (P→Q) |
        +---+---+-------+
        | T | T |   T   |
        +---+---+-------+
        | T | F |   F   |
        +---+---+-------+
        | F | T |   T   |
        +---+---+-------+
        | F | F |   T   |
        +---+---+-------+

         */

        // Btw, NOR (↓) is also functionally complete by itself. Check it out!

    }
    
}
