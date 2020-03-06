package lojic.parser;

import lojic.DefaultFactory;
import lojic.nodes.Node;
import lojic.nodes.connectives.BinaryConnective;
import lojic.nodes.connectives.Connective;
import lojic.nodes.connectives.UnaryConnective;
import lojic.nodes.truthapts.Atom;
import lojic.nodes.truthapts.Formula;
import lojic.nodes.truthapts.LocalAtom;
import lojic.tree.NodeTree;

import java.util.*;

/**
 * @author AlienIdeology
 *
 * A parser of tokens, lexed from LojicLexer
 * This generates a NodeTree, which is a tree of Node objects.
 */
public class LojicParser {

    private final List<Connective> connectives;
    private String cache;
    private List<Atom> cacheAtoms;

    /**
     * Load the parser with default connectives and associativity
     */
    public LojicParser() {
        connectives = new ArrayList<>();
        cacheAtoms = new ArrayList<>();
        reset();
    }

    /* Static Utilities */

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
     * @return true if a string is an open parenthesis, false if it is not,
     *          or if the string is null or empty
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

    /* Parsing */

    /**
     * Parse a logical expression with all default settings
     * @see DefaultFactory for all default constants
     * Note: this is a static method
     *
     * @param formula The logical expression
     * @return A syntax tree that represents the expression
     * @throws IllegalArgumentException If the string formula is null or empty
     * @throws SyntaxException If the string has a wrong syntax
     */
    public static NodeTree parseDefault(String formula) {
        return new LojicParser().parse(formula);
    }

