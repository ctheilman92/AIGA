import GA.*;

public class Main {

    public static int GENERATION_COUNT;

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("[ ERR ]: no input detected");
            System.exit(1);
        }

        //this is [SIZE * SIZE] board => there are SIZE^2 - 1
        int SIZE = Integer.parseInt(args[0]);
        System.out.println("[ BOARD SIZE: " + SIZE + " * " + SIZE + " ]");
        System.out.println("===============================================================");

        GA ga = new GA(SIZE);

    }
}
