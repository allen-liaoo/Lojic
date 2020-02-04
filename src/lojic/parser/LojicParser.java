package lojic.parser;

import lojic.nodes.ConnectiveFactory;
import lojic.parser.token.Token;
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
        //Formula baseFormula = new Formula(0, formula, null);
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
        LojicLexer tokenizer = new LojicLexer(cache, formula);
        ParseList<Token> tokens = tokenizer.lex(location);
        // DEBUG Print formulas
        System.out.println(tokens);
        System.out.println(Arrays.toString(tokens.stream().map(Token::getType).toArray()));
        System.out.println(Arrays.toString(tokens.stream().map(Token::getLocation).toArray()));
        System.out.println();
        // DEBUG tokens.forEach(tk -> System.out.println(tk + "\n" + LojicUtil.generateIndicator(cache, tk.getLocation())));

        //List<Token> token = expr(tokens, 10);
    }

    // DEBUG: Parse and print everything
    /*
    private void pe(ParseList<Token> tokens) {
        for (Token token : tokens) {
            if (token.isType(TokenType.FORMULA) && !token.isUnparsedFormula()) {
                parse(token.toString(), token.getLocation());
            } else if (token.isUnparsedFormula()){
                System.out.println(token.toString());
                System.out.println(Arrays.toString(((Token.ParsedFormula)token).getTokens().toArray()));
                System.out.println(Arrays.toString(((Token.ParsedFormula)token).getTokens().stream().map(Token::getType).toArray()));
                System.out.println(Arrays.toString(((Token.ParsedFormula)token).getTokens().stream().map(Token::getLocation).toArray()));
                System.out.println();
                pe(new ParseList<>(((Token.ParsedFormula) token).getTokens()));
            }
        }
    }*/

    // Precedence climbing, recursive
    private List<Token> expr(ParseList<Token> list, int prevPrec) {
        String left = list.next().toString();
        while (ConnectiveFactory.getPrecedence(list.peek().toString()) <= prevPrec) {
            // Unfinished
            left += list.next().toString();
        }
        System.out.println(left);
        return null;
    }

}
