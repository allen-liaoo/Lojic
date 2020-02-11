package lojic.parser;

import lojic.DefaultFactory;
import lojic.nodes.Node;
import lojic.nodes.connectives.Connective;
import lojic.nodes.truthapts.Atom;
import lojic.nodes.truthapts.Formula;
import lojic.nodes.truthapts.LocalAtom;
import lojic.tree.NodeTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author AlienIdeology
 *
 * A parser of tokens, lexed from LojicLexer
 * This generates a NodeTree, which is a tree of Node objects.
 */
public class LojicParser {

    private List<Connective> connectives;
    private String cache;
    private List<Atom> cacheAtoms;

    /**
     * Load the parser with default connectives and associativity
     */
    public LojicParser() {
        connectives = Arrays.asList(DefaultFactory.DEFAULT_CONNECTIVES);
        cache = "";
        cacheAtoms = new ArrayList<>();
        for (Connective con : connectives) {
            con.setAssociativity(true);
        }
    }

    /**
     * Parse a logical expression with all default settings
     * @see DefaultFactory for all default constants
     * Note: this is a static method
     *
     * @param formula The logical expression
     * @return A syntax tree that represents the expression
     */
    public static NodeTree parseDefault(String formula) {
        return new LojicParser().parse(formula);
    }

    /**
     * Check if a string is a valid atom
     * Returns true if the atom string contains only alphabetic or numeric letters
     *
     * @param string The string
     * @return true if the string is a valid atom, false if not
     *          returns false if the string is null or empty
     */
    public static boolean isAtomic(String string) {
        if (string == null || string.isEmpty()) return false;
        for (char ch : string.toCharArray()) {
            if (!Character.isAlphabetic(ch) && !Character.isDigit(ch)) return false;
        }
        return true;
    }

    /**
     * Check if a string is an open parenthesis
     *
     * @param string The string
     * @return true if a string is an open parenthesis, false if not
     *          returns false if the string is null or empty
     */
    public static boolean isOpenParenthesis(String string) {
        if (string == null || string.isEmpty()) return false;
        for (String s : TokenType.PARENTHESIS_OPEN.SYMBOLS) {
            if(string.equals(s)) return true;
        }
        return false;
    }

    /**
     * Check if a string is an closing parenthesis
     *
     * @param string The string
     * @return true if a string is an closing parenthesis, false if not
     *          returns false if the string is null or empty
     */
    public static boolean isCloseParenthesis(String string) {
        if (string == null || string.isEmpty()) return false;
        for (String s : TokenType.PARENTHESIS_CLOSE.SYMBOLS) {
            if(string.equals(s)) return true;
        }
        return false;
    }

    /**
     * Check if a string's first character is an open parenthesis,
     * and if its last character is a closing parenthesis
     *
     * @param string The string
     * @return true if a string is an parenthesized, false if not
     *          returns false if the string is null or empty
     */
    public static boolean isParenthesized(String string) {
        if (string == null || string.isEmpty()) return false;
        return isOpenParenthesis(String.valueOf(string.charAt(0)))
                && isCloseParenthesis(String.valueOf(string.charAt(string.length()-1)));
    }

    /**
     * Parse a logical expression and clear cache
     *
     * @param formula The logical expression
     * @return A syntax tree that represents the expression
     */
    public NodeTree parse(String formula) {
        if (formula == null || formula.isEmpty()) throw new IllegalArgumentException("Parser does not accept empty or null string");

        cache = strip(formula);
        Node root = parseString(null, cache, 0, 0);

        NodeTree tree =  new NodeTree(root, cacheAtoms.toArray(new Atom[0]));
        cacheAtoms.clear();
        cache = "";
        return tree;
    }

    /**
     * Append a string to the cache
     * Use {@link #parse()} after the formula is cached
     *
     * @param formula The string to be cached
     * @return This parser for method chaining
     */
    public LojicParser append(String formula) {
        cache += formula;
        return this;
    }

