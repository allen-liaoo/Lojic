package lojic.parser;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author AlienIdeology
 */
public class ParseList<E> extends ArrayList<E> {

    private int index = -1;

    public ParseList(int initialCapacity) {
        super(initialCapacity);
    }

    public ParseList() {
    }

    public ParseList(Collection<? extends E> c) {
        super(c);
    }

    public E next() {
        index++;
        return get(index);
    }

    public E peek() {
        return get(index++);
    }

    public boolean hasNext() {
        return this.size() > index + 1;
    }

}
