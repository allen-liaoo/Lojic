package lojic.parser;

import lojic.nodes.ConnectiveFactory;
import lojic.nodes.truthapts.Formula;
import lojic.parser.token.Token;
import lojic.parser.token.TokenList;
import lojic.tree.FormulaTree;

import java.util.Arrays;
import java.util.List;

/**
 * @author AlienIdeology
 *
 * A parser of tokens, lexed from LojicLexer
 * This generates a FormulaTree, which is a tree of Node objects.
 */
public class LojicParser {

    private String cache;

    public LojicParser() {
        cache = "";
    }

    public FormulaTree parse(String formula) {
        if (formula == null || formula.isEmpty()) throw new IllegalArgumentException("Parser does not accept empty or null string");

        cache = LojicUtil.strip(formula);
        parse(cache, 0);

        cache = "";
        return new FormulaTree(null, 0);
    }

    public FormulaTree parse() {
        return parse(cache);
    }

    public LojicParser append(String formula) {
        cache += formula;
        return this;
    }

    // Recursive method used to parse any individual formula to nodes
    private void parse(String formula, int location) {
        Formula baseFormula = new Formula(0, formula, null);
        LojicLexer tokenizer = new LojicLexer(cache, formula);
        TokenList tokens = tokenizer.lex(location);
        System.out.println(tokens);
        System.out.println(Arrays.toString(tokens.stream().map(Token::getLocation).toArray()));
        System.out.println(Arrays.toString(tokens.stream().map(Token::getType).toArray()));
        //List<Token> token = expr(tokens, 10);
    }

    // Precedence climbing, recursive
    private List<Token> expr(TokenList list, int prevPrec) {
        String left = list.next().toString();
        while (ConnectiveFactory.getPrecedence(list.peek().toString()) <= prevPrec) {
            // Unfinished
            left += list.next().toString();
        }
        System.out.println(left);
        return null;
    }

}