    /**
     * Parse a logical expression and clear cache
     *
     * @param formula The logical expression
     * @return A syntax tree that represents the expression
     * @throws IllegalArgumentException If the string formula is null or empty
     * @throws SyntaxException If the string has a wrong syntax
     */
    public NodeTree parse(String formula) {
        if (formula == null || formula.isEmpty()) throw new IllegalArgumentException("Parser does not accept empty or null string");

        cache = strip(formula);
        Node root = parseString(null, cache, 0, 0);

        NodeTree tree =  new NodeTree(root, cacheAtoms.toArray(new Atom[0]));
        cacheAtoms = new ArrayList<>();
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
     * @throws IllegalArgumentException If the string formula is null or empty
     * @throws SyntaxException If the string has a wrong syntax
     */
    public NodeTree parse() {
        return parse(cache);
    }

    /* Non-static Utilities */

    /**
     * Check if a string is a formula by checking if it contains connectives
     * as defined by the settings of this parser
     *
     * @param string The string
     * @return true if the string is a formula, false if it is not,
     *          or if the string is null or empty
     */
    public boolean isFormula(String string) {
        if (string == null || string.isEmpty()) return false;
        for (Connective con : connectives) {
            if (string.contains(con.getOfficialSymbol())) return true;
        }
        return false;
    }

    /**
     * Check if a string is a connective, as defined by the settings of this parser
     *
     * @param string The string
     * @return true if the string is a connective, false if it is not,
     *          or if the string is null or empty
     */
    // FEATURE: No symbols stripping - Change if condition
    public boolean isConnective(String string) {
        return getConnective(string) != null;
    }

    /**
     * Check if a string is a binary connective, as defined by the settings of this parser
     *
     * @param string The string
     * @return true if the string is a binary connective, false if it is not,
     *          or if the string is null or empty
     */
    // FEATURE: No symbols stripping - Change if condition
    public boolean isBinaryConnective(String string) {
        return getConnective(string) instanceof BinaryConnective;
    }

    /**
     * Check if a string is a unary connective, as defined by the settings of this parser
     *
     * @param string The string
     * @return true if the string is a unary connective, false if it is not,
     *          or if the string is null or empty
     */
    // FEATURE: No symbols stripping - Change if condition
    public boolean isUnaryConnective(String string) {
        return getConnective(string) instanceof UnaryConnective;
    }

    /**
     * Get a connective object by its string
     *
     * @param connective The string
     * @return the connective object, or null if the parser does not recognize this string as a connective
     */
    // FEATURE: No symbols stripping - Change if condition
    public Connective getConnective(String connective) {
        if (connective == null || connective.isEmpty()) return null;
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

    /* Parser Settings */

    /**
     * Resets the parser, restore its settings (to default connectives) and clear its cache
     *
     * @return This parser for method chaining
     */
    public LojicParser reset() {
        connectives.clear();
        cache = "";
        cacheAtoms.clear();
        for (Connective con : DefaultFactory.DEFAULT_CONNECTIVES) {
            // default right associative
            con.setAssociativity(true);
            connectives.add(con);
        }
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
     * Connectives with the same official symbols or the same {@link Connective#getPossibleTruths()} with
     * existing instances of connectives will replace those existing connectives.
     *
     * Note: It is recommended to use {@link #replaceConnective(String, Connective)}, especially if one wants
     * to change a already-existing connective's official symbol
     *
     * @param connectives The connectives to be added or to replace
     * @return This parser for method chaining
     * @throws IllegalArgumentException If one of the connectives contains some "other symbols" that already belongs
     *          to an existing connective
     * @throws NullPointerException If a connective is null
     */
    public LojicParser setConnectives(Connective... connectives) {
        HashMap<Integer, Connective> toBeReplaced = new HashMap<>();
        List<Connective> toBeAdded = new ArrayList<>();
        for (Connective con : connectives) {
            Objects.requireNonNull(con, "Cannot add/replace a null connective!");
            for (Connective con1 : this.connectives) {
                // Check for existing connectives
                // Equal official symbols
                if (con.getOfficialSymbol().equals(con1.getOfficialSymbol())) {
                    toBeReplaced.put(this.connectives.indexOf(con1), con);
                    continue;
                }

                // Equal truth tables
                if (Arrays.equals(con.getPossibleTruths(), con1.getPossibleTruths())) {
                    toBeReplaced.put(this.connectives.indexOf(con1), con);
                    continue;
                }

                // Contains same symbols
                if (!Collections.disjoint(Arrays.asList(con1.getSymbols()),
                        Arrays.asList(con.getSymbols()))) {
                    throw new IllegalArgumentException("Cannot add a connective with \"Other Symbols\" that already exists!");
                }

                toBeAdded.add(con);
            }
        }

        toBeReplaced.forEach(this.connectives::set);
        this.connectives.addAll(toBeAdded);
        return this;
    }

    /**
     * Replaces an already-included connective with a new connective
     * This method finds the connective with the specified official symbol, then replace that connective
     *
     * @param offSymbol The official symbol of the original connective
     * @param connective The new connective
     * @return This parser for method chaining
     * @throws IllegalArgumentException If the official symbol is null or empty
     * @throws NullPointerException If the connective is null
     */
    public LojicParser replaceConnective(String offSymbol, Connective connective) {
        if (offSymbol == null || offSymbol.isEmpty()) throw new IllegalArgumentException("Cannot replace a connective with a null official symbol!");
        Objects.requireNonNull(connective, "Cannot replace null connective! Use LojicParser#removeConnectives(Connective...) instead.");

        HashMap<Integer, Connective> toBeReplaced = new HashMap<>();
        for (Connective con : this.connectives) {
            if (con.getOfficialSymbol().equals(offSymbol)) {
                toBeReplaced.put(this.connectives.indexOf(con), connective);
                break;
            }
        }
        toBeReplaced.forEach(this.connectives::set);
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
     * Remove connectives from the list by providing an array of official symbols
     *
     * @param conSymbols The connectives' official symbols
     * @return This parser for method chaining
     * @throws NullPointerException If a connective official symbol is null
     */
    public LojicParser removeConnectives(String... conSymbols) {
        List<Connective> toBeRemoved = new ArrayList<>();
        for (Connective connective : connectives) {
            for (String sym : conSymbols) {
                Objects.requireNonNull(sym, "Cannot remove a null connective!");
                if (connective.getOfficialSymbol().equals(sym)) {
                    toBeRemoved.add(connective);
                }
            }
        }
        toBeRemoved.forEach(connectives::remove);
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
        for (int[] ints : indexes) {
            int index = ints[0];
            int prec = ints[1];

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
            formula.append(rnode.getString());
            //right.stream().map(Token::toString).forEach(formula::append);

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
