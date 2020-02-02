package lojic.tree;

import lojic.nodes.Node;

/**
 * @author AlienIdeology
 */
public class FormulaTree {

    private Node root;
    private int levels;

    public FormulaTree(Node root, int levels) {
        this.root = root;
        this.levels = levels;
    }

    public Node getRoot() {
        return root;
    }

    public int getLevels() {
        return levels;
    }

}
