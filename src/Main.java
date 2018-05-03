import GA.*;

public class Main {

    public static int GENERATION_COUNT;

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("[ ERR ]: LACK OF NECESSARY ARGUMENTS");
            System.exit(1);
        }

        long APPLICATION_START_TIME = System.currentTimeMillis();

        //this is [SIZE * SIZE] board => there are SIZE^2 - 1
        int SIZE = Integer.parseInt(args[0]);
        int POP = Integer.parseInt(args[1]);
        System.out.println("[ BOARD SIZE: " + SIZE + " * " + SIZE + " ]");
        System.out.println("===============================================================");

        //start initial generation
        int Generations = 1;
        GA ga = new GA(SIZE, POP);

        ga.RandomPopulation();
        while (!ga.IsGoalFound()) {

            ga.RouletteSelect();
            Generations++;
            System.out.println("\n"+(char)27 + "[31m" + "GEN: " + Generations);
        }

        System.out.println("GOAL STATE FOUND AT GENERATION: " + Generations);
        long APPLICATION_STOP_TIME = System.currentTimeMillis();

        System.out.println("EXECUTION TIME: " + (APPLICATION_STOP_TIME - APPLICATION_START_TIME) + "/Ms");
    }
}
