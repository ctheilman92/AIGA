package GA;

import com.sun.org.apache.bcel.internal.generic.POP;
import javafx.scene.Parent;

import javax.print.DocFlavor;
import java.util.*;

/*



 */

public class GA {

    boolean GOAL_FOUND;
    int SIZE;                           //The length of columns in rows in the board states such that the board is [ SIZE * SIZE ]
    int POPULATION_SIZE;                //population size (BOARD_SIZE^2)^3 to gaurentee a large enough set of solvable states are in population
    ArrayList<Chromosome> POPULATION;   //a set of state configurations possible solvable configurations

    public GA() { }

    public GA(int N) {
        this.GOAL_FOUND = false;
        this.SIZE = N;
        this.POPULATION_SIZE = 50;

        System.out.println(SIZE + " ======== " + POPULATION_SIZE);
    }

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
            while (!c.IsStateSolvable() || !IsUniqueChromosome(c)) {
                c.SetState();
            }

            POPULATION.add(c);
        }

        //check population for goal state
        GOAL_FOUND = CheckIfGoalFound(POPULATION);
    }

    public boolean CheckIfGoalFound(ArrayList<Chromosome> Pool) {

        for (Chromosome c : Pool) {
            if (c.IsGoal()) {
                return true;
            }
        }

        return false;
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

        Random gen = new Random();
        float SumFitness = GetSumFitness();

        //for every chromosome set a fitness-proportionate to the population
        POPULATION.forEach(c -> c.SetProbabilityGrade(SumFitness));

        float SumProbabilities = 0.0f;
        for (Chromosome c : POPULATION) {
            SumProbabilities += c.GetProbabilityGrade();
        }

        float threshold = (SumProbabilities / POPULATION_SIZE);

        System.out.println("SUM OF ALL FITNESS PROBABILITIES: " + SumProbabilities);
        System.out.println("THRESHOLD: " + threshold);


        //get all parents that have fitness probs >= the threshold
        ArrayList<Chromosome> WeakerSolutions = new ArrayList<Chromosome>();
        ArrayList<Chromosome> ChildPool = new ArrayList<Chromosome>();

        POPULATION.forEach((c) -> { if (c.GetProbabilityGrade() < threshold) { WeakerSolutions.add(c); }});
        WeakerSolutions.forEach((c) -> POPULATION.remove(c));


        //use the parent solutions as a breeding pool
        //create a new child to replace all the weaker solutions
        //@param POPULATION_SIZE is always 50 - we deleted the weaker solutions in hte previous step
        int Index1;
        int Index2;
        Chromosome p1, p2;

        for (int i = POPULATION.size(); i < POPULATION_SIZE; i++) {
            //crossover & mutate
            Index1 = gen.nextInt(POPULATION.size());
            p1 = POPULATION.get(Index1);

            Index2 = gen.nextInt(POPULATION.size());
            while (Index1 == Index2) { Index2 = gen.nextInt(POPULATION.size()); }


            p2 = POPULATION.get(Index2);

            Chromosome Child = CrossOver(p1, p2);
            ChildPool.add(Child);

        }

        //check to see if any children in the pool are goal states
        if (!CheckIfGoalFound(ChildPool)) {

            //for mutation we will try to dynamically pick the rate for a random number between 0 and the total of children produced in pool
            System.out.println("child pool size: " + ChildPool.size());
            int mxr = gen.nextInt((int)(ChildPool.size() * .2));
            System.out.println("Mutation Rate: " + mxr);

            int MutationIndex;
            for (int i = 1; i <= mxr; i++) {
                MutationIndex = gen.nextInt(ChildPool.size());
                Chromosome mChild = Mutate(ChildPool.get(MutationIndex));
                ChildPool.set(MutationIndex, mChild);
            }
        }

        ChildPool.forEach((c) -> {
            if (c.IsStateSolvable() && IsUniqueChromosome(c)) {
                POPULATION.add(c);
            }
            else {
                //select weaker state to put back into population
                int weakIndex = gen.nextInt(WeakerSolutions.size());
                Chromosome weakc = WeakerSolutions.get(weakIndex);

                while (!weakc.IsStateSolvable() || !IsUniqueChromosome(weakc)) {
                    weakIndex = gen.nextInt(WeakerSolutions.size());
                    weakc = WeakerSolutions.get(weakIndex);
                }

                POPULATION.add(weakc);
            }
        });
    }


    public Chromosome CrossOver(Chromosome p1, Chromosome p2) {
        Random gen = new Random();
        int cxval = 0;
        while (cxval == 0) { cxval = gen.nextInt(p1.GetState().size()); }

        ArrayList<Integer> ChildState = new ArrayList<>();

        for (int i = 0; i < cxval; i ++) {
            int val = p1.GetState().get(i);
            ChildState.add(val);
        }

        for (int i = cxval; i < p2.GetState().size(); i++) {

            //if we already grabbed this value from p1, then just get indexed value from p1..
            int val = p2.GetState().get(i);
            if (ChildState.contains(val)) {
                val = p1.GetState().get(i);
            }

            ChildState.add(val);
        }

        //assume we have a new state of size N
        //here we are
        return new Chromosome(ChildState.size(), ChildState);
    }


    public Chromosome Mutate(Chromosome c) {
        Random gen = new Random();
        int c1 = gen.nextInt(c.GetState().size());
        int c2 = c1;

        while (c2 == c1) {
            c2 = gen.nextInt(c.GetState().size());
        }

        //System.out.println("chromosome before mutation: " + c.GetState());


        ArrayList<Integer> swapstate = c.GetState();
        Collections.swap(swapstate, c1, c2);
        c.SetState(swapstate);

        //System.out.println("chromosome after mutating points " + c1 + " and " + c2 + " :-> " + c.GetState());
        return c;
    }

}
