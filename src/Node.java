import java.util.ArrayList;
import java.util.List;

public class Node<T extends Comparable<T>> {
    private List<T> keys = new ArrayList<>();
    private List<Node<T>> ptr = new ArrayList<>();

    public List<Node<T>> getPtr() {
        return ptr;
    }

    public List<T> getKeys() {
        return keys;
    }
}
