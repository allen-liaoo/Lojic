package lojic.parser;

import lojic.nodes.Formula;
import lojic.nodes.Node;
import lojic.nodes.NodeClimber;
import lojic.nodes.connectives.Connective;

/**
 * Internal class for building formulas
 * User should ignore this class
 */
class IFormula extends Formula  {

    IFormula(int level, String string, Connective connective, Node parent) {
        super(level, string, connective, parent);
    }

    void setChildren(Node[] nodes) {
        super.children = nodes;
        super.climber = new NodeClimber(this); // update climber
    }

    void setString(String string) {
        super.string = string;
    }

}
