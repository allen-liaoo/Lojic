package lojic.table;

import lojic.nodes.truthapts.Atom;
import lojic.tree.NodeTree;

import static lojic.table.Column.*;

/**
 * @author AlienIdeology
 */
public class TruthTable {

    private final NodeTree nodeTree;
    private int rowsize;

    private boolean showSubColumn;
    private Column[] columns;

    private String[] trueAtoms = new String[]{"T", "⊤", "1"};
    private String[] falseAtoms = new String[]{"F", "⊥", "0"};

    public TruthTable(NodeTree nodeTree) {
        this.nodeTree = nodeTree;
        rowsize = 2 ^ (nodeTree.getAtoms().length);
        detailSetting(false, ATOMS, ROOT);
    }

    public TruthTable detailSetting(boolean showSubColumn, Column... columns) {
        this.showSubColumn = showSubColumn;
        this.columns = columns;
        return this;
    }

    public TruthTable detailModerate() {
        return detailSetting(false, PREMISES, ROOT);
    }

    public TruthTable detailFull() {
        return detailSetting(true, ATOMS, FORMULAS, ROOT);
    }

    public void setTrueAtoms(String... trueAtoms) {
        this.trueAtoms = (trueAtoms == null)
                ? new String[]{} : trueAtoms;
    }

    public void setFalseAtoms(String... falseAtoms) {
        this.falseAtoms = (falseAtoms == null)
                ? new String[]{} : falseAtoms;
    }

    private void fillAtomTruths() {
        Atom[] atoms = nodeTree.getAtoms();

        for (int i = 0; i < atoms.length; i++) {
            Atom atom = atoms[i];
            boolean[] truths = new boolean[rowsize];

            if (isTAtom(atom.toString())) {
                truths = fillTruths(rowsize, true, 0, truths.length);
            } else if (isFAtom(atom.toString())) {
                truths = fillTruths(rowsize, false, 0, truths.length);
            } else {
                boolean value = true;
                int num = i + 1;
                int times = 2 ^ (atoms.length - num);
                // TODO: fill truth values
            }
            atom.setTruths(truths);
        }
    }

    private boolean[] fillTruths(int capacity, boolean value, int start, int end) {
        boolean[] truths = new boolean[capacity];
        for (int i = start; i < end; i++) {
            truths[i] = value;
        }
        return truths;
    }

    private boolean isTAtom(String atom) {
        for (String ta : trueAtoms) {
            if (ta.equals(atom)) return true;
        }
        return false;
    }

    private boolean isFAtom(String atom) {
        for (String fa : falseAtoms) {
            if (fa.equals(atom)) return true;
        }
        return false;
    }

}
