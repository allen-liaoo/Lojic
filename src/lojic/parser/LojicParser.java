package lojic.parser;

import com.sun.istack.internal.Nullable;
import lojic.LojicUtil;
import lojic.nodes.ConnectiveFactory;
import lojic.nodes.Node;
import lojic.nodes.connectives.Connective;
import lojic.nodes.truthapts.Atom;
import lojic.nodes.truthapts.Formula;
import lojic.nodes.truthapts.LocalAtom;
import lojic.parser.token.Token;
import lojic.parser.token.TokenType;
import lojic.tree.FormulaTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author AlienIdeology
 *
 * A parser of tokens, lexed from LojicLexer
 * This generates a FormulaTree, which is a tree of Node objects.
 */
public class LojicParser {

    private List<Connective> connectives;
    private String cache;
    private List<Atom> cacheAtoms;

    /**
     * Load the parser with default connectives and associativity
     */
    public LojicParser() {
        connectives = Arrays.asList(ConnectiveFactory.DEFAULT_CONNECTIVES);
        cache = "";
        cacheAtoms = new ArrayList<>();
        for (Connective con : connectives) {
            boolean assoc;
            switch (con.getPrecedence()) {
                case 50:
                    assoc = ConnectiveFactory.RIGHT_ASSOCIATIVE_P50;
                    break;
                case 40:
                    assoc = ConnectiveFactory.RIGHT_ASSOCIATIVE_P40;
                    break;
                case 30:
                    assoc = ConnectiveFactory.RIGHT_ASSOCIATIVE_P30;
                    break;
                case 20:
                    assoc = ConnectiveFactory.RIGHT_ASSOCIATIVE_P20;
                    break;
                case 10:
                    assoc = ConnectiveFactory.RIGHT_ASSOCIATIVE_P10;
                    break;
                default:
                    assoc = false;
            }
            con.setAssociativity(assoc);
        }
    }

    /**
     * Parse the logical expression
     *
     * @param formula The logical expression
     * @return A syntax tree that represents the expression
     */
    public FormulaTree parse(String formula) {
        if (formula == null || formula.isEmpty()) throw new IllegalArgumentException("Parser does not accept empty or null string");

        cache = LojicUtil.strip(formula);
        parseString(new Formula(0, formula, null), cache, 0);

        cache = "";
        cacheAtoms.clear();
        return new FormulaTree(null, 0);
    }

    public FormulaTree parse() {
        return parse(cache);
    }

    public LojicParser append(String formula) {
        cache += formula;
        return this;
    }

    /**
     * Set the associativity of connectives at a precedence level.
     * All connectives with the same precedence level must have the same associativity (left or right).
     *
     * @param precedence The precedence level/
     * @param isRightAssociative true if the connectives should be right associative,
     *                           false if the connectives should be left associative
     * @return This parser for method chaining
     */
    public LojicParser setAssociativity(int precedence, boolean isRightAssociative) {
        for (Connective con : connectives) {
            if (con.getPrecedence() == precedence) {
                con.setAssociativity(isRightAssociative);
            }
        }
        return this;
    }

    /**
     * Add or replace connectives to this parser
     * Connectives with unique official symbols (is not already stored in the parser's list of connectives)
     * will be added to the list.
     * Connectives with the same official symbols will replace instances connectives with the same official symbol.
     *
     * @param connectives The connectives to add or replace
     * @return This parser for method chaining
     */
    public LojicParser setConnectives(Connective... connectives) {
        for (Connective con : connectives) {
            boolean replaced = false;
            for (Connective con1 : this.connectives) {
                if (con.getOfficialSymbol().equals(con1.getOfficialSymbol())) {
                    this.connectives.set(this.connectives.indexOf(con1), con);
                    replaced = true;
                }
                if (!replaced) this.connectives.add(con);
            }
        }
        return this;
    }

    /**
     * Remove connectives from the list
     *
     * @param connectives The connectives to be removed
     * @return This parser for method chaining
     */
    public LojicParser removeConnectives(Connective... connectives) {
        this.connectives.removeAll(Arrays.asList(connectives));
        return this;
    }

    /**
     * Removes unnecessary default connectives
     * Unmoved connectives:
     * 1. {@link ConnectiveFactory#NEG}
     * 2. {@link ConnectiveFactory#AND}
     * 3. {@link ConnectiveFactory#OR}
     * 4. {@link ConnectiveFactory#IF}
     * 5. {@link ConnectiveFactory#IFF}
     *
     * Removed connectives
     * 1. {@link ConnectiveFactory#NAND}
     * 2. {@link ConnectiveFactory#NOR}
     * 3. {@link ConnectiveFactory#XOR}
     * 4. {@link ConnectiveFactory#NIF}
     * 5. {@link ConnectiveFactory#IF_CON}
     * 6. {@link ConnectiveFactory#NIF_CON}
     *
     * @return This parser for method chaining
     */
    public LojicParser useMinimalConnectives() {
        removeConnectives(ConnectiveFactory.NAND,
                ConnectiveFactory.NOR,
                ConnectiveFactory.XOR,
                ConnectiveFactory.NIF,
                ConnectiveFactory.IF_CON,
                ConnectiveFactory.NIF_CON);
        return this;
    }

