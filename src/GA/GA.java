package GA;

import com.sun.org.apache.bcel.internal.generic.POP;
import com.sun.tools.javac.comp.Check;
import javafx.scene.Parent;
import java.util.*;

/*



 */

public class GA {

    //region ANSI_COLOR OUTPUT
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    //endregion

    //region MEMBERS
    boolean GOAL_FOUND;
    int SIZE;                           //The length of columns in rows in the board states such that the board is [ SIZE * SIZE ]
    int POPULATION_SIZE;                //population size (BOARD_SIZE^2)^3 to gaurentee a large enough set of solvable states are in population
    float CurrentGenMaxFitnessProb;
    float CurrentGenMinFitnessProb;
    ArrayList<Chromosome> POPULATION;   //a set of state configurations possible solvable configurations
    final double mkx = .5;
    //endregion

    //region CONSTRUCTORS
    public GA() { }

    public GA(int N, int P) {
        CurrentGenMaxFitnessProb = 0;
        CurrentGenMinFitnessProb = 0;

        this.GOAL_FOUND = false;
        this.SIZE = N;
        this.POPULATION_SIZE = P;

        System.out.println(SIZE + " ======== " + POPULATION_SIZE);
    }
    //endregion

    //region METHODS
    public String PrintGeneration() {

        //this is just a formatted output
        //3 states per line
        int lineCount = 0;

        StringBuilder sb = new StringBuilder();

        for (Chromosome c : POPULATION) {
            sb.append(c.GetState().toString() + " , ");
            lineCount++;

            if (lineCount % 3 == 0)
                sb.append("\n-----------------------------------------------------------------------------------------\n");
        }

        return sb.toString();
    }

    public void RandomPopulation() {

        POPULATION = new ArrayList<Chromosome>();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            Chromosome c = new Chromosome(SIZE);

            //generate each chromosome configuration such that each solution is solvable AND unique to our population
            c.SetState();
//            while (!c.IsStateSolvable() || !IsUniqueChromosome(c)) {
//                c.SetState();
//            }

            while (!IsUniqueChromosome(c)) {
                c.SetState();
            }

            POPULATION.add(c);
        }

        //check population for goal state
        GOAL_FOUND = CheckIfGoalFound(POPULATION);
    }

    public boolean CheckIfGoalFound(ArrayList<Chromosome> Pool) {

        boolean didFindGoal = false;
        for (Chromosome c : Pool) {

            float goalfitness = c.GetState().size();//-> if using byStepComparator

            if (c.GetState().toString().equals(c.GetGoal().toString())) {
                System.out.println("\n~~~~~~~~~~\nSTATE FOUND GOAL: " + c.GetState() + "----> S(f): " + c.GetFitnessGrade() + "\n~~~~~~~~~~~");
                didFindGoal = true;
                GOAL_FOUND = true;
            }

            return didFindGoal;
        }


        return didFindGoal;
    }

    public boolean IsGoalFound() { return GOAL_FOUND; }

    public boolean IsUniqueChromosome(Chromosome c) {

        String cstate = c.GetState().toString();
        for (Chromosome compc : POPULATION) {
            String compstate = compc.GetState().toString();

            if (cstate.equals(compstate))
                return false;
        }

        return true;
    }

    public ArrayList<Chromosome> GetPopulation() { return POPULATION; }

    public String PrintProbabilities() {
        StringBuilder sb = new StringBuilder();

        for(Chromosome c : POPULATION) {
            String p = String.valueOf(c.GetProbabilityGrade());
            sb.append(p);
            sb.append('\n');
        }

        return sb.toString();
    }

    public float GetSumFitness() {
        float sum = 0.0f;
        for (Chromosome c : POPULATION) {
            sum += c.GetFitnessGrade();
        }

        return sum;
    }

    /*
        the idea is to select the parents above the mean fitness probability of the set
        we want to replace the weaker solutions with the children of the states.
    */
    public void RouletteSelect() {
        POPULATION.sort(Comparator.comparingDouble(o -> (double) o.GetFitnessGrade()));
        //POPULATION.forEach(c -> System.out.println(c.GetState() + " ----> " + c.GetFitnessGrade()));

        Random gen = new Random();
        int Index1 = gen.nextInt(POPULATION.size());
        System.out.println("==" + Index1);
        int Index2 = gen.nextInt(POPULATION.size());
        System.out.println("==" + Index2);

        ArrayList<Chromosome> Parents = new ArrayList<Chromosome>();
        Chromosome Child = CrossOver(POPULATION.get(Index1), POPULATION.get(Index2));
        Parents.add(Child);

        if (!this.CheckIfGoalFound(Parents)) {

            //start mutation
            double rand = Math.random();
            if (rand >= mkx) {
                Chromosome MXChild = Child;
                Mutate(MXChild);

                //if mutated child is superior replace child with this then compare with worst gene in pool
                if (MXChild.GetFitnessGrade() >= Child.GetFitnessGrade())
                    Child = MXChild;
            }
        }

        if (Child.GetFitnessGrade() > POPULATION.get(gen.nextInt(POPULATION.size())).GetFitnessGrade())
            POPULATION.set(0,Child);
    }


    public Chromosome CrossOver(Chromosome p1, Chromosome p2) {

        Random gen = new Random();
        int cxval = 0;
        while (cxval == 0) { cxval = gen.nextInt(p1.GetState().size()); }

        ArrayList<Integer> ChildState = new ArrayList<>();
//        System.out.println("CROSSOVER " + cxval + " ----> p1: " + p1.GetState());
//        System.out.println("CROSSOVER " + cxval + " ----> p2: " + p2.GetState());

        for (int i = 0; i < cxval; i ++) {
            int val = p1.GetState().get(i);
            ChildState.add(val);
        }

        for (int i = cxval; i < p2.GetState().size(); i++) {

            //if we already grabbed this value from p1, then just get indexed value from p1..
            int val = p2.GetState().get(i);

            if (ChildState.contains(val)) {
                for (int j = 0; j < p2.GetState().size(); j++) {
                    if (!ChildState.contains(j)) {
                        val = j;
                        continue;
                    }
                }
            }

            ChildState.add(val);
        }

        System.out.println("CROSSOVER " + cxval + "\t-> child: " + ChildState);
        return new Chromosome(ChildState.size(), ChildState);

    }


    //we are mutating the state inside of here
    public void Mutate(Chromosome c) {
        Random gen = new Random();
        int c1 = gen.nextInt(c.GetState().size());
        int c2 = c1;

        while (c2 == c1) {
            c2 = gen.nextInt(c.GetState().size());
        }
//        System.out.println("chromosome before mutation: " + c.GetState());

        ArrayList<Integer> swapstate = c.GetState();
        Collections.swap(swapstate, c1, c2);
        c.SetState(swapstate);

        System.out.println("MUTATION(" + c1 + ", " + c2 + ") -> Child: " + c.GetState());
    }
    //endregion
}
