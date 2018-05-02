package GA;

import com.sun.org.apache.bcel.internal.generic.POP;
import com.sun.tools.javac.comp.Check;
import javafx.scene.Parent;
import java.util.*;

/*



 */

public class GA {

    boolean GOAL_FOUND;
    int SIZE;                           //The length of columns in rows in the board states such that the board is [ SIZE * SIZE ]
    int POPULATION_SIZE;                //population size (BOARD_SIZE^2)^3 to gaurentee a large enough set of solvable states are in population
    float CurrentGenMaxFitnessProb;
    float CurrentGenMinFitnessProb;
    ArrayList<Chromosome> POPULATION;   //a set of state configurations possible solvable configurations
    final double mkx = .5;

    public GA() { }

    public GA(int N, int P) {
        CurrentGenMaxFitnessProb = 0;
        CurrentGenMinFitnessProb = 0;

        this.GOAL_FOUND = false;
        this.SIZE = N;
        this.POPULATION_SIZE = P;

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

            float goalfitness = 0.0f;
//            for (int i = 1; i <= c.GetState().size(); i++) {
//                goalfitness += Math.pow(i, 2); //-> if using byValueMultiplier fitness
//            }


            goalfitness = c.GetState().size();//-> if using byStepComparator

            if (c.GetState().toString().equals(c.GetGoal().toString())) {
                System.out.println("STATE FOUND GOAL: " + c.GetState() + "----> S(f): " + c.GetFitnessGrade() + " vs G(f): " + goalfitness);
                didFindGoal = true;
                GOAL_FOUND = true;
            }
//            else {
//                System.out.println("STATE NOT FOUND GOAL: " + c.GetState() + "----> S(f): " + c.GetFitnessGrade() + " vs G(f): " + goalfitness);
//            }


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


        POPULATION.sort((o1, o2) -> Double.compare((double) o1.GetFitnessGrade(), (double) o2.GetFitnessGrade()));
        //POPULATION.forEach(c -> System.out.println(c.GetState() + " ----> " + c.GetFitnessGrade()));


        Random gen = new Random();
        //float SumFitness = GetSumFitness();
//        double kmx = (float)Math.random();

        //for every chromosome set a fitness-proportionate to the population
        //POPULATION.forEach(c -> c.SetProbabilityGrade(SumFitness));
//


//        float threshold = (SumFitness / POPULATION_SIZE);
//        System.out.println("THRESHOLD: " + threshold);
//
//        float cxr = gen.nextFloat() * (this.CurrentGenMaxFitnessProb - this.CurrentGenMinFitnessProb) + this.CurrentGenMinFitnessProb;

        //float cxr = gen.nextFloat() * (((this.CurrentGenMaxFitnessProb/SumFitness) * 100) - ((this.CurrentGenMinFitnessProb/SumFitness) * 100)) + ((this.CurrentGenMinFitnessProb/SumFitness) * 100);
        //System.out.println("PROBABILITY THRESHOLD: " + cxr);

//        //get all parents that have fitness probs >= the threshold
//        ArrayList<Chromosome> WeakerSolutions = new ArrayList<Chromosome>();
//        ArrayList<Chromosome> ChildPool = new ArrayList<Chromosome>();
////
//        POPULATION.forEach((c) -> { if (c.GetFitnessGrade() < cxr) { WeakerSolutions.add(c); }});
//        WeakerSolutions.forEach((c) -> POPULATION.remove(c));
//        System.out.println("POPULATION MATING POOL: -> " + POPULATION.size());
////
//        int breeding = ((POPULATION.size()/2) % 2 == 0) ? (POPULATION.size()/2) : ((POPULATION.size() -1) /2);
//        System.out.println("BREEDING PAIRS: " + breeding);


        //use the parent solutions as a breeding pool
        //create a new child to replace all the weaker solutions
        //@param POPULATION_SIZE is always 50 - we deleted the weaker solutions in hte previous step



        //the 2 best parents are at the end of the list -- try to replace the worst gene (first of the list)
        ArrayList<Chromosome> Parents = new ArrayList<Chromosome>();
        Chromosome Child = CrossOver(POPULATION.get(POPULATION.size() -1), POPULATION.get(POPULATION.size() -2));
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

        if (Child.GetFitnessGrade() > POPULATION.get(0).GetFitnessGrade())
            POPULATION.set(0,Child);

//        for (int i = 0; i < breeding; i++) {
//            Index1 = gen.nextInt(POPULATION.size());
//            Index2 = gen.nextInt(POPULATION.size());
//
//            while (Index1 == Index2) {
//                Index2 = gen.nextInt(POPULATION.size());
//            }
//
//            //pop the 2 parents we will add either both back, or the stronger one and the new stronger child
//            ArrayList<Chromosome> NewPair = CrossOver(POPULATION.get(Index1), POPULATION.get(Index2));
//
//
//            //check the 2 new chromosomes if goal
//            if (!CheckIfGoalFound(NewPair)) {
//
//                //if we didn't find hte goal state then mutate
//                NewPair.forEach((c) -> {
//                    double rand = Math.random();
//                    if (rand >= mkx) {
//
//                        Mutate(c);  //instead of removing and reading from our pairs list - just change state if we wanna mutate
//                    }
//
//                });
//            }
//
////            System.out.println("ADDING STATES: \n\t" + NewPair.get(0).GetState() + "\n\t" + NewPair.get(1).GetState());
//
//            POPULATION.set(Index1, NewPair.get(0));
//            POPULATION.set(Index2, NewPair.get(0));
//        }


    }


    public Chromosome CrossOver(Chromosome p1, Chromosome p2) {
        ArrayList<Chromosome> NewPair = new ArrayList<Chromosome>();

        Random gen = new Random();
        int cxval = 0;
        while (cxval == 0) { cxval = gen.nextInt(p1.GetState().size()); }

        ArrayList<Integer> ChildState = new ArrayList<>();
        System.out.println("CROSSOVER " + cxval + " ----> p1: " + p1.GetState());
        System.out.println("CROSSOVER " + cxval + " ----> p2: " + p2.GetState());

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

        System.out.println("CROSSOVER " + cxval + " -> child: " + ChildState);
        return new Chromosome(ChildState.size(), ChildState);

//        if (p1.GetFitnessGrade() > p2.GetFitnessGrade()) {
//
//            ReturnParent = p1;
//            if (p2.GetFitnessGrade() < ReturnChild.GetFitnessGrade() && ReturnChild.GetState() != p1.GetState()) {
//
//                NewPair.add(ReturnChild);
//            }
//            else {
//                NewPair.add(p2);
//            }
//        }
//        else {  //p2 is greater, so log at least this child
//
//            ReturnParent = p2;
//            if (p1.GetFitnessGrade() < ReturnChild.GetFitnessGrade() && ReturnChild.GetState() != p2.GetState()) {
//                NewPair.add(ReturnChild);
//            }
//            else {
//                NewPair.add(p1);
//            }
//
//        }
//
//        NewPair.add(ReturnParent);
//        return NewPair;
    }


    //we are mutating the state inside of here
    public void Mutate(Chromosome c) {
        Random gen = new Random();
        int c1 = gen.nextInt(c.GetState().size());
        int c2 = c1;

        while (c2 == c1) {
            c2 = gen.nextInt(c.GetState().size());
        }

        System.out.println("chromosome before mutation: " + c.GetState());


        ArrayList<Integer> swapstate = c.GetState();
        Collections.swap(swapstate, c1, c2);
        c.SetState(swapstate);

        System.out.println("chromosome after mutating points " + c1 + " and " + c2 + " :-> " + c.GetState());
 //       return c;
    }

}
