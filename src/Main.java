import java.util.Scanner;

public class Main {

    private static void menu() {
        System.out.println("\nAVL Tree Java Implementation");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("| [1] Print Tree           |");
        System.out.println("| [2] Insert key           |");
        System.out.println("| [0] Exit                 |");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.print(">>> ");
    }
    public static void main(String[] args) {
        BTree<Integer> tree = new BTree<>(2);
        Integer[] nums = {80, 50, 100, 90, 60, 65, 70, 75, 55, 64, 51};

        for( Integer i :
                nums ) {
            tree.insert(i);
        }
        tree.printTree();

        Scanner reader = new Scanner(System.in);
        menu();
        int select = reader.nextInt();
        while(true) {
            if( select == 1 ) {
                System.out.println("\n======Print Tree======");
                tree.printTree();
                System.out.println("=======================");
                menu();
                select = reader.nextInt();
            } else if( select == 2 ) {
                System.out.println("\n======Insert Operation======");
                System.out.println("| [-1] Exit                |\n");
                System.out.print("Insert a key to tree: ");
                int key = reader.nextInt();
                while(key != -1) {
                    tree.insert(key);
                    System.out.println("=======================");
                    System.out.print("Insert a key to tree: ");
                    key = reader.nextInt();
                }
                menu();
                select = reader.nextInt();
            } else if( select == 0 )
                System.exit(0);
        }

    }
}
