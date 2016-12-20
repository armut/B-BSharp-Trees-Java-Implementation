
public class Main {
    public static void main(String[] args) {
        BTree<Integer> tree = new BTree<>(2);
        Integer[] nums = {5, 6, 7, 8, 9, 4, 10, 11, 12};

        for( Integer i :
                nums ) {
            tree.insert(i);
        }
        tree.printTree();
    }
}
