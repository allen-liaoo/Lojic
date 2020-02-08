# Lojic
### Logic Expression Parser and Truth-Table Generator
Lojic is a java library that parses logical expressions and generate
abstract syntax trees and truth tables.

### Example
```java
LojicParser parser = new LojicParser();
FormulaTree tree = parser.parse("P->Q"); // throws SyntaxException if the syntax is incorrect
TruthTable tb = new TruthTable(tree);
Map<Node, boolean[]> table = table.detailFull().getTable();
```
The result would be:

P|Q|P->Q
:---:|:---:|:----:
T|T| T
T|F| F
F|T| T
F|F| T

On the other hand,
```java
parser.parse("(P->Q");
```
The result would be
```
lojic.parser.SyntaxException: Index 4 - Missing closing parenthesis
(P->Q
    ^
```

### Supported Logical Connectives
Name|Object Name|Official Symbol|Other Symbols|Precedence|Associativity
----|:---------:|:-------------:|-------------|----------|-------------
Negation|NEG|¬|~, !|50|right
Conjunction|AND|∧|&, ^, ×, •, ⋅|40|right
Alternative Denial, Sheffer Stroke|NAND|↑|⊼|40|right
Disjunction|OR|∨|+, ∥|30|right
Joint Denial, Peirce's arrow|NOR|↓|⊽|30|right
Exclusive Disjunction|XOR|⊕|⊻, <-/->, <=/=>, ↮, ≢|30|right
Conditional, Material Implication|IF|→|->, =>, ⇒, ⊃|20|right
Material Nonimplication|NIF|↛|-/>, =/>|20|right
Converse Implication|IF_CON|←|<-, <=, ⇐, ⊂|20|right
Converse Nonimplication|NIF_CON|↚|</-, </=|20|right
Biconditional, Logical Equality|IFF|↔|<->, <=>, ≡, ⇔, =|10|right

### Supported Parenthesis
> (), {}, []

The opening parenthesis does not have to be the same type as the closing
parenthesis. For example, `(A&B]->C` is identical to `(A&B)->C`.

### Format
The name of atoms (propositions or operands) must be alphabetic or
numeric. There cannot be whitespaces or special characters. There are no
length limits to their names.

The lexer automatically converts all logical connectives to their
official symbols and all parenthesis to the standard type `()`.

### Todo List
- [x] Debug lexer (location tracking, error handling)
- [ ] - [ ] Finish parser (precedence climbing + recursively descent to build tree)
- [ ] T, F, 0, 1
- [ ] Javadocs
- [ ] Formula Tree
- [ ] TruthTable