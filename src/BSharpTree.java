
public class BSharpTree<T extends Comparable<T>> extends BTree<T> {

    public BSharpTree(int d) {
        super(d);
    }

    private Node<T> generateNode(Node<T> n, Node<T> sibling, T newKey, T parentKey, boolean isLeft) {
        // Gets all keys that are related to distributing together in a one big node.
        Node<T> newNode = new Node<>();
        if( isLeft ) {
            newNode.getKeys().addAll(n.getKeys());
            newNode.getKeys().add(parentKey);
            newNode.getKeys().addAll(sibling.getKeys());
        } else {
            newNode.getKeys().addAll(sibling.getKeys());
            newNode.getKeys().add(parentKey);
            newNode.getKeys().addAll(n.getKeys());
        }

        boolean imDone = false;
        for( int i = 0; i < newNode.getKeys().size(); i++ ) {
            if( newKey.compareTo(newNode.getKeys().get(i)) < 0 ) {
                newNode.getKeys().add(i, newKey);
                imDone = true;
                break;
            }
        }
        if( !imDone )
            newNode.getKeys().add(newKey);
        return newNode;
    }

    private void redistribute(Node<T> n, T newKey, Node<T> leftPtr, Node<T> rightPtr) {
        //TODO: Pointer'lar ne olacak?
        if( n == root) {
            split(n, newKey, leftPtr, rightPtr);
        } else {
            Node<T> parentNode = getParent(root, n.getKeys().get(0));
            int parentIndex = getParentPtr(n, parentNode);
            boolean isLeft = false;
            try {
                if( newKey.compareTo(parentNode.getKeys().get(parentIndex)) < 0 )
                    isLeft = true;
            } catch (IndexOutOfBoundsException e) {
                isLeft = false;
            }
            T parentKey = isLeft ? parentNode.getKeys().get(parentIndex) :
                    parentNode.getKeys().get(parentIndex - 1);
            Node<T> sibling = getSibling(n, parentKey);

            if( sibling.getKeys().size() < maxCellSize ) {
                Node<T> tmpNode = generateNode(n, sibling, newKey, parentKey, isLeft);
                int comparisonRecord = ((tmpNode.getKeys().size() + 1) / 2) - 1;
                if( isLeft )
                    parentNode.getKeys().set(parentIndex, tmpNode.getKeys().get(comparisonRecord));
                else
                    parentNode.getKeys().set(parentIndex - 1, tmpNode.getKeys().get(comparisonRecord));

                Node<T> left = new Node<>();
                Node<T> right = new Node<>();
                for( int i = 0; i < tmpNode.getKeys().size(); i++ ) {
                    if( i < comparisonRecord ) {
                        left.getKeys().add(tmpNode.getKeys().get(i));
                        left.getPtr().add(null);
                    }
                    else if( i > comparisonRecord ) {
                        right.getKeys().add(tmpNode.getKeys().get(i));
                        right.getPtr().add(null);
                    }
                }
                left.getPtr().add(null);
                right.getPtr().add(null);


                if( isLeft ) {
                    parentNode.getPtr().set(parentIndex, left);
                    parentNode.getPtr().set(parentIndex + 1, right);
                } else {
                    parentNode.getPtr().set(parentIndex - 1, left);
                    parentNode.getPtr().set(parentIndex, right);
                }
            }
            else if( parentNode.getKeys().size() < maxCellSize ) {
                Node<T> tmpNode = generateNode(n, sibling, newKey, parentKey, isLeft);

                int mid1 = ((tmpNode.getKeys().size() + 1) / 3) - 1;
                int mid2 = mid1 + (tmpNode.getKeys().size() - mid1) / 2;

                Node<T> left = new Node<>();
                Node<T> middle = new Node<>();
                Node<T> right = new Node<>();
                for( int i = 0; i < tmpNode.getKeys().size(); i++ ) {
                    if( i < mid1 ) {
                        left.getKeys().add(tmpNode.getKeys().get(i));
                        left.getPtr().add(null);
                    } else if( i == mid1 ) {
                        if( isLeft )
                            parentNode.getKeys().set(parentIndex, tmpNode.getKeys().get(mid1));
                        else
                            parentNode.getKeys().set(parentIndex - 1, tmpNode.getKeys().get(mid1));
                        left.getPtr().add(null);
                        middle.getPtr().add(null);
                    } else if( i > mid1 && i < mid2 ) {
                        middle.getKeys().add(tmpNode.getKeys().get(i));
                        middle.getPtr().add(null);
                    } else if( i == mid2 ) {
                        if( isLeft ) {
                            parentNode.getKeys().add(parentIndex + 1, tmpNode.getKeys().get(mid2));
                        } else {
                            parentNode.getKeys().add(parentIndex, tmpNode.getKeys().get(mid2));
                        }
                        right.getPtr().add(null);
                    }
                    else {
                        right.getKeys().add(tmpNode.getKeys().get(i));
                        right.getPtr().add(null);
                    }

                    if( isLeft ) {
                        parentNode.getPtr().set(parentIndex, left);
                        parentNode.getPtr().set(parentIndex + 1, middle);
                        try {
                            parentNode.getPtr().set(parentIndex + 2, right);
                        } catch (IndexOutOfBoundsException e) {
                            parentNode.getPtr().add(right);
                        }
                    } else {
                        parentNode.getPtr().set(parentIndex - 1, left);
                        parentNode.getPtr().set(parentIndex, middle);
                        try {
                            parentNode.getPtr().set(parentIndex + 1, right);
                        } catch (IndexOutOfBoundsException e) {
                            parentNode.getPtr().add(right);
                        }
                    }
                }
            } else {
                //TODO: Bu kısımda ne olacak?
                split(n, newKey, leftPtr, rightPtr);
            }
        }
    }

    private Node<T> getSibling(Node<T> n, T parentKey) {
        //TODO: Ortada olan bir node için sibling hangisidir?
        Node<T> p = getParent(root, n.getKeys().get(0));
        int pointerIndex = getParentPtr(n, p);
        Node<T> sibling;
        if( n.getKeys().get(n.getKeys().size() - 1).compareTo(parentKey) < 0 )
            // which means a left child,
            sibling = p.getPtr().get(pointerIndex + 1);
        else
            //TODO: Burada bir böcek var.
            sibling = p.getPtr().get(pointerIndex - 1);
        return sibling;
    }

    @Override
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
                            redistribute(n, key, leftPtr, rightPtr);
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
                    else {
                        redistribute(n, key, leftPtr, rightPtr);
                    }
                }
            }
        }
    }

}
