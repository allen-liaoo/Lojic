package lojic.parser;

import lojic.nodes.ConnectiveFactory;
import lojic.parser.token.Token;
import lojic.parser.token.TokenList;
import lojic.parser.token.TokenUtil;
import lojic.tree.FormulaTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author AlienIdeology
 */
public class LojicParser {

    private String cache;

    public LojicParser() {
        cache = "";
    }

    public FormulaTree parse(String formula) {
        if (formula == null || formula.isEmpty()) throw new IllegalArgumentException("Parser does not accept empty or null string");

        String stripped = TokenUtil.strip(formula);
        cache = stripped;
        parse(stripped, 0);

        cache = "";
        return new FormulaTree();
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
        TokenList tokens = lex(formula, location);
        //List<Token> left = expr(tokens, 0);
    }

    // Precedence climbing, recursive
    private List<Token> expr(TokenList list, int precedence) {
        TokenList left = new TokenList();
        left.add(list.next());
        while (ConnectiveFactory.getPrecedence(list.peek().toString()) > precedence) {
            // Unfinished
        }
        return null;
    }

    /*
     * Group parenthesized formulas into a string
     * lex("P∧Q") => ["P", "∧", "Q"]
     * lex("((P∧Q))") => ["P", "∧", "Q"]
     * lex("(P∧Q)->R") => ["P∧Q", "->", "R"]
     *
     * Recursively called to parse any formula to tokens
     */
    private TokenList lex(String formula, int location) {
        StringReader reader = new StringReader(formula);
        TokenList tokens = new TokenList();

        while (true) {
            while (reader.hasNext()) {
                tokens.add(handleNext(reader, location + reader.index() + 1)); // reader index starts at -1, so offset it by 1
            }
            System.out.println(tokens);
            System.out.println(Arrays.toString(tokens.stream().map(Token::getLocation).toArray()));
            System.out.println(Arrays.toString(tokens.stream().map(Token::getType).toArray()));

            /*
             Deal with unnecessary parenthesis enclosing this atom or formula
             This only strips away ALL parenthesis if the formula is not compound
             A formula is compound when its sub-formula(s) or sub-atom(s) is parenthesized twice of more
             Example: "((A))->((B))"
                      Non Compound "(A)->(B)"
             parse() will later deal with sub-formula(s) or sub-atom(s) that is still parenthesized
             */

            // "((A))" => "(A)" => "A"
            // "(((A&B)))" => "A" "&" "B"
            // "((A)->(B)->(C&D))" => "A" "->" "B" -> "C&D"
            // "((A&B)->(B&C))" => "A&B"
            if (tokens.size()==1){
                reader.updateString(tokens.get(0).toString());
                // "A" => "A"
                // "(A)" => "A"
                if (tokens.get(0).isType(Token.Type.ATOM)) break;

                // "(((A&B)))" => ""A&B"
                else {
                    tokens.clear();
                    reader.setIndex(-1);
                }
            }
            else break;
        }

        return tokens;
    }

    // Grouping parenthesis and handling errors

    /*
     * Handle all syntax errors
     * This method group parenthesized strings into formulas or atoms, and un-parenthesize it.
     */
    private Token handleNext(StringReader reader, int location) {
        Token next = reader.next();
        Token.Type type = next.getType();

        System.out.println(next.toString() + " " + next.getType());

        // loc = location + reader index
        // errorNoNext requires -1; throw new Exception requires -1 or -2
        int loc = location + next.length();

        switch (type) {
            case PARENTHESIS_OPEN: {
                int count = 1;
                StringBuilder cache = new StringBuilder(next.toString());
                errorNext(reader, loc, Token.Type.BINARY_CONNECTIVE);

                while (count !=0) {
                    // Cases like "(" or "((" or "((P->Q)"
                    if (!reader.hasNext()) {
                        // "(", "(("
                        if(TokenUtil.isOpenParenthesis(cache.substring(cache.length()-1))) {
                            errorNoNext(reader, loc-1);
                        // "((P->Q)"
                        } else {
                            throw new SyntaxException(loc-1, "Missing closing parenthesis",
                                    generateIndicator(this.cache, loc-1));
                        }
                    }

                    next = reader.next();
                    loc += next.length();

                    if (next.isType(Token.Type.PARENTHESIS_OPEN)) {
                            count++;
                    } else if (next.isType(Token.Type.PARENTHESIS_CLOSE)) {
                        count--;
                    }
                    cache.append(next.toString());
                }

                String result = cache.toString();
                // Remove parenthesis in front and at the end
                result = result.substring(1, result.length()-1);

                // Empty Formula ()
                if(result.isEmpty()) throw new SyntaxException(loc-2, "Empty formula",
                        generateIndicator(this.cache, loc-2));

                // Return formula of atom
                return TokenUtil.isAtomic(result) ? new Token (reader, result, Token.Type.ATOM, location) :
                        new Token(reader, result, Token.Type.FORMULA, location);
            }

            case PARENTHESIS_CLOSE: throw new SyntaxException(loc-1, next,
                    generateIndicator(reader.toString(), loc-1));

            case ATOM: {
                errorNext(reader, loc, Token.Type.UNARY_CONNECTIVE, Token.Type.PARENTHESIS_OPEN);
                break;
            }
            case UNARY_CONNECTIVE:
            case BINARY_CONNECTIVE: {
                // Cases like "A&" or "!"
                errorNoNext(reader, loc-1);
                errorNext(reader, loc, Token.Type.BINARY_CONNECTIVE, Token.Type.PARENTHESIS_CLOSE);
                break;
            }
            case UNKNOWN:
                throw new SyntaxException(loc, "Unrecognized character " + next,
                        generateIndicator(cache, loc));
        }

        next.setLocation(location);
        return next;
    }

    // Check if the next token is of certain TokenType. If so, throw SyntaxException
    private void errorNext(StringReader reader, int location, Token.Type... types) {
        if (reader.hasNext()) {
            Token token = reader.peek();
            for (Token.Type tp : types) {
                if (token.isType(tp)) throw new SyntaxException(location,
                        (CharSequence) token.toString(),
                        generateIndicator(cache, location));
            }
        }
    }

    // Check if there is a next token. If not, throw SyntaxException
    private void errorNoNext(StringReader reader, int location) {
        if (!reader.hasNext()) throw new SyntaxException(location,
                "Missing atom or formula at the end of the expression",
                generateIndicator(cache, location));
    }

    // For printing detailed error messages which indicates the location the error occurs
    private String generateIndicator(String formula, int index) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < index; i++) {
            builder.append(" ");
        }
        return formula + "\n" + builder.toString() + "^";
    }

    // Old Comment
    // Unnecessary parenthesis enclosing an atom or formula
    // A. Thorough un-parenthesizing
    // B. Partial un-parenthesizing
    // C. Return Atom
    // D. Return Formula
    //
    // AC1. ATOM "((A))" => "(A)" -> "A"
    // AC2. ATOM "(((A&B)))" => "A"
    // BC.  ATOM "((A)->(B)->(C&D))" => "A"
    // BD.  FORMULA "((A&B)->(B&C))" => "A&B"
    // E.   ERROR "((&B)->(vC))" => Syntax Error "(&"


}
