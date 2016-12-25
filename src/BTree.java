public class BTree<T extends Comparable<T>> {
    Node<T> root;
    int d, maxCellSize, ptrSize;

    public BTree(int d) {
        this.d = d;
        this.maxCellSize = 2 * d;
        this.ptrSize = 2 * d + 1;
    }

    void split(Node<T> n, T newKey, Node<T> leftPtr, Node<T> rightPtr) {
        // newKey is the bad guy who causes the need of split.
        boolean imDone = false;
        // Iterate through keys and insert it into the correct place.
        for( int i = 0; i < n.getKeys().size(); i++ ) {
            if( newKey.compareTo(n.getKeys().get(i)) < 0 ) {
                n.getKeys().add(i, newKey);
                n.getPtr().set(i, leftPtr);
                n.getPtr().add(i + 1, rightPtr);
                imDone = true;
                break;
            }
        }
        if( !imDone ) {
            n.getKeys().add(newKey);
            n.getPtr().set(n.getPtr().size() - 1, leftPtr);
            n.getPtr().add(rightPtr);
        }

        // Now we have maxCellSize + 1 keys in our node n.
        // So, now we have got a node that has odd number of keys.
        // Which means we have a median.
        int mid = (n.getKeys().size()) / 2;
        T midKey = n.getKeys().get(mid);

        Node<T> left = new Node<>();
        Node<T> right = new Node<>();
        for( int i = 0; i < n.getKeys().size(); i++ ) {
            // Add left node the keys and related pointers until the middle key.
            if( i < mid ) {
                left.getKeys().add(n.getKeys().get(i));
                left.getPtr().add(n.getPtr().get(i));
            } else if( i == mid ) {
                left.getPtr().add(n.getPtr().get(i));
            }
            // After the middle key, add right node the keys after middle key and related pointers.
            else if( i > mid ) {
                right.getKeys().add(n.getKeys().get(i));
                right.getPtr().add(n.getPtr().get(i));
            }
        }
        right.getPtr().add(n.getPtr().get(n.getPtr().size() - 1));

        // Now re-design n in place.
        n.getKeys().clear();
        n.getPtr().clear();

        n.getPtr().add(left);
        n.getKeys().add(midKey);
        n.getPtr().add(right);

        // If n is the root, then nothing to do. Everything is just perfect.
        // If n is not root, then we need to insert n to its parent somehow.
        if( n != root ) {
            // Find the parent of n,
            Node<T> p = getParent(root, midKey);
            // Since our insert method expects a parent node and a key,
            // we need to get parent's parent so that we can insert the key into parent.
            // If parent is not root, then it should have a parent, if it is root then getParent will return the root.
            Node<T> pp = getParent(root, p.getKeys().get(0));
            int index = getParentPtr(n, p);
            p.getPtr().set(index, null);
            insertPrivate(pp, midKey, left, right);
        }
    }

    Node<T> getParent(Node<T> p, T key) {
        if( p == root && root.getKeys().contains(key) )
            return root;

        // For every single key in current p,
        for( int i = 0; i < p.getKeys().size(); i++ ) {
            Node<T> left = p.getPtr().get(i);
            // Isn't it possible for a node to have only one child?
            // Nope, it is not. Because children are born with split.
            if( left != null ) {
                // If any of them is the key we are looking for,
                if( left.getKeys().contains(key) )
                    return p;
                // If left does not contain the key and key is less than left's smallest key, and
                // if the key is between the smallest and greatest of left or
                // if the key is between the greatest of left and its successor(which is n->keys[i])
                else if( !left.getKeys().contains(key) && (
                        key.compareTo(left.getKeys().get(0)) < 0 ||
                                (key.compareTo(left.getKeys().get(0)) > 0 &&
                                        key.compareTo(left.getKeys().get(left.getKeys().size() - 1)) < 0) ||
                                (key.compareTo(left.getKeys().get(left.getKeys().size() - 1)) > 0 &&
                                        key.compareTo(p.getKeys().get(i)) < 0)
                )
                        )
                    // then, go left,
                    return getParent(left, key);
            }
        }
        // If we reach this code segment, then it means we couldn't find the parent yet
        // and we have one more node to look up.
        Node<T> theLastPointer = p.getPtr().get(p.getPtr().size() - 1);
        if( key.compareTo(p.getKeys().get(p.getKeys().size() - 1)) > 0 ) {
            if( theLastPointer != null ) {
                if( theLastPointer.getKeys().contains(key) )
                    return p;
                else
                    return getParent(theLastPointer, key);
            }
        }

        // If none of the above, then no such key.
        return null;
    }

    int getParentPtr(Node<T> n, Node<T> p) {
        // Find n's position in parent p's pointer nodes.
        for( int i = 0; i < p.getPtr().size(); i++ )
            if( p.getPtr().get(i) == n )
                return i;
        return -1;
    }

    public void insert(T key) {
        insertPrivate(root, key, null, null);
    }

    void insertAux(Node<T> n, T key, Node<T> leftPtr, Node<T> rightPtr) {
        boolean imDone = false;
        // Iterate through the node,
        for( int i = 0; i < n.getKeys().size(); i++ ) {
            // If key is less than n->key[i],
            if( key.compareTo(n.getKeys().get(i)) < 0 ) {
                // If it does not point to a node which has a smaller key,
                if( n.getPtr().get(i) == null ) {
                    // insert the key here with its pointers.
                    n.getKeys().add(i, key);
                    n.getPtr().add(i, leftPtr);
                    n.getPtr().set(i + 1, rightPtr);
                    imDone = true;
                    break;
                }
                // Else, we need get into that pointer.
                else {
                    insertPrivate(n.getPtr().get(i), key, leftPtr, rightPtr);
                    imDone = true;
                    break;
                }
            }
            // Else if the key already exists,
            else if( key.compareTo(n.getKeys().get(i)) == 0 ) {
                System.out.println("The key " + key + " already exists in the tree.");
                imDone = true;
                break;
            }
        }
        // If the iteration is over and we could not find the correct place,
        // then we need to append key.
        if( !imDone ) {
            // If the last pointer points out somewhere,
            if( n.getPtr().get(n.getPtr().size() - 1) != null ) {
                // then go for that node.
                insertPrivate(n.getPtr().get(n.getPtr().size() - 1), key, leftPtr, rightPtr);
            }
            // Else, we append the key with its pointers.
            else {
                n.getPtr().set(n.getPtr().size() - 1, leftPtr);
                n.getKeys().add(key);
                n.getPtr().add(rightPtr);
            }
        }
    }

    void insertPrivate(Node<T> n, T key, Node<T> leftPtr, Node<T> rightPtr) {
        // If root is null, then the tree is empty.
        // Let's add root first.
        if( root == null ) {
            root = new Node<T>();
            root.getPtr().add(leftPtr);
            root.getKeys().add(key);
            root.getPtr().add(rightPtr);
        }
        // Else, continue inserting,
        else {
            // If n's size is less than maximum cell size,
            if( n.getKeys().size() < maxCellSize ) {
                insertAux(n, key, leftPtr, rightPtr);
            }
            // Else if, the node is full,
            else if( n.getKeys().size() == maxCellSize ) {
                boolean imDone = false;
                // Iterate through the keys of it.
                for( int i = 0; i < n.getKeys().size(); i++ ) {
                    // If the key to insert is smaller than n->key[i],
                    if( key.compareTo(n.getKeys().get(i)) < 0 ) {
                        // Do we need to get deeper,
                        if( n.getPtr().get(i) != null ) {
                            insertPrivate(n.getPtr().get(i), key, leftPtr, rightPtr);
                            imDone = true;
                            break;
                        }
                        // Else there is no room for our lonely key,
                        else {
                            // split.
                            split(n, key, leftPtr, rightPtr);
                            imDone = true;
                            break;
                        }
                    }
                    // If we already have that key,
                    else if( key.compareTo(n.getKeys().get(i)) == 0 ) {
                        System.out.println("The key " + key + " already exists in the tree.");
                    }
                }
                // If the iteration over and we could not place our key,
                if( !imDone ) {
                    // Then, we need to append that key if we can,
                    if( n.getPtr().get(n.getPtr().size() - 1) != null )
                        insertPrivate(n.getPtr().get(n.getPtr().size() - 1), key, leftPtr, rightPtr);
                        // whereas there is no room, we split.
                    else
                        split(n, key, leftPtr, rightPtr);
                }
            }
        }
    }

    public void printTree() {
        if( root != null )
            printTreePrivate(root);
        else
            System.out.println("The tree is empty.");
    }

    void printTreePrivate(Node<T> n) {
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

    public boolean search(T key) {
        Node<T> n = getParent(root, key);
        if( n == null ) {
            System.out.println("The key " + key + " does not exist in the tree.");
            return false;
        }

        System.out.println("The key " + key + " is found and its parent node has these keys:");
        for( T k :
                n.getKeys() ) {
            System.out.print(" ~" + k);
        }
        System.out.println();
        return true;
    }
}
