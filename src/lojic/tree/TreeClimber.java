package lojic.tree;

import lojic.nodes.Node;
import lojic.nodes.truthapts.Formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author AlienIdeology
 *
 * A NodeTree iterator that utilizes {@link FunctionalInterface}
 * The climber stores a cache list of the nodes in the tree, which can be filtered and outputted.
 */
public class TreeClimber {

    private final Node root;
    private final List<Node> list;
    private List<Node> cacheList;

    /**
     * Constructor of TreeClimber
     *
     * @param root The root node
     */
    TreeClimber(Node root) {
        this.root = root;
        this.list = new ArrayList<>();
        forEach(list::add);
        this.cacheList = new ArrayList<>(list);
    }

    /**
     * Executes a {@link Consumer} action for each node of the NodeTree
     *
     * @param action The action to be executed
     * @throws NullPointerException if the parameter is null
     */
    public void forEach(Consumer<Node> action) {
        Objects.requireNonNull(action, "Cannot iterate a NodeTree with a null Consumer!");
        forEach(root, action);
    }

    /**
     * Filter the list with a {@link Predicate} condition
     *
     * @param filter The condition.
     *               If an element does not satisfy the condition, it is removed from the cache list
     * @return This climber for method chaining
     * @throws NullPointerException if the parameter is null
     */
    public TreeClimber filter(Predicate<Node> filter) {
        Objects.requireNonNull(filter, "Cannot filter a NodeTree with a null Predicate!");
        forEach(n -> {
            if (!filter.test(n)) {
                cacheList.remove(n);
            }
        });
        return this;
    }

    /**
     * Filter the list with a {@link BiPredicate} condition
     *
     * @param filter The condition, with the first parameter being the list which this method removes elements from
     *               If an element does not satisfy the condition, it is removed from the cache list
     * @return This climber for method chaining
     * @throws NullPointerException if the parameter is null
     */
    public TreeClimber filter(BiPredicate<List<Node>, Node> filter) {
        Objects.requireNonNull(filter, "Cannot filter a NodeTree with a null BiPredicate!");
        forEach(n -> {
            if (!filter.test(cacheList, n)) cacheList.remove(n);
        });
        return this;
    }

    /**
     * Resets this tree climber and remove previous filters
     *
     * @return This climber for method chaining
     */
    public TreeClimber reset() {
        this.cacheList = new ArrayList<>(list);
        return this;
    }

    /**
     * Get the list that is filtered
     * This automatically {@link #reset()} the cache list
     *
     * @return The list that is filtered
     */
    public List<Node> toList() {
        ArrayList<Node> temp = new ArrayList<>(cacheList);
        reset();
        return temp;
    }

    private void forEach(Node node, Consumer<Node> action) {
        if (node instanceof Formula) {
            for (Node n : ((Formula) node).getChildren()) {
                forEach(n, action);
            }
        }
        action.accept(node);
    }

}
