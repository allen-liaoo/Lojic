package lojic.parser.token;

import java.util.ArrayList;

/**
 * @author AlienIdeology
 */
public class TokenList extends ArrayList<Token> {

    private int index;

    public TokenList() {
        this.index = -1;
    }

    public Token next() {
        index++;
        return get(index);
    }

    public Token peek() {
        return get(index++);
    }

    public boolean hasNext() {
        return this.size() > index + 1;
    }

}
