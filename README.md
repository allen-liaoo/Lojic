# Lojic
Lojic is a java library that parses logical expressions and generate
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

The name of atoms (propositions or operands) must be alphabetic and/or
numeric. There cannot be whitespaces or special characters. There are no
length limits.

The lexer automatically converts all logical connectives to their
official symbols and all parenthesis to the standard type `()`.


### Example
```java
public class Example {
    
    public static void main(String[] args) {
        NodeTree tree = LojicParser.parseDefault("P->Q"); // throws SyntaxException if the syntax is incorrect
        
        TruthTable table = new TruthCalculator(tree).compute();
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
public class MoreExample {
    
    public static void main(String[] args) {
        LojicParser parser = new LojicParser();
        NodeTree tree = parser
                            .useMinimalConnectives() // Method chaining
                            .append("P->") // caches a string
                            .append("(Q->R)")
                            .parse(); // an alternative way of parsing an expression
                                      // whenever one parses the string, the parser's cache resets
        
        TruthTable table = new TruthCalculator(tree).compute();
        List<Column> columns = table.getFullTable(); 
        // alternatively, you can get a list of columns for the table rather than a string output
    }
    
}
```

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

True|False
----|-----
T|F
⊤|⊥
1|0

### Todo List
- [x] Lexer and Parser (LojicLexer and LojicParser)
- [x] Abstract Syntax Tree (NodeTree)
- [ ] TruthTable
- [ ] TruthTable printing
- [ ] Javadocs