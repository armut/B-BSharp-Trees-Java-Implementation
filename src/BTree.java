public class BTree<T extends Comparable<T>> {
    Node<T> root;
    private int d, maxCellSize, ptrSize;

    public BTree(int d) {
        //TODO: Aynı key'in tekrar eklenmemesi.
        this.d = d;
        this.maxCellSize = 2 * d;
        this.ptrSize = 2 * d + 1;
    }

    private void split(Node<T> n, T newKey) {
        // newKey is the bad guy who causes the need of split.
        //TODO: Yukarıda yer varsa.
        boolean imDone = false;
        for( int i = 0; i < n.getKeys().size(); i++ ) {
            if( newKey.compareTo(n.getKeys().get(i)) < 0 ) {
                n.getKeys().add(i, newKey);
                //TODO: ptr kontrolü
                imDone = true;
                break;
            }
        }
        if( !imDone )
            n.getKeys().add(newKey);

        int mid = (n.getKeys().size()) / 2;
        T midKey = n.getKeys().get(mid);

        Node<T> left = new Node<>();
        Node<T> right = new Node<>();
        for( int i = 0; i < n.getKeys().size(); i++ ) {
            if( i < mid ) {
                left.getKeys().add(n.getKeys().get(i));
                left.getPtr().add(n.getPtr().get(i));
            } else if( i == mid ) {
                left.getPtr().add(n.getPtr().get(i));
            } else if( i > mid ) {
                right.getKeys().add(n.getKeys().get(i));
                right.getPtr().add(n.getPtr().get(i));
            }
        }
        right.getPtr().add(null);
        
        n.getKeys().clear();
        n.getPtr().clear();

        n.getPtr().add(left);
        n.getKeys().add(midKey);
        n.getPtr().add(right);

        if( n != root ) {
            Node<T> p = getParent(root, midKey);
            if( p != root ) {
                Node<T> pp = getParent(p, midKey);
                insertPrivate(pp, midKey, left, right);
            } else {
                int index = getParentPtr(n, p);
                p.getPtr().set(index, null);
                insertPrivate(root, midKey, left, right);
            }
        }
    }

    private Node<T> getParent(Node<T> p, T key) {
        if( p == root && root.getKeys().contains(key) )
            return root;

        // For every single key in current p,
        for( int i = 0; i < p.getKeys().size(); i++ ) {
            Node<T> left = p.getPtr().get(i);
            Node<T> right = p.getPtr().get(i + 1);

            if( left != null && right != null ) {
                if( left.getKeys().contains(key) || right.getKeys().contains(key) )
                    return p;
                else if( !left.getKeys().contains(key) && key.compareTo(left.getKeys().get(0)) < 0 )
                    return getParent(left, key);
                else if( !right.getKeys().contains(key) && key.compareTo(right.getKeys().get(0)) < 0 )
                    return getParent(right, key);
            }
        }
        // If none of the above, then no such key.
        return null;
    }

    private int getParentPtr(Node<T> n, Node<T> p) {
        // Find n's position in parent p's pointer nodes.
        for( int i = 0; i < p.getPtr().size(); i++ )
            if(p.getPtr().get(i) == n)
                return i;
        return -1;
    }

    public void insert(T key) {
        insertPrivate(root, key, null, null);
    }

    private void insertPrivate(Node<T> n, T key, Node<T> leftPtr, Node<T> rightPtr) {
        boolean imDone = false;
        if( root == null ) {
            root = new Node<T>();
            root.getPtr().add(leftPtr);
            root.getKeys().add(key);
            root.getPtr().add(rightPtr);
        } else {
            if( n.getKeys().size() < maxCellSize ) {
                int tmp = 0;
                for( int i = 0; i < n.getKeys().size(); i++ ) {
                    if( key.compareTo(n.getKeys().get(i)) < 0 ) {
                        if( n.getPtr().get(i) == null ) {
                            n.getKeys().add(i, key);
                            n.getPtr().add(i, leftPtr);
                            n.getPtr().set(i + 1, rightPtr);
                            imDone = true;
                            break;
                        } else {
                            insertPrivate(n.getPtr().get(i), key, leftPtr, rightPtr);
                            imDone = true;
                            break;
                        }
                    } else if( key.compareTo(n.getKeys().get(i)) > 0 ) {
                        if( n.getPtr().get(i + 1) == null ) {
                            tmp = i + 1;
                        } else {
                            insertPrivate(n.getPtr().get(i + 1), key, leftPtr, rightPtr);
                            imDone = true;
                            break;
                        }
                    }
                }
                if( !imDone ) {
                    n.getPtr().set(tmp, leftPtr);
                    n.getKeys().add(key);
                    n.getPtr().add(rightPtr);
                }
            } else if( n.getKeys().size() == maxCellSize ) {
                split(n, key);
            }
        }
    }

    public void printTree() {
        if( root != null )
            printTreePrivate(root);
        else
            System.out.println("The tree is empty.");
    }

    private void printTreePrivate(Node<T> n) {
        for( int i = 0; i < n.getPtr().size(); i++ ) {
            if( n.getPtr().get(i) != null )
                printTreePrivate(n.getPtr().get(i));
        }
        String output = "";
        for( T key :
                n.getKeys() ) {
            output += " ~ " + String.valueOf(key);
        }
        System.out.println(output);
    }
}