    /**
     * Parse the cached logical expression
     *
     * @return A syntax tree that represents the expression
     */
    public NodeTree parse() {
        return parse(cache);
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
     * 1. {@link DefaultFactory#NEG}
     * 2. {@link DefaultFactory#AND}
     * 3. {@link DefaultFactory#OR}
     * 4. {@link DefaultFactory#IF}
     * 5. {@link DefaultFactory#IFF}
     *
     * Removed connectives
     * 1. {@link DefaultFactory#NAND}
     * 2. {@link DefaultFactory#NOR}
     * 3. {@link DefaultFactory#XOR}
     * 4. {@link DefaultFactory#NIF}
     * 5. {@link DefaultFactory#IF_CON}
     * 6. {@link DefaultFactory#N_IF_CON}
     *
     * @return This parser for method chaining
     */
    public LojicParser useMinimalConnectives() {
        removeConnectives(DefaultFactory.NAND,
                DefaultFactory.NOR,
                DefaultFactory.XOR,
                DefaultFactory.NIF,
                DefaultFactory.IF_CON,
                DefaultFactory.N_IF_CON);
        return this;
    }

    /**
     * Check if a string is a connective, as defined by the settings of this parser
     *
     * @param string The string
     * @return true if the string is a connective
     */
    // FEATURE: No symbols stripping - Change if condition
    public boolean isConnective(String string) {
        return getConnective(string) != null;
    }

    /**
     * Check if a string is a binary connective, as defined by the settings of this parser
     *
     * @param string The string
     * @return true if the string is a binary connective
     */
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

    /**
     * Check if a string is a unary connective, as defined by the settings of this parser
     *
     * @param string The string
     * @return true if the string is a unary connective
     */
    // FEATURE: No symbols stripping - Change if condition
    public boolean isUnaryConnective(String string) {
        for (Connective con : connectives) {
            if (con.isUnary()) {
                if (string.equals(con.getOfficialSymbol())) return true;
            }
        }
        return false;
    }

    /**
     * Get a connective object by its string
     *
     * @param connective The string
     * @return the connective object, or null if the parser does not recognize this string as a connective
     */
    // FEATURE: No symbols stripping - Change if condition
    public Connective getConnective(String connective) {
        for (Connective con : connectives) {
            if (con.getOfficialSymbol().equals(connective)) return con;
        }
        return null;
    }

    /**
     * Strips a string of all unofficial connective symbols and white spaces
     *
     * @param input The string to be stripped
     * @return The result string
     */
    // FEATURE: No symbols stripping - Remove for loops
    public String strip(String input) {
        input = input.replaceAll("\\s", ""); // Get rid of all white spaces

        for (Connective con : connectives) {
            for (String s : con.getSymbols()) {
                if (input.contains(s))
                    input = input.replace(s, con.getOfficialSymbol());
            }
        }
        return input;
    }

    // Recursive method used to parse any unparsed formula to nodes
    private Node parseString(Node parent, String formula, int location, int level) {
        LojicLexer tokenizer = new LojicLexer(this, cache, formula);
        List<Token> tokens = tokenizer.lex(location);
        // DEBUG Print formulas
        /*System.out.println(tokens);
        System.out.println(Arrays.toString(tokens.stream().map(tok -> (tok instanceof Token.ParsedFormula) ?
                "PARSED_FORMULA" : tok.getType()).toArray()));
        System.out.println(Arrays.toString(tokens.stream().map(Token::getLocation).toArray()));
        tokens.forEach(tk -> System.out.println(tk + "\n" + LojicUtil.generateIndicator(cache, tk.getLocation())));*/

        return parseTokens(parent, tokens, level);
    }

    // Recursive method used to parse any parsed formula, list of tokens, or individual atom, to nodes
    private Node parseTokens(Node parent, List<Token> tokens, int level) {

        if (tokens.size() == 1 || // second boolean expression might be redundant
            (tokens.stream().noneMatch(t -> t.isType(TokenType.UNARY_CONNECTIVE) || t.isType(TokenType.BINARY_CONNECTIVE)))
        ) {
            Token tok = tokens.get(0);
            if (tok.isType(TokenType.ATOM)) {
                Atom atom = new Atom(tok.toString());
                if (!cacheAtoms.contains(atom)) {
                    cacheAtoms.add(atom);
                } else {
                    atom = cacheAtoms.get(cacheAtoms.indexOf(atom));
                }

                return new LocalAtom(level, parent, atom);
            } else if (tok.isParsedFormula()) {
                return parseTokens(parent, ((Token.ParsedFormula) tok).getTokens(), level);
            } else if (tok.isType(TokenType.FORMULA)) {
                return parseString(parent, tok.toString(), tok.getLocation(), level);
            }
        }

        List<int[]> indexes = new ArrayList<>();

        // Get the indexes of connectives, ignores atom or formulas
        for (int i = 0; i < tokens.size(); i++) {
            int prec = getPrecedence(tokens.get(i));
            if (prec < DefaultFactory.PRECEDENCE_DEFAULT) {
                indexes.add(new int[]{i, prec});
            }
        }

        int lowIndex = -1; // index of the connective with the lowest precedence
        int lowPrec = DefaultFactory.PRECEDENCE_DEFAULT;
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

        // TODO: Thoroughly debugs parenthesizing node strings
        StringBuilder formula = new StringBuilder();
        Formula thisNode = new Formula(level, "", parent);
        Connective mainConnective = getConnective(tokens.get(lowIndex).toString());
        thisNode.setConnective(mainConnective);
        formula.append("(");

        // Main connective is unary
        if (lowIndex <= 0) {
            List<Token> right = tokens.subList(lowIndex+1, tokens.size());
            Node rnode = parseTokens(thisNode, right, level+1);

            thisNode.setChildren(new Node[]{rnode});
            formula.append(mainConnective.getOfficialSymbol());
            right.stream().map(Token::toString).forEach(formula::append);

        // Main connective is binary
        } else {
            List<Token> left = tokens.subList(0, lowIndex);
            List<Token> right = tokens.subList(lowIndex+1, tokens.size());
            Node lnode = parseTokens(thisNode, left, level+1);
            Node rnode = parseTokens(thisNode, right, level+1);

            thisNode.setChildren(new Node[]{lnode, rnode});
            //left.stream().map(Token::toString).forEach(formula::append);
            formula.append(lnode.getString());
            formula.append(mainConnective.getOfficialSymbol());
            //right.stream().map(Token::toString).forEach(formula::append);
            formula.append(rnode.getString());

        }

        formula.append(")");
        thisNode.setString(formula.toString());
        return thisNode;
    }

    private int getPrecedence(Token token) {
        if (token.isType(TokenType.ATOM) || token.isType(TokenType.FORMULA)) return DefaultFactory.PRECEDENCE_HIGHEST;
        else {
            return getConnective(token.toString()).getPrecedence();
        }
    }

}
