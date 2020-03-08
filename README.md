# Lojic
Lojic is a java library that parses logical expressions in Propositional Logic and generate
abstract syntax trees and truth tables.

### Format
 - Formula - Any statement that contains a connective is a formula
 - Connective - Logical operators like AND, OR
   - Official symbol - The connective symbol that this java library deals
     with and displays as. A connective only has one official symbol.
   - Other symbols - Any symbols that is also recognized by the parser/lexer.
   - Precedence - See [Wikipedia.org/Logical_connective#Order__of_precedence](https://en.wikipedia.org/wiki/Logical_connective#Order_of_precedence)
   - Associativity - See [Wikipedia.org/Operator_associativity](https://en.wikipedia.org/wiki/Operator_associativity)
 - Atom - Variables that denotes propositions
   - True atom - An atom with a unique name that always has the value of `true`
   - False atom - An atom with a unique name that always has the value of `false`

The name of atoms (propositions or operands) must be alphabetic and/or
numeric. There cannot be whitespaces or special characters. There are no
length limits.

### Important Java Objects
 - LojicParser - Used to parse each logical expressions. Its internal lexer automatically converts all logical connectives to their
official symbols and all parenthesis to the standard type `()`.
 - Node - A well-formed formula or atom, containing its children formulas or atoms. It is structured like an abstract  syntax tree.
 - TruthTable - A list of Columns of atoms and formulas
   - Column - Contains a node and its truth-values. 
   - TTableBuilder - Used to configure truth table's settings (such as showning sub-columns, recognition of true/false atoms).
 - Argument - A list of premises and a conclusion, supports (semantic) validity checking.


### Example
```java
public class Example {
    
    public static void main(String[] args) {
        Node node = LojicParser.parseDefault("P->Q"); // throws SyntaxException if the syntax is incorrect
        
        TruthTable table = node.buildTruthTable();
        String result = table.print();
    }
    
}
```
The result would be:

P|Q|(P→Q)
:---:|:---:|:----:
T|T| T
T|F| F
F|T| T
F|F| T

On the other hand,
```
parser.parse("(P->Q");
```
There would be a SyntaxException thrown:
```
lojic.parser.SyntaxException: Index 4 - Missing closing parenthesis
(P->Q
    ^
```

You can also configure the parser for it to recognize more or less
connectives than the default. Start by creating an instance of
LojicParser:
```java
public class FormulaParsing {
    
    public static void main(String[] args) {
        LojicParser parser = new LojicParser();
        Node tree = parser
                        .useMinimalConnectives() // Method chaining
                        .append("P->") // caches a string
                        .append("(Q->R)")
                        .parse(); // an alternative way of parsing an expression
                                  // whenever one parses the string, the parser's cache resets
        
        TruthTable table = tree.buildTruthTable();
        List<Column> columns = table.getColumns(); 
        // you can get a list of columns for the table rather than a string output
    }
    
}
```
You can also create an argument and check its validity.
```java
public class ArgumentParsing {
    
    public static void main(String[] args) {
        Argument argument = Argument.fromSequent("P->Q, ~Q ⊢ ~P");
        argument.isValid(); // returns true
    }
}
```
For more examples, checkout [these examples](https://github.com/AlienIdeology/Lojic/tree/master/example/src/)  

### Logical Connectives
Name|Object Name|Official Symbol|Other Symbols|Precedence|Associativity
----|:---------:|:-------------:|-------------|:--------:|:-----------:
Negation|NEG|¬|~, !|50|right
Conjunction|AND|∧|/\\, &, ^, ×, •, ⋅|40|right
Alternative Denial, Sheffer Stroke|NAND|↑|⊼|40|right
Disjunction|OR|∨|\\/, \|, +, ∥|30|right
Joint Denial, Peirce's arrow|NOR|↓|⊽|30|right
Exclusive Disjunction|XOR|⊕|⊻, <-/->, <=/=>, ↮, ≢|30|right
Conditional, Material Implication|IF|→|>, ->, =>, ⇒, ⊃|20|right
Material Nonimplication|NIF|↛|-/>, =/>|20|right
Converse Implication|IF_CON|←|<, <-, <=, ⇐, ⊂|20|right
Converse Nonimplication|N_IF_CON|↚|</-, </=|20|right
Biconditional, Logical Equality|IFF|↔|<>, <->, <=>, ≡, ⇔, =|10|right

### Parenthesis
> (), {}, []

The opening parenthesis does not have to be the same type as the closing
parenthesis. For example, `(A&B]->C` is identical to `(A&B)->C`.

### Truth Values
Atoms with these names will automatically be filled with the corresponding truth value.  
The java class TTableBuilder is responsible for building truth tables that recognize these True/False atoms

True|False
----|-----
T|F
⊤|⊥
1|0

### Sequent
In a String sequent, premises must be divided by commas `,`. The conclusion must be separated from 
the premises by a symbol of logical consequence. If the conclusion is true independent of any premises
(it is a logical theorem), then the sequent must start with a symbol of logical consequence.  
All recognized symbols of logical consequence is as follows:

Type|Symbols
-------|----
Syntactic Consequence|⊢, &#124;-
Semantic Consequence|⊨, &#124;=

\* Even though the symbols denote different concepts of logical consequence, they are not treated differently in this instance. 

### Todo List
- [x] Propositional Logic
  - [x] Lexer and Parser
  - [x] Abstract Syntax Tree
  - [x] TruthTable
  - [x] Javadocs
  - [x] Semantic Validity checker
  - [ ] Syntactic Validity checker (Inference Rules)
- [ ] Quantificational Logic
  - [ ] Lexer and Parser
  - [ ] Abstract Syntax Tree
  - [ ] Javadocs
  - [ ] Subject variables
  - [ ] Predicates
  - [ ] Quantifiers
  - [ ] Validity?
- [ ] Restructuring (extract generic classes, two packages for PL and QL)