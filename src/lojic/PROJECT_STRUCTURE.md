# Node
String getString()  
Node getParent()  
int getLevel()

### TruthApt
void setTruths(boolean [])  
boolean[] getTruths()  
boolean isSet()

###  Formula
extends Node, implements TruthApt  
Connective getConnective()  
BinaryConnective/null getBConnective()  
UnaryConnective/null getUConnective()  
boolean isBinary()  
void setChildren(Node[])  
Node[] getChildren()
###  LocalAtom
extends Node  
Atom getAtom()
###  Atom
implements TruthApt

### Connective(String, String...)
String getOfficialSymbol()  
String[] getSymbols()
### BinaryConnective
extends Connective  
boolean computeTruth(boolean, boolean)
### UnaryConnective
extends Connective  
boolean computeTruth(boolean)

------------------------------------

# Parser
FormulaTree parse(String)  
private void parse(Formula) - called to parse all formulas and set their children nodes.

## Process

1. Strip string
2. Group atom (StringReader)
3. Group parenthesis and Syntax error handling (parser.handleNext)
4. Token array
5. Read Connectives
    1. Order of precedence
    2. Associativity
    3. Set children -> Recursively parse each
6. Construct Tree

##  Order of precedence
```
50 ¬
40 ∧, ↑
30 ∨, ↓, ⊻
20 →, ↛  ← ↚
10 ↔
```

### LojicLexer(String)
TokenList lex(int) - return a list of tokens from thes tring  
Token handleNext() - handles syntax error and group parenthesized atoms or formulas  
Token next() - return an atom token (grouped) or other tokens
### Token
enum Type  
    ATOM  
    CONNECTIVE  
    PARENTHESIS_OPEN  
    PARENTHESIS_CLOSE  
    UNKNOWN  
    FORMULA  
String string  
int location  
Type type
### SyntaxException
extends Exception

##  Lexer
Reads string char by char. Save formulas to parse later, in the same manner. Cases:

1. `(`
   1. Error: Missing `)`.
   2. Error: Missing *formula* or *atom* `()`.
   3. Error: Binary cannot follow from `(`
2. `)` Error: Unary, Atom, or `(` cannot follow from `)`
3. `A-Za-z`
   1. Error: Unary or `(` cannot follow from an Atom
4. `~` or `&v→↔...`
   1. Error: Missing *formula* or *atom* (right).
   2. Error: Binary or `)` cannot follow from an Unary
6. Error: Unknown character. (`0-9.*` etc)

------------------------------------

## ConnectiveFactory
Contain public static objects of connective.
```
NEG "¬"
    !boolean
AND "∧"
    boolean1 && boolean2
NAND "↑"
    !boolean1 \|| !boolean2
OR "∨"
    boolean1 \|| boolean2
NOR "↓"
    !boolean1 && !boolean2
XOR "⊻"
    (boolean1 && !boolean2) \|| (!boolean1 && boolean2)
IF "→"
    !boolean1 \|| boolean2
NIF "↛"
    boolean1 && !boolean2
IF_CON "←"
    boolean1 \|| !boolean2
NIF_CON "↚"
    !boolean1 && boolean2
IFF "\↔"  
    (boolean1 \|| !boolean2) && (!boolean1 \|| boolean2)
```

------------------------------------
# FormulaTree (Node n, int levels)
Node getRoot()  
int getLevels()  
Atom[] getAtoms()  
Node[] getNodes(int level) - return all nodes at a level  
TruthApt[] getTruthApts() - return all TruthApts in order of precedence

# TruthTable (FormulaTree)
detailSetting(boolean showSubColumn, Column...)  
//default {false, ATOM, ROOT}  
detailModerate() {false, PREMISE, ROOT}  
detailFull() {true, ATOM FORMULA, ROOT}  
boolean isAlwaysTrue()  
Node[] getPremises()  
void fillTruths()  
Map<Node, boolean[]> getTable()  
FormulaTree getTree()

enum Column  
    ATOMS  
    PREMISES  
    FORMULAS  
    ROOT  
fillTruths() Uses:  
FormulaTree.getAtoms() -> Fill in all atoms with truth values  
FormulaTree.getNodes(int level) -> For all nodes at level, check parent formula.  
if parent formula has no truth value, compute truth value. Get all child nodes from parent formula,  
and remove them from the list. Repeat the process until there is no nodes in the list.  
Then get list of nodes at an upper level (level--). repeat until level == 1.