    // FEATURE: No symbols stripping - Change if condition
    public boolean isConnective(String token) {
        for (Connective con : connectives) {
            if (token.equals(con.getOfficialSymbol())) return true;
        }
        return false;
    }

    // FEATURE: No symbols stripping - Change if condition
    public boolean isBinaryConnective(String string) {
        for (Connective con : connectives) {
            if (con.isBinary()) {
                if (string.equals(con.getOfficialSymbol())) {
                    return true;
                }
            }
        }
        return false;
    }

    // FEATURE: No symbols stripping - Change if condition
    public boolean isUnaryConnective(String string) {
        for (Connective con : connectives) {
            if (con.isUnary()) {
                if (string.equals(con.getOfficialSymbol())) return true;
            }
        }
        return false;
    }

    @Nullable
    // FEATURE: No symbols stripping - Change if condition
    public Connective getConnective(String connective) {
        for (Connective con : connectives) {
            if (con.getOfficialSymbol().equals(connective)) return con;
        }
        return null;
    }

    // Recursive method used to parse any individual formula to nodes
    private void parseString(Node parent, String formula, int location) {
        LojicLexer tokenizer = new LojicLexer(this, cache, formula);
        List<Token> tokens = tokenizer.lex(location);
        // DEBUG Print formulas
        System.out.println(tokens);
        System.out.println(Arrays.toString(tokens.stream().map(tok -> (tok instanceof Token.ParsedFormula) ?
                "PARSED_FORMULA" : tok.getType()).toArray()));
        System.out.println(Arrays.toString(tokens.stream().map(Token::getLocation).toArray()));
        System.out.println();
        // DEBUG tokens.forEach(tk -> System.out.println(tk + "\n" + LojicUtil.generateIndicator(cache, tk.getLocation())));

        Node node = parseTokens(parent, tokens);
    }

    private Node parseTokens(Node parent, List<Token> tokens) {

        if (tokens.size() == 1) {
            Token tok = tokens.get(0);
            if (tok.isType(TokenType.ATOM)) {
                Atom atom = new Atom(tok.toString());
                if (!cacheAtoms.contains(atom)) {
                    cacheAtoms.add(atom);
                } else {
                    atom = cacheAtoms.get(cacheAtoms.indexOf(atom));
                }

                return new LocalAtom(tok.getLocation(), parent, atom);
            } else if (tok instanceof Token.ParsedFormula) {
                return parseTokens(parent, ((Token.ParsedFormula) tok).getTokens());
            }
        }

        List<int[]> indexes = new ArrayList<>();

        // Get the indexes of connectives, ignores atom or formulas
        for (int i = 0; i < tokens.size(); i++) {
            int prec = getPrecedence(tokens.get(i));
            if (prec < ConnectiveFactory.PRECEDENCE_DEFAULT) {
                indexes.add(new int[]{i, prec});
            }
        }

        System.out.println(Arrays.toString(indexes.stream().map(Arrays::toString).toArray()));

        int lowIndex = -1; // index of the connective with the lowest precedence
        int lowPrec = ConnectiveFactory.PRECEDENCE_DEFAULT;
        // Handle associativity and precedence
        for (int j = 0; j < indexes.size(); j++) {
            int index = indexes.get(j)[0];
            int prec = indexes.get(j)[1];

            // When it meets a lower precedence, cache the index and precedence
            if (prec < lowPrec) {
                lowPrec = prec;
                lowIndex = index;

            // When it meets a connective with the same precedence, check left associativity.
            // If it is left associative, cache the right connective's index and precedence
            } else if (prec == lowPrec) {
                Connective con = getConnective(tokens.get(index).toString());
                if (!con.isRightAssociative()) {
                    lowIndex = index;
                }
            }
        }

        System.out.println(lowIndex + " " + lowPrec);

        List<Token> left = lowIndex > 0 ?
                tokens.subList(0, lowIndex) : null; // null when

        List<Token> right = lowIndex < tokens.size()-1 ?
                tokens.subList(lowIndex+1, tokens.size()) : null; // never null

        return null;
    }

    // DEBUG: Parse and print everything

    /*private void pe(ParseList<Token> tokens) {
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
    /*private List<Token> parsetok(ParseList<Token> list, int prevPrec) {
        Token left = list.next();
        Token.ParsedFormula formula = new Token.ParsedFormula(left);
        while (getPrecedence(list.peek()) <= prevPrec) {
            // Unfinished
             formula.add(list.next());
        }
        System.out.println(left);
        return null;
    }*/

    private int getPrecedence(Token token) {
        if (token.isType(TokenType.ATOM) || token.isType(TokenType.FORMULA)) return ConnectiveFactory.PRECEDENCE_HIGHEST;
        else {
            return getConnective(token.toString()).getPrecedence();
        }
    }

}